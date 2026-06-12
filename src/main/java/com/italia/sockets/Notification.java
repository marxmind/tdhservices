package com.italia.sockets;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/notification")
public class Notification {
	
	// Set of connected sessions
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
	
	@OnOpen
	public void onOpen(Session session) {
		sessions.add(session);
		System.out.println("open session: " + session.getId());
		//sendMessageToClient(session, "You are online");
	}
	
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		System.out.println("Received: " + message);
		//session.getBasicRemote().sendText("connecting... " + message);
		session.getBasicRemote().sendText("Connecting... ");
		sendMessageToClient(session, message);
	}
	
	@OnClose
	public void onClose(Session session) {
		System.out.println("disconnected: " + session.getId());
	}
	
	// To send to a specific stored user
	/*public void sendMessageToUser(Session sessions, String userId, String content) {
	    Session session = sessions.get(userId);
	    if (session != null && session.isOpen()) {
	        session.getAsyncRemote().sendText(content);
	    }
	}*/
	
	private void sendMessageToClient(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to send a message to all connected clients
    public void broadcast(String message) {
        for (Session session : sessions) {
            sendMessageToClient(session, "Broadcast: " + message);
        }
    }
	
	
}
