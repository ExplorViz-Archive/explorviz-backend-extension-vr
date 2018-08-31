package net.explorviz.extension.vr.main;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/extension/dummy")
public class Application extends ResourceConfig {

	public Application() {

		// register DI
		register(new ExtensionDependencyInjectionBinder());

		// Enable CORS
		register(CORSResponseFilter.class);
	}
}
