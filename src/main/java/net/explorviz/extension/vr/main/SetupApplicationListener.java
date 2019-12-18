package net.explorviz.extension.vr.main;

import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.LoggerFactory;

@WebListener
public class SetupApplicationListener implements ApplicationEventListener {

  private static final Logger LOGGER =
      (Logger) LoggerFactory.getLogger(SetupApplicationListener.class);

  @Override
  public void onEvent(final ApplicationEvent event) {

    // After this type, CDI (e.g. injected LandscapeExchangeService) has been
    // fullfilled
    final Type t = Type.INITIALIZATION_FINISHED;


    if (event.getType().equals(t)) {
      startExtension();
    }

  }

  @Override
  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return null;
  }

  private void startExtension() {
    LOGGER.info("* * * * * * * * * * * * * * * * * * *\n");
    LOGGER.info("VR Extension Servlet initialized.\n");
    LOGGER.info("* * * * * * * * * * * * * * * * * * *");

  }

  public void contextDestroyed(final ServletContextEvent servletContextEvent) {
    // Nothing to dispose
  }

}
