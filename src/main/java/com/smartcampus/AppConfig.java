package com.smartcampus;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

// app config for jax-rs
@ApplicationPath("/api/v1")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        // register all resources inside com.smartcampus package
        packages("com.smartcampus");
    }
}
