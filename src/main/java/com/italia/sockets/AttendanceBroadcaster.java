package com.italia.sockets;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.json.JSONObject;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/attendancebroadcaster")
public class AttendanceBroadcaster {

	// Set of connected sessions
    //private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

 // Inside your class:
    private static final Set<Session> clients = new CopyOnWriteArraySet<>();
    
    @OnOpen
    public void onOpen(Session session) {
    	// Set idle timeout to 60 seconds. 
        // If no message (ping or data) is received for 60s, close the connection.
        //session.setMaxIdleTimeout(60000);
        
    	clients.add(session);
        System.out.println("New user joined: " + session.getId());
        //broadcast("User " + session.getId() + " has joined the chat!");
        //broadcastFast("New user joined: " + session.getId(), session);
    }

    
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
        	session.getAsyncRemote().sendText(message); 
        } catch (Exception e) {
            // Handle parsing errors
        }
    }
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("User left: " + session.getId());
       // broadcast("User " + session.getId() + " has disconnected.");
        //clients.remove(session);
        //broadcastFast("User left: " + session.getId(), session);
        clients.remove(session); 
    }
    
    
    private void broadcastFast(String message, Session user) {
        clients.forEach(peer -> {
            if (peer.isOpen() && !peer.getId().equalsIgnoreCase(user.getId())) {
                peer.getAsyncRemote().sendText(message); 
                // This returns immediately and sends in the background
            }
        });
    }
    
    /**
     * The Broadcast Logic
     */
    private void broadcast(String message) {
        // Use the session's own container to find all active peers
        // Note: In a real-world high-traffic app, you might use a CopyOnWriteArraySet
        for (Session peer : clients) {
            try {
                if (peer.isOpen()) {
                    peer.getBasicRemote().sendText(message);
                }
            } catch (IOException e) {
                System.err.println("Failed to send to " + peer.getId());
            }
        }
    }
	
}
