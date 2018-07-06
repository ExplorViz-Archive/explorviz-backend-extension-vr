package net.explorviz.extension.vr.main;

import java.net.InetSocketAddress;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

public class WebsocketServer extends WebSocketServer {

	private static final int TCP_PORT = 4444;

	private final HashMap<Long, WebSocket> conns;

	private static long idCounter = 0;

	public WebsocketServer() {
		super(new InetSocketAddress(TCP_PORT));
		conns = new HashMap<>();
		System.out.println("Websocket constructed");
	}

	@Override
	public void onOpen(final WebSocket conn, final ClientHandshake handshake) {
		final long clientID = createID();
		conns.put(clientID, conn);
		System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
		final JSONObject messageID = new JSONObject();
		messageID.put("id", clientID);
		conn.send(messageID.toString());
	}

	@Override
	public void onClose(final WebSocket conn, final int code, final String reason, final boolean remote) {
		final long id = getWebSocketByID(conn);
		if (id != -1)
			conns.remove(id);
		System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onMessage(final WebSocket conn, final String message) {
		System.out.println("Message from client: " + message);
	}

	private static synchronized long createID() {
		return idCounter++;
	}

	private long getWebSocketByID(final WebSocket conn) {
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
			conns.remove(conn);
			// do some thing if required
		}
		System.out.println("ERROR from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}

}