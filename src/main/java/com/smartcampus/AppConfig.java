package com.smartcampus;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS application configuration.
 */
@ApplicationPath("/api/v1")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        // Register resources and providers in the com.smartcampus package
        packages("com.smartcampus");
    }
}
