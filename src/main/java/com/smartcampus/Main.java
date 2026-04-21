package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

// main class to start the embedded grizzly server
public class Main {
    // base url for the server
    public static final String BASE_URI = "http://localhost:9090/";

    // starts the server with jersey resources
    public static HttpServer startServer() {
        // setting up resource config to scan classes
        final ResourceConfig rc = new AppConfig();

        // create and start the server
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) {
        try {
            final HttpServer server = startServer();
            System.out.println(String.format("Jersey app started with endpoints available at "
                    + "%sapi/v1\nHit Ctrl-C to stop it...", BASE_URI));
            
            // keep server running until stopped
            Thread.currentThread().join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
