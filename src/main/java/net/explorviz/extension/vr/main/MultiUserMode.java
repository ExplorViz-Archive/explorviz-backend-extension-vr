package net.explorviz.extension.vr.main;

import java.awt.Color;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import net.explorviz.extension.vr.model.ApplicationModel;
import net.explorviz.extension.vr.model.BaseModel;
import net.explorviz.extension.vr.model.HighlightingModel;
import net.explorviz.extension.vr.model.UserModel;
import net.explorviz.extension.vr.model.UserModel.State;
import net.explorviz.shared.config.helper.PropertyHelper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for multi user experience. Contains a WebSocket for communication with clients.
 *
 * @author Daniel König & Malte Hansen
 *
 */
public class MultiUserMode extends WebSocketServer implements Runnable {

  private Thread multiUserThread; // thread which e.g. sends messages to users

  private static int DEFAULT_PORT = 4444; // default port of websocket

  private final HashMap<String, WebSocket> conns; // maps userID to the corresponding socket
                                                  // connection
  private final HashMap<String, UserModel> users; // maps userID to the corresponding user model
  private final BaseModel landscape; // only containing positional information about landscape
  private static boolean landscapePosChanged = false; // tells whether a user already manipulated
                                                      // landscape
  private final HashMap<String, Boolean> systemState; // tells if a system (systemID) is
                                                      // opened/closed
  private final HashMap<String, Boolean> nodeGroupState; // tells if a nodegroup (nodegroupID) is
                                                         // opened/closed
  private final HashMap<String, ApplicationModel> apps; // maps applicationID to the application
                                                        // model
  private static boolean running = true; // tells if multi-user mode is active
  private final HashMap<String, JSONArray> queues; // maps userID to corresponding message queue

  private static final Logger LOGGER = LoggerFactory.getLogger(MultiUserMode.class); // used for
                                                                                     // console logs

  private static int getTCPPort() {
    try {
      return PropertyHelper.getIntegerProperty("vr.port");
    } catch (final NumberFormatException e) {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info(
            "ATTENTION: Using default port " + DEFAULT_PORT + ". Check explorviz.properties file.",
            e);
      }
    }
    return DEFAULT_PORT;
  }

  public void run2() {
    init();

    final int fps = 90; // number of update per second.
    final double ticksPerSecond = 1000000000 / fps;
    double delta = 0;
    long now;
    long lastTime = java.lang.System.nanoTime();

    while (running) {
      now = java.lang.System.nanoTime();
      delta += (now - lastTime) / ticksPerSecond;
      lastTime = now;

      if (delta >= 1) {
        tick();
        delta--;
      }
    }
  }

  public static void main(final String[] args) {
    final MultiUserMode multiUserMode = new MultiUserMode();
    multiUserMode.start();
  }

  private void init() {}

  private void tick() {
    synchronized (queues) {
      synchronized (conns) {
        for (final String userID : queues.keySet()) {
          if (conns.containsKey(userID)) {
            final JSONArray queue = queues.get(userID);
            if (queue.length() > 0) {
              final WebSocket conn = conns.get(userID);
              try {
                conn.send(queue.toString());
              } catch (final Exception e) {
                disconnectUser(conn);
              }

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
    super(new InetSocketAddress(getTCPPort()));
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

    /*
     * final ExtensionAPIImpl coreAPI = ExtensionAPIImpl.getInstance(); final Landscape landscape =
     * coreAPI.getLatestLandscape();
     *
     * final List<System> landscapeSystems = landscape.getSystems();
     *
     * // copy ids of systems and nodegroups to Hashmaps and initialize open state with // true for
     * (final System LandscapeSystem : landscapeSystems) { systemState.put(LandscapeSystem.getId(),
     * false); final List<NodeGroup> nodeGroups = LandscapeSystem.getNodeGroups(); for (final
     * NodeGroup nodeModel : nodeGroups) { nodeGroupState.put(nodeModel.getId(), false); } }
     */

  }

  private void sendLandscape(final String userID) {
    final JSONArray systemArray = new JSONArray();
    for (final Map.Entry<String, Boolean> entry : systemState.entrySet()) {
      final JSONObject systemObj = new JSONObject();
      systemObj.put("id", entry.getKey());
      systemObj.put("opened", entry.getValue());
      systemArray.put(systemObj);
    }

    final JSONArray nodeGroupArray = new JSONArray();
    for (final Map.Entry<String, Boolean> entry : nodeGroupState.entrySet()) {
      final JSONObject nodeGroupObj = new JSONObject();
      nodeGroupObj.put("id", entry.getKey());
      nodeGroupObj.put("opened", entry.getValue());
      nodeGroupArray.put(nodeGroupObj);
    }

    final JSONArray appArray = new JSONArray();
    for (final Map.Entry<String, ApplicationModel> appEntry : apps.entrySet()) {
      final ApplicationModel app = appEntry.getValue();

      final JSONObject appObj = new JSONObject();
      appObj.put("id", app.getId());
      appObj.put("position", app.getPosition());
      appObj.put("quaternion", app.getQuaternion());

      final JSONArray componentArray = new JSONArray();
      for (final String componentID : app.getOpenComponents()) {
        componentArray.put(componentID);
      }

      final JSONArray componentHighlightedArray = new JSONArray();
      for (final UserModel user : this.users.values()) {
        if (user.hasHighlightedEntity()) {
          final HighlightingModel highlighted = user.getHighlightedEntity();
          final JSONObject highlightingObj = new JSONObject();
          highlightingObj.put("event", "hightlighting_update");
          highlightingObj.put("time", java.lang.System.currentTimeMillis());
          highlightingObj.put("userID", user.getId());
          highlightingObj.put("appID", highlighted.getHighlightedApp());
          highlightingObj.put("entityType", highlighted.getEntityType());
          highlightingObj.put("entityID", highlighted.getHighlightedEntity());
          highlightingObj.put("isHighlighted", true);
          componentHighlightedArray.put(highlightingObj);
        }
      }

      appObj.put("openComponents", componentArray);
      appObj.put("highlightedComponents", componentHighlightedArray);
      appArray.put(appObj);

    }

    final JSONObject landscapeObj = new JSONObject();
    landscapeObj.put("event", "landscape");
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

  private void sendHighlighting(final String userID) {
    for (final UserModel user : users.values()) {
      if (user.hasHighlightedEntity()) {
        final HighlightingModel highlighted = user.getHighlightedEntity();
        final JSONObject highlightingObj = new JSONObject();
        highlightingObj.put("event", "hightlighting_update");
        highlightingObj.put("time", java.lang.System.currentTimeMillis());
        highlightingObj.put("userID", user.getId());
        highlightingObj.put("appID", highlighted.getHighlightedApp());
        highlightingObj.put("entityType", highlighted.getEntityType());
        highlightingObj.put("entityID", highlighted.getHighlightedEntity());
        highlightingObj.put("isHighlighted", true);
        enqueue(userID, highlightingObj);
      }
    }
  }

  private void enqueue(final String userID, final JSONObject message) {
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
    // if (noUsers)
    // initializeLandscapeModel();

    final UserModel user = new UserModel();
    final String clientID = user.getId();
    user.setState(State.CONNECTING);
    user.setTimeOfLastMessage(java.lang.System.currentTimeMillis());

    synchronized (conns) {
      conns.put(clientID, conn);
    }
    synchronized (users) {
      users.put(clientID, user);
    }

    LOGGER
        .info("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());

    // inform users about new user with ID
    userConnecting(user);
  }

  private void userConnecting(final UserModel user) {
    final String userID = user.getId();
    // message other user about the new user
    final JSONObject userConnectingMessage = new JSONObject();
    userConnectingMessage.put("event", "user_connecting");
    userConnectingMessage.put("id", userID);
    broadcastAllBut(userConnectingMessage, userID);

    final JSONObject selfConnectingMessage = new JSONObject();

    selfConnectingMessage.put("id", userID);
    selfConnectingMessage.put("event", "self_connecting");
    final Color color = user.getColor();
    final JSONArray colorArray = new JSONArray(
        new double[] {color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0});
    selfConnectingMessage.put("color", colorArray);

    enqueue(userID, selfConnectingMessage);

  }

  private void userConnected(final String userID, final String name) {
    final UserModel user;
    synchronized (users) {
      user = users.get(userID);
    }

    final Color color = user.getColor();
    final JSONArray colorArray = new JSONArray(
        new double[] {color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0});

    // message other users about the new user
    final JSONObject connectedMessage = new JSONObject();
    connectedMessage.put("event", "user_connected");
    connectedMessage.put("name", user.getUserName());
    connectedMessage.put("id", userID);
    connectedMessage.put("color", colorArray);

    broadcastAllBut(connectedMessage, userID);

    // send user all other users' id and name
    final JSONObject initMessage = new JSONObject();
    final JSONArray usersArray = new JSONArray();
    initMessage.put("event", "self_connected");
    synchronized (users) {
      for (final UserModel userData : users.values()) {
        if (userData.getState() == State.CONNECTED || userData.getState() == State.SPECTATING) {
          final JSONObject userObject = new JSONObject();
          userObject.put("id", userData.getId());
          userObject.put("name", userData.getUserName());
          final Color userColor = userData.getColor();
          final JSONArray userColorArray = new JSONArray(new double[] {userColor.getRed() / 255.0,
              userColor.getGreen() / 255.0, userColor.getBlue() / 255.0});
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
    // sendHighlighting(userID);

    user.setState(State.CONNECTED);
  }

  private void updateOpenApp(final JSONObject JSONmessage) {
    final String appID = JSONmessage.getString("id");
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
    final double[] position = new double[jsonPosition.length()];
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

    final JSONArray jsonOffset = JSONmessage.getJSONArray("offset");
    final double[] offset = new double[jsonOffset.length()];
    for (int p = 0; p < jsonOffset.length(); p++) {
      offset[p] = jsonOffset.getFloat(p);
    }
    landscape.setPosition(offset);

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
   * @param msg The message which all users should receive
   */
  public void broadcastAll(final JSONObject msg) {
    synchronized (users) {
      for (final String userID : users.keySet()) {
        final State state = users.get(userID).getState();
        if (state == State.CONNECTED || state == State.SPECTATING)
          enqueue(userID, msg);
      }
    }
  }

  /**
   * Sends a message (usually JSON as a string) to all but one connected users
   *
   * @param msg The message which all users should receive
   * @param userID The user that should be excluded from the message
   */
  public void broadcastAllBut(final JSONObject msg, final String userID) {
    synchronized (users) {
      for (final String id : users.keySet()) {
        final State state = users.get(id).getState();
        if (userID != id && (state == State.CONNECTED || state == State.SPECTATING))
          enqueue(id, msg);
      }
    }
  }

  @Override
  public void onClose(final WebSocket conn, final int code, final String reason,
      final boolean remote) {
    disconnectUser(conn);

    // // LOGGER.info("Closed connection to " +
    // conn.getRemoteSocketAddress().getAddress().getHostAddress());
  }

  @Override
  public void onMessage(final WebSocket conn, final String message) {
    new Thread(() -> {
      final JSONArray queue = new JSONArray(message);
      for (int i = 0; i < queue.length(); i++) {
        final JSONObject JSONmessage = queue.getJSONObject(i);
        final String event = JSONmessage.getString("event");
        final String id = getIDByWebSocket(conn);
        final UserModel user;

        synchronized (users) {
          user = users.get(id);
        }
        if (user == null) {
          return;
        }

        user.setTimeOfLastMessage(java.lang.System.currentTimeMillis());

        if (!event.equals("user_positions")) {
          LOGGER.info("Event: " + message.toString());
        }

        switch (event) {
          case "user_positions":
            JSONmessage.put("id", id);
            broadcastAllBut(JSONmessage, id);
            break;
          case "connect_request":
            final String name = JSONmessage.getString("name");
            user.setUserName(name);
            userConnected(id, name);
            break;
          case "user_controllers":
            onUserControllers(JSONmessage, user);
            broadcastAllBut(JSONmessage, id);
            break;
          case "disconnect_request":
            disconnectUser(conn);
            break;
          case "landscape_position":
            updateLandscapePosition(JSONmessage);
            broadcastAllBut(JSONmessage, id);
            break;
          case "system_update":
            final String systemID = JSONmessage.getString("id");
            final Boolean systemOpened = JSONmessage.getBoolean("isOpen");
            systemState.put(systemID, systemOpened);

            // forward update from user to all other users
            broadcastAllBut(JSONmessage, id);
            break;
          case "nodegroup_update":
            final String nodeGroupID = JSONmessage.getString("id");
            final Boolean nodeGroupOpened = JSONmessage.getBoolean("isOpen");
            nodeGroupState.put(nodeGroupID, nodeGroupOpened);

            // forward update from user to all other users
            broadcastAllBut(JSONmessage, id);
            break;
          case "app_translated":
            broadcastAllBut(JSONmessage, id);
            break;
          case "app_opened":
            updateOpenApp(JSONmessage);
            broadcastAllBut(JSONmessage, id);
            break;
          case "app_closed":
            final String applicationID = JSONmessage.getString("id");
            apps.remove(applicationID);
            broadcastAllBut(JSONmessage, id);
            break;
          case "app_grabbed":
            grabbingApp(JSONmessage, id);
            break;
          case "app_released":
            updateOpenApp(JSONmessage);
            apps.get(JSONmessage.getString("id")).setGrabbed(false);
            broadcastAllBut(JSONmessage, id);
            break;
          case "component_update":
            if (JSONmessage.getBoolean("isFoundation")) {
              apps.get(JSONmessage.getString("appID")).closeAllComponents();
            } else if (JSONmessage.getBoolean("isOpened")) {
              apps.get(JSONmessage.getString("appID"))
                  .openComponent(JSONmessage.getString("componentID"));
            } else {
              apps.get(JSONmessage.getString("appID"))
                  .closeComponent(JSONmessage.getString("componentID"));
            }

            broadcastAllBut(JSONmessage, id);
            break;
          case "hightlighting_update":
            JSONmessage.put("userID", id);
            updateHighlighting(JSONmessage, id);
            broadcastAllBut(JSONmessage, id);
            break;
          case "spectating_update":
            if (JSONmessage.getBoolean("isSpectating"))
              users.get(id).setState(State.SPECTATING);
            else
              users.get(id).setState(State.CONNECTED);
            broadcastAllBut(JSONmessage, id);
            break;
        }
      }
    }).start();
  }

  private void updateHighlighting(final JSONObject msg, final String userID) {
    final UserModel user = users.get(userID);
    final boolean isHighlighted = msg.getBoolean("isHighlighted");
    if (!isHighlighted) {
      user.setHighlighted(false);
      return;
    }
    final String appID = msg.getString("appID");
    final String entityType = msg.getString("entityType");
    final String entityID = msg.getString("entityID");

    // Overwrite highlighting of other users (if they highlighted same entity)
    for (final UserModel otherUser : users.values()) {
      if (otherUser.hasHighlightedEntity()
          && otherUser.getHighlightedEntity().getHighlightedApp().equals(appID)
          && otherUser.getHighlightedEntity().getHighlightedEntity().equals(entityID)) {
        otherUser.setHighlighted(false);
      }
    }
    user.setHighlightedEntity(isHighlighted, appID, entityType, entityID);
  }

  private void onUserControllers(final JSONObject JSONmessage, final UserModel user) {
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
      final JSONObject controllers = JSONmessage.getJSONObject("disconnect");
      if (controllers.has("controller1")) {
        user.getController1().setName(null);
      }
      if (controllers.has("controller2")) {
        user.getController2().setName(null);
      }
    }
    JSONmessage.put("id", user.getId());
  }

  private void grabbingApp(final JSONObject msg, final String userID) {
    msg.put("userID", userID);
    final String appID = msg.getString("appID");
    final boolean isGrabbed = apps.get(appID).isGrabbed();
    if (isGrabbed) {
      // TODO: send error msg to user
    } else {
      apps.get(appID).setGrabbed(true);
      apps.get(appID).setGrabbedByUser(userID);
      broadcastAllBut(msg, userID);
    }
  }

  private void disconnectUser(final WebSocket conn) {
    if (conn != null) {
      final String clientID = getIDByWebSocket(conn);

      // check if user already disconnected
      if (clientID == "<undefined>")
        return;

      for (final ApplicationModel app : apps.values()) {
        app.setUnboundByUser(clientID);
      }

      if (conn.isOpen())
        conn.close();

      final JSONObject disconnectMessage = new JSONObject();
      disconnectMessage.put("event", "user_disconnect");
      disconnectMessage.put("id", clientID);
      broadcastAllBut(disconnectMessage, clientID);

      synchronized (conns) {
        conns.remove(clientID);
      }
      synchronized (users) {
        final UserModel user = users.get(clientID);
        if (user != null) {
          // remove highlighting for others
          if (user.hasHighlightedEntity()) {
            final HighlightingModel highlighted = user.getHighlightedEntity();
            final JSONObject highlightingObj = new JSONObject();
            highlightingObj.put("event", "hightlighting_update");
            highlightingObj.put("time", java.lang.System.currentTimeMillis());
            highlightingObj.put("userID", clientID);
            highlightingObj.put("appID", highlighted.getHighlightedApp());
            highlightingObj.put("entityType", highlighted.getEntityType());
            highlightingObj.put("entityID", highlighted.getHighlightedEntity());
            highlightingObj.put("isHighlighted", false);
            this.broadcastAllBut(highlightingObj, clientID);
          }

          user.removeColor();
          users.remove(clientID);
        }
      }
      synchronized (queues) {
        queues.remove(clientID);
      }

    } else {
      LOGGER.info("CONNECTION IS NULL");
    }
  }

  private String getIDByWebSocket(final WebSocket conn) {
    for (final String id : conns.keySet()) {
      if (conns.get(id) == conn)
        return id;
    }
    return "<undefined>";
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
