//package com.twd.BfiTradingApplication.config;
//
//import com.corundumstudio.socketio.SocketIOServer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import jakarta.annotation.PreDestroy;
//
//@Configuration
//public class SocketIOConfig {
//
//    @Value("${socket.host:localhost}")
//    private String host;
//
//    @Value("${socket.port:9092}")
//    private int port;
//
//    private SocketIOServer server;
//
//    @Bean
//    public SocketIOServer socketIOServer() {
//        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
//        config.setHostname(host);
//        config.setPort(port);
//        config.setOrigin("http://localhost:4200"); // Allow Angular frontend
//        config.setAllowCustomRequests(true);
//
//        this.server = new SocketIOServer(config);
//
//        // Log connections
//        server.addConnectListener(client -> {
//            String token = client.getHandshakeData().getSingleUrlParam("token");
//            System.out.println("Client connected: " + client.getSessionId() + ", Token: " + (token != null ? "Provided" : "None"));
//        });
//
//        // Handle room joining
//        server.addEventListener("join", String.class, (client, room, ackSender) -> {
//            if (room != null && room.startsWith("user-")) {
//                client.joinRoom(room);
//                System.out.println("Client " + client.getSessionId() + " joined room: " + room);
//            } else {
//                System.out.println("Invalid room join request from client " + client.getSessionId() + ": " + room);
//            }
//        });
//
//        // Log disconnections
//        server.addDisconnectListener(client -> {
//            System.out.println("Client disconnected: " + client.getSessionId());
//        });
//
//        server.start();
//        System.out.println("Socket.IO server started on " + host + ":" + port);
//        return server;
//    }
//
//    @PreDestroy
//    public void stopSocketIOServer() {
//        if (this.server != null) {
//            server.stop();
//            System.out.println("Socket.IO server stopped");
//        }
//    }
//}





package com.twd.BfiTradingApplication.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

@Configuration
public class SocketIOConfig {

    @Value("${socket.host:localhost}")
    private String host;

    @Value("${socket.port:9092}")
    private int port;

    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setOrigin("http://localhost:4200"); // Match Angular frontend
        config.setAllowCustomRequests(true);

        this.server = new SocketIOServer(config);

        // Log connections
        server.addConnectListener(client -> {
            String token = client.getHandshakeData().getSingleUrlParam("token");
            System.out.println("Client connected: " + client.getSessionId() + ", Token: " + (token != null ? token : "None"));
        });

        // Handle room joining
        server.addEventListener("join", String.class, (client, room, ackSender) -> {
            if (room != null && room.startsWith("user-")) {
                client.joinRoom(room);
                System.out.println("Client " + client.getSessionId() + " joined room: " + room);
            } else {
                System.out.println("Invalid room join request from client " + client.getSessionId() + ": " + room);
            }
        });

        // Log disconnections
        server.addDisconnectListener(client -> {
            System.out.println("Client disconnected: " + client.getSessionId());
        });

        server.start();
        System.out.println("Socket.IO server started on " + host + ":" + port);
        return server;
    }

    @PreDestroy
    public void stopSocketIOServer() {
        if (this.server != null) {
            server.stop();
            System.out.println("Socket.IO server stopped");
        }
    }
}
