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
        this.server = new SocketIOServer(config);
        this.server.addConnectListener(client -> System.out.println("Client connected: " + client.getSessionId()));
        this.server.addDisconnectListener(client -> System.out.println("Client disconnected: " + client.getSessionId()));
        this.server.start(); // Démarrer immédiatement
        return this.server;
    }

    @PreDestroy
    public void stopSocketIOServer() {
        if (this.server != null) {
            this.server.stop();
            System.out.println("Socket.IO server stopped");
        }
    }
}