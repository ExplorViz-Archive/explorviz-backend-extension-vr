package net.explorviz.extension.vr.main;

import java.net.InetSocketAddress;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import net.explorviz.extension.vr.model.UserModel;

/**
 * Main class for multi user experience. Contains a WebSocket for communication
 * with clients.
 *
 * @author Daniel König & Malte Hansen
 *
 */
public class MultiUserMode extends WebSocketServer implements Runnable {

	private Thread multiUserThread;
	private static final int TCP_PORT = 4444;
	private final HashMap<Long, WebSocket> conns;
	private final HashMap<Long, UserModel> users;

	public void run2() {
		System.out.println("MultiUserMode: Main loop entered");
	}

	@Override
	public void start() {
		super.start();
		System.out.println("MultiUserMode: starting");

		if (multiUserThread == null) {
			multiUserThread = new Thread(this::run2);
			multiUserThread.start();
		}

	}

	MultiUserMode() {
		super(new InetSocketAddress(TCP_PORT));
		conns = new HashMap<>();
		users = new HashMap<>();
		System.out.println("MultiUserMode: Websocket constructed");
	}

	@Override
	public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
		final UserModel user = new UserModel();
		final long clientID = user.getId();
		user.setState("connecting");

		conns.put(clientID, conn);
		users.put(clientID, user);

		System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());

		// inform users about new user with ID
		userConnecting(clientID);
	}

	private void userConnecting(final long userID) {
		// message other user about the new user
		final JSONObject connectingMessage = new JSONObject();
		connectingMessage.put("event", "user_connecting");
		connectingMessage.put("id", userID);
		broadcastAllBut(connectingMessage.toString(), userID);

		connectingMessage.remove("event");
		connectingMessage.put("event", "connecting");
		final WebSocket conn = conns.get(userID);
		conn.send(connectingMessage.toString());

	}

	private void userConnected(final long userID, final String name, final String device) {
		users.get(userID).setState("connected");
		// message other user about the new user
		final JSONObject connectedMessage = new JSONObject();
		final JSONObject user = new JSONObject();
		connectedMessage.put("event", "user_connected");
		user.put("name", users.get(userID).getUserName());
		user.put("id", userID);
		user.put("device", device);
		connectedMessage.put("user", user);
		broadcastAllBut(connectedMessage.toString(), userID);

		// send user their id and all other users' id and name
		final JSONObject initMessage = new JSONObject();
		final JSONArray usersArray = new JSONArray();
		initMessage.put("event", "connected");
		initMessage.put("id", userID);
		for (final UserModel userData : users.values()) {
			if (userData.getState().equals("connected")) {
				final JSONObject userObject = new JSONObject();
				userObject.put("id", userData.getId());
				userObject.put("name", userData.getUserName());
				userObject.put("device", userData.getDevice());
				usersArray.put(userObject);
			}
		}
		initMessage.put("users", usersArray);
		final WebSocket conn = conns.get(userID);
		conn.send(initMessage.toString());

	}

	/**
	 * Sends a message (usually JSON as a string) to all connected users
	 *
	 * @param msg
	 *            The message which all users should receive
	 */
	public void broadcastAll(final String msg) {
		for (final WebSocket conn : conns.values()) {
			conn.send(msg);
		}
	}

	/**
	 * Sends a message (usually JSON as a string) to all but one connected users
	 *
	 * @param msg
	 *            The message which all users should receive
	 * @param userID
	 *            The user that should be excluded from the message
	 */
	public void broadcastAllBut(final String msg, final long userID) {
		for (final WebSocket conn : conns.values()) {
			if (conns.get(userID) != conn)
				conn.send(msg);
		}
	}

	@Override
	public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
		final long id = getIDByWebSocket(conn);
		if (id != -1) {
			conns.remove(id);
			users.remove(id);
		}
		System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onMessage(final WebSocket conn, final String message) {
		// System.out.println("Message from client: " + message);
		new Thread(() -> {
			JSONObject JSONmessage = new JSONObject(message);
			final String event = JSONmessage.getString("event");
			final long id = getIDByWebSocket(conn);

			if (event.equals("position")) {
				JSONmessage.put("id", id);
				broadcastAllBut(JSONmessage.toString(), id);
				JSONmessage = null;
			} else if (event.equals("request_connect")) {
				final String name = JSONmessage.getString("name");
				final String device = JSONmessage.getString("device");
				final UserModel user = users.get(id);
				user.setUserName(name);
				user.setDevice(device);
				userConnected(id, name, device);
			}
		}).start();

	}

	private long getIDByWebSocket(final WebSocket conn) {
		for (final long id : conns.keySet()) {
			if (conns.get(id) == conn)
				return id;
		}
		return -1;
	}

	@Override
	public void onError(final WebSocket conn, final Exception ex) {
		// ex.printStackTrace();
		if (conn != null) {
			final long clientID = getIDByWebSocket(conn);
			conns.remove(clientID);
		}
		System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onStart() {
		System.out.println("Server has started");
	}

}
