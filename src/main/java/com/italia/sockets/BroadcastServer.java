package com.italia.sockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.json.JSONObject;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/broadcast")
public class BroadcastServer {

	// Set of connected sessions
    //private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

 // Inside your class:
    private static final Set<Session> clients = new CopyOnWriteArraySet<>();
    
    @OnOpen
    public void onOpen(Session session) {
    	// Set idle timeout to 60 seconds. 
        // If no message (ping or data) is received for 60s, close the connection.
        session.setMaxIdleTimeout(60000);
        
    	clients.add(session);
        System.out.println("New user joined: " + session.getId());
        //broadcast("User " + session.getId() + " has joined the chat!");
        //broadcastFast("New user joined: " + session.getId(), session);
    }

    /*@OnMessage
    public void onMessage(String message, Session session) {
    	
    	JSONObject json = new JSONObject(message);
    	// Check if the incoming message is a Heartbeat Ping
        if ("ping".equals(json.optString("type"))) {
            sendPong(session);
            return; // Exit early so this doesn't reach your business logic
        }
    	
        // When one person sends a message, everyone sees it
        String formattedMessage = "User " + session.getId() + " is online";//"User " + session.getId() + ": " + message;
        //broadcast(formattedMessage);
    }*/
    
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            // Use a JSON library like Jackson, Gson, or Jakarta JSON-P
            var json = new JSONObject(message); 
            System.out.println("on message " + json.optString("type"));
            System.out.println("on message " + json.toString());
            
            if(json.has("type")) {
            	System.out.println("type=== " + json.optString("type"));
            	// Immediately reply with a pong
                String pongResponse = "{\"type\": \"pong\"}";
                session.getBasicRemote().sendText(pongResponse);
                return; // Stop here; don't pass to business logic
            }else if(json.has("attendance")) {
            	System.out.println("attendance=== " + json.optString("attendance"));
            	System.out.println("pasok sa else....");
            	String pongResponse = "{\"type\": \"attendance\",\"message\": \""+ json.optString("attendance") +"\"}";
            	//session.getBasicRemote().sendText(pongResponse);
            	//System.out.println("Message: " + message);
            	broadcastFast(pongResponse, session);
            }else if(json.has("order")) {
            	System.out.println("order=== " + json.optString("order"));
            	String pongResponse = "{\"type\": \"order\",\"message\": \""+ json.optString("order") +"\"}";
            	broadcastFast(pongResponse, session);
            }	
            
           /* if ("ping".equals(json.optString("type"))) {
                // Immediately reply with a pong
                String pongResponse = "{\"type\": \"pong\"}";
                session.getBasicRemote().sendText(pongResponse);
                return; // Stop here; don't pass to business logic
            }else {
            	System.out.println("pasok sa else....");
            	String pongResponse = "{\"type\": \"attendance\",\"message\": \""+ json.optString("attendance") +"\"}";
            	//session.getBasicRemote().sendText(pongResponse);
            	//System.out.println("Message: " + message);
            	broadcastFast(pongResponse, session);
            	//return;
            }*/

            // Process actual application data here...
            
        } catch (Exception e) {
            // Handle parsing errors
        }
    }
    
    private void sendPong(Session session) {
        try {
            JSONObject pong = new JSONObject();
            pong.put("type", "pong");
            session.getBasicRemote().sendText(pong.toString());
        } catch (IOException e) {
            System.err.println("Failed to send pong: " + e.getMessage());
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
