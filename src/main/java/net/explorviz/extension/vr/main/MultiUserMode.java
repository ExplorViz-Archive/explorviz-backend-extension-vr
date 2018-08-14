package net.explorviz.extension.vr.main;

import java.awt.Color;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.extension.vr.model.ApplicationModel;
import net.explorviz.extension.vr.model.BaseModel;
import net.explorviz.extension.vr.model.UserModel;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.model.landscape.System;

/**
 * Main class for multi user experience. Contains a WebSocket for communication
 * with clients.
 *
 * @author Daniel König & Malte Hansen
 *
 */
public class MultiUserMode extends WebSocketServer implements Runnable {

	private Thread multiUserThread;
	// private final ArrayList<SystemModel> systems = new ArrayList<>();
	private static final int TCP_PORT = 4444;
	private final HashMap<Long, WebSocket> conns;
	private final HashMap<Long, UserModel> users;
	private final BaseModel landscape;
	private static boolean landscapePosChanged = false;
	private final HashMap<Long, Boolean> systemState;
	private final HashMap<Long, Boolean> nodeGroupState;
	private final HashMap<Long, ApplicationModel> apps;
	private static boolean running = true;
	private final HashMap<Long, JSONArray> queues;

	private static final Logger LOGGER = LoggerFactory.getLogger(MultiUserMode.class);

	public void run2() {
		init();

		final int fps = 90; // number of update per second.
		final double tickPerSecond = 1000000000 / fps;
		double delta = 0;
		long now;
		long lastTime = java.lang.System.nanoTime();

		while (running) {
			now = java.lang.System.nanoTime();
			delta += (now - lastTime) / tickPerSecond;
			lastTime = now;

			if (delta >= 1) {
				tick();
				delta--;
			}
		}
	}

	private void init() {
		// initializeLandscapeModel();
	}

	private void tick() {
		synchronized (queues) {
			synchronized (conns) {
				for (final long userID : queues.keySet()) {
					if (conns.containsKey(userID)) {
						final JSONArray queue = queues.get(userID);
						if (queue.length() > 0) {
							final WebSocket conn = conns.get(userID);
							conn.send(queue.toString());
						}
					}
				}
				queues.clear();
			}
		}
	}

	@Override
	public void start() {
		super.start();
		LOGGER.info("MultiUserMode: starting");

		if (multiUserThread == null) {
			multiUserThread = new Thread(this::run2);
			multiUserThread.start();
		}

	}

	MultiUserMode() {
		super(new InetSocketAddress(TCP_PORT));
		conns = new HashMap<>();
		users = new HashMap<>();
		queues = new HashMap<>();
		landscape = new BaseModel();
		systemState = new HashMap<>();
		nodeGroupState = new HashMap<>();
		apps = new HashMap<>();
		LOGGER.info("MultiUserMode: Websocket constructed");
	}

	private void initializeLandscapeModel() {
		LOGGER.info("Initialize landscape");
		// empty old Hashmaps for new incoming data
		landscapePosChanged = false;
		systemState.clear();
		nodeGroupState.clear();
		apps.clear();

		final ExtensionAPIImpl coreAPI = ExtensionAPIImpl.getInstance();
		final Landscape landscape = coreAPI.getLatestLandscape();

		final List<System> landscapeSystems = landscape.getSystems();

		// copy ids of systems and nodegroups to Hashmaps and initialize open state with
		// true
		for (final System LandscapeSystem : landscapeSystems) {
			systemState.put(LandscapeSystem.getId(), false);
			final List<NodeGroup> nodeGroups = LandscapeSystem.getNodeGroups();
			for (final NodeGroup nodeModel : nodeGroups) {
				nodeGroupState.put(nodeModel.getId(), false);
			}
		}

	}

	private void sendLandscape(final Long userID) {
		final JSONArray systemArray = new JSONArray();
		for (final Map.Entry<Long, Boolean> entry : systemState.entrySet()) {
			final JSONObject systemObj = new JSONObject();
			systemObj.put("id", entry.getKey());
			systemObj.put("opened", entry.getValue());
			systemArray.put(systemObj);
		}

		final JSONArray nodeGroupArray = new JSONArray();
		for (final Map.Entry<Long, Boolean> entry : nodeGroupState.entrySet()) {
			final JSONObject nodeGroupObj = new JSONObject();
			nodeGroupObj.put("id", entry.getKey());
			nodeGroupObj.put("opened", entry.getValue());
			nodeGroupArray.put(nodeGroupObj);
		}

		final JSONArray appArray = new JSONArray();
		for (final Map.Entry<Long, ApplicationModel> appEntry : apps.entrySet()) {
			final ApplicationModel app = appEntry.getValue();

			final JSONObject appObj = new JSONObject();
			appObj.put("id", app.getId());
			appObj.put("position", app.getPosition());
			appObj.put("quaternion", app.getQuaternion());

			final JSONArray componentArray = new JSONArray();
			for (final Long componentID : app.getOpenComponents()) {
				componentArray.put(componentID);
			}
			appObj.put("openComponents", componentArray);
			appArray.put(appObj);

		}

		final JSONObject landscapeObj = new JSONObject();
		landscapeObj.put("event", "receive_landscape");
		landscapeObj.put("systems", systemArray);
		landscapeObj.put("nodeGroups", nodeGroupArray);
		landscapeObj.put("openApps", appArray);

		if (landscapePosChanged) {
			final JSONObject landscapePosObj = new JSONObject();
			landscapePosObj.put("position", landscape.getPosition());
			landscapePosObj.put("quaternion", landscape.getQuaternion());
			landscapeObj.put("landscape", landscapePosObj);
		}

		enqueue(userID, landscapeObj);
	}

	private void enqueue(final long userID, final JSONObject message) {
		synchronized (queues) {
			if (!queues.containsKey(userID)) {
				queues.put(userID, new JSONArray());
			}
			final JSONArray queue = queues.get(userID);
			queue.put(message);
		}
	}

	@Override
	public void onOpen(final WebSocket conn, final ClientHandshake handshake) {

		boolean noUsers = false;

		synchronized (users) {
			if (users.keySet().isEmpty()) {
				noUsers = true;
			}
		}

		// initialize landscape when first user connects
		if (noUsers)
			initializeLandscapeModel();

		final UserModel user = new UserModel();
		final long clientID = user.getId();
		user.setState("connecting");

		synchronized (conns) {
			conns.put(clientID, conn);
		}
		synchronized (users) {
			users.put(clientID, user);
		}

		LOGGER.info("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());

		// inform users about new user with ID
		userConnecting(clientID);
	}

	private void userConnecting(final long userID) {
		// message other user about the new user
		final JSONObject userConnectingMessage = new JSONObject();
		userConnectingMessage.put("event", "receive_user_connecting");
		userConnectingMessage.put("id", userID);
		broadcastAllBut(userConnectingMessage, userID);

		final JSONObject selfConnectingMessage = new JSONObject();

		selfConnectingMessage.put("id", userID);
		selfConnectingMessage.put("event", "receive_self_connecting");

		enqueue(userID, selfConnectingMessage);

	}

	private void userConnected(final long userID, final String name) {
		final UserModel user;
		synchronized (users) {
			user = users.get(userID);
		}
		// message other users about the new user
		final JSONObject connectedMessage = new JSONObject();
		final JSONObject userObj = new JSONObject();
		connectedMessage.put("event", "receive_user_connected");
		userObj.put("name", user.getUserName());
		userObj.put("id", userID);
		final Color color = user.getColor();
		final JSONArray colorArray = new JSONArray(new int[] { color.getRed(), color.getGreen(), color.getBlue() });
		userObj.put("color", colorArray);
		connectedMessage.put("user", userObj);
		broadcastAllBut(connectedMessage, userID);

		// send user all other users' id and name
		final JSONObject initMessage = new JSONObject();
		final JSONArray usersArray = new JSONArray();
		initMessage.put("event", "receive_self_connected");
		synchronized (users) {
			for (final UserModel userData : users.values()) {
				if (userData.getState().equals("connected")) {
					final JSONObject userObject = new JSONObject();
					userObject.put("id", userData.getId());
					userObject.put("name", userData.getUserName());
					final Color userColor = userData.getColor();
					final JSONArray userColorArray = new JSONArray(
							new int[] { userColor.getRed(), userColor.getGreen(), userColor.getBlue() });
					userObject.put("color", userColorArray);
					final JSONObject controllers = new JSONObject();
					controllers.put("controller1", userData.getController1().getName());
					controllers.put("controller2", userData.getController2().getName());
					userObject.put("controllers", controllers);
					usersArray.put(userObject);
				}
			}
		}
		initMessage.put("users", usersArray);
		enqueue(userID, initMessage);

		// send current state of landscape to new user
		sendLandscape(userID);

		user.setState("connected");
	}

	private void updateOpenApp(final JSONObject JSONmessage) {
		final Long appID = JSONmessage.getLong("id");
		ApplicationModel appModel;

		// add app to Hashmap or get app from Hashmap
		if (apps.containsKey(appID)) {
			appModel = apps.get(appID);
		} else {
			appModel = new ApplicationModel();
			appModel.setId(appID);
			apps.put(appID, appModel);
		}

		appModel.setOpen(true);

		final JSONArray jsonPosition = JSONmessage.getJSONArray("position");
		final float[] position = new float[jsonPosition.length()];
		for (int p = 0; p < jsonPosition.length(); p++) {
			position[p] = jsonPosition.getFloat(p);
		}
		appModel.setPosition(position);

		final JSONArray jsonQuaternion = JSONmessage.getJSONArray("quaternion");
		final float[] quaternion = new float[jsonQuaternion.length()];
		for (int q = 0; q < jsonQuaternion.length(); q++) {
			quaternion[q] = jsonQuaternion.getFloat(q);
		}
		appModel.setQuaternion(quaternion);
	}

	private void updateLandscapePosition(final JSONObject JSONmessage) {
		landscapePosChanged = true;

		final JSONArray jsonPosition = JSONmessage.getJSONArray("position");
		final float[] position = new float[jsonPosition.length()];
		for (int p = 0; p < jsonPosition.length(); p++) {
			position[p] = jsonPosition.getFloat(p);
		}
		landscape.setPosition(position);

		final JSONArray jsonQuaternion = JSONmessage.getJSONArray("quaternion");
		final float[] quaternion = new float[jsonQuaternion.length()];
		for (int q = 0; q < jsonQuaternion.length(); q++) {
			quaternion[q] = jsonQuaternion.getFloat(q);
		}
		landscape.setQuaternion(quaternion);
	}

	/**
	 * Sends a message (usually JSON as a string) to all connected users
	 *
	 * @param msg
	 *            The message which all users should receive
	 */
	public void broadcastAll(final JSONObject msg) {
		synchronized (users) {
			for (final long userID : users.keySet()) {
				if (users.get(userID).getState() == "connected")
					enqueue(userID, msg);
			}
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
	public void broadcastAllBut(final JSONObject msg, final long userID) {
		synchronized (users) {
			for (final long id : users.keySet()) {
				if (userID != id && users.get(id).getState() == "connected")
					enqueue(id, msg);
			}
		}
	}

	@Override
	public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
		final long id = getIDByWebSocket(conn);

		final JSONObject disconnectMessage = new JSONObject();
		disconnectMessage.put("event", "receive_user_disconnect");
		disconnectMessage.put("id", id);
		broadcastAllBut(disconnectMessage, id);

		if (id != -1) {
			synchronized (conns) {
				conns.remove(id);
			}
			synchronized (users) {
				users.remove(id);
			}
			synchronized (queues) {
				queues.remove(id);
			}
		}
		LOGGER.info("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onMessage(final WebSocket conn, final String message) {
		// LOGGER.info("Message from client: " + message);
		new Thread(() -> {
			final JSONArray queue = new JSONArray(message);
			for (int i = 0; i < queue.length(); i++) {
				final JSONObject JSONmessage = queue.getJSONObject(i);
				final String event = JSONmessage.getString("event");
				final long id = getIDByWebSocket(conn);
				final UserModel user;

				synchronized (users) {
					user = users.get(id);
				}

				switch (event) {
				case "receive_user_positions":
					// LOGGER.info("Positions from " + id + ": " + JSONmessage.toString());
					JSONmessage.put("id", id);
					broadcastAllBut(JSONmessage, id);
					break;
				case "receive_connect_request":
					LOGGER.info(JSONmessage.toString());
					final String name = JSONmessage.getString("name");
					user.setUserName(name);
					userConnected(id, name);
					break;
				case "receive_user_controllers":
					if (JSONmessage.has("connect")) {
						final JSONObject controllers = JSONmessage.getJSONObject("connect");
						if (controllers.has("controller1")) {
							user.getController1().setName(controllers.getString("controller1"));
						}
						if (controllers.has("controller2")) {
							user.getController2().setName(controllers.getString("controller2"));
						}
					}
					if (JSONmessage.has("disconnect")) {
						final JSONArray controllers = JSONmessage.getJSONArray("disconnect");
						for (int j = 0; j < controllers.length(); j++) {
							if (controllers.get(j) == "controller1") {
								user.getController1().setName(null);
							}
							if (controllers.get(j) == "controller2") {
								user.getController2().setName(null);
							}
						}
					}
					JSONmessage.put("id", id);
					broadcastAllBut(JSONmessage, id);
					break;
				case "receive_disconnect_request":
					disconnectUser(conn);
					break;
				case "receive_landscape_position":
					updateLandscapePosition(JSONmessage);
					broadcastAllBut(JSONmessage, id);
					break;
				case "receive_system_update":
					final Long systemID = JSONmessage.getLong("id");
					final Boolean systemOpened = JSONmessage.getBoolean("isOpen");
					systemState.put(systemID, systemOpened);

					// forward update from user to all other users
					broadcastAllBut(JSONmessage, id);
					// broadcastAllBut(JSONmessage, id);
					break;
				case "receive_nodegroup_update":
					final Long nodeGroupID = JSONmessage.getLong("id");
					final Boolean nodeGroupOpened = JSONmessage.getBoolean("isOpen");
					nodeGroupState.put(nodeGroupID, nodeGroupOpened);

					// forward update from user to all other users
					broadcastAllBut(JSONmessage, id);
					break;
				case "receive_app_opened":
					LOGGER.info(JSONmessage.toString());
					updateOpenApp(JSONmessage);
					broadcastAllBut(JSONmessage, id);
					break;
				case "receive_app_closed":
					LOGGER.info(JSONmessage.toString());
					final Long applicationID = JSONmessage.getLong("id");
					apps.remove(applicationID);
					broadcastAllBut(JSONmessage, id);
					break;
				case "receive_app_binded":
					bindingApp(JSONmessage, id);
					break;
				case "receive_app_released":
					LOGGER.info(JSONmessage.toString());
					updateOpenApp(JSONmessage);
					apps.get(JSONmessage.getLong("id")).setBound(false);
					broadcastAllBut(JSONmessage, id);
					break;
				case "receive_component_update":
					LOGGER.info(JSONmessage.toString());
					if (JSONmessage.getBoolean("isOpened")) {
						apps.get(JSONmessage.getLong("appID")).openComponent(JSONmessage.getLong("componentID"));
					} else {
						apps.get(JSONmessage.getLong("appID")).closeComponent(JSONmessage.getLong("componentID"));
					}
					broadcastAllBut(JSONmessage, id);
					break;
				case "receive_hightlight_update":
					LOGGER.info(JSONmessage.toString());
					broadcastAllBut(JSONmessage, id);
					break;
				}
			}
		}).start();
	}

	private void bindingApp(final JSONObject msg, final Long userID) {
		LOGGER.info(msg.toString());
		msg.put("userID", userID);
		final Long appID = msg.getLong("appID");
		final boolean isBound = apps.get(appID).isBound();
		if (isBound) {
			// TODO: send error msg to user
		} else {
			apps.get(appID).setBound(true);
			apps.get(appID).setBoundByUser(userID);
			broadcastAllBut(msg, userID);
		}
	}

	private void disconnectUser(final WebSocket conn) {
		if (conn != null) {
			final long clientID = getIDByWebSocket(conn);

			for (final ApplicationModel app : apps.values()) {
				app.setUnboundByUser(clientID);
			}

			if (conn.isOpen())
				conn.close();

			final JSONObject disconnectMessage = new JSONObject();
			disconnectMessage.put("event", "receive_user_disconnect");
			disconnectMessage.put("id", clientID);
			broadcastAllBut(disconnectMessage, clientID);

			synchronized (conns) {
				conns.remove(clientID);
			}
			synchronized (users) {
				users.remove(clientID);
			}
			synchronized (queues) {
				queues.remove(clientID);
			}

		} else {
			LOGGER.info("CONNECTION IS NULL");
		}
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
		ex.printStackTrace();
		disconnectUser(conn);
		LOGGER.info("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onStart() {
		LOGGER.info("Server has started");
	}

}
