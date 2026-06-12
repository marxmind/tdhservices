package com.italia.mcp;

import jakarta.websocket.*;
import java.net.URI;
import java.io.StringReader;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonObjectBuilder;

@ClientEndpoint
public class XiaozhiMcpClient {

    private Session session;
    private String sessionId = null;

    public void connectToXiaozhi(String mcpEndpointUrl) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(mcpEndpointUrl));
            System.out.println("Network socket layer connected successfully!");
        } catch (Exception e) {
            System.err.println("Tomcat WebSocket connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("WebSocket pipe open. Sending Xiaozhi Custom Protocol Hello Wrap...");
        sendXiaozhiHello();
    }

    @OnMessage
    public void onMessage(String message) {
    	
    	System.out.println("onMessage: " + message);
    	
        try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonNode = reader.readObject();
            
            // Check the top-level wrapping envelope type required by the specification
            String envelopeType = jsonNode.getString("type", "");

            // Handle the Xiaozhi Protocol initialization response
            if ("hello".equals(envelopeType)) {
                if (jsonNode.containsKey("session_id")) {
                    this.sessionId = jsonNode.getString("session_id");
                }
                System.out.println("Xiaozhi platform hello accepted! Assigned Session ID: " + this.sessionId);
                return; // Wait for the broker to initiate the MCP stage
            }

            // Route standard MCP interactions nested within the wrapper envelope
            if ("mcp".equals(envelopeType) || jsonNode.containsKey("method")) {
                
                // If it is wrapped inside an outer "type":"mcp" container, unwrap it first
                JsonObject mcpPayload = jsonNode.containsKey("method") ? jsonNode : jsonNode.getJsonObject("mcp");
                if (mcpPayload == null) return;

                String method = mcpPayload.getString("method", "");
                Object id = mcpPayload.containsKey("id") ? mcpPayload.get("id").toString() : null;

                System.out.println("Processing Wrapped MCP Action Request: " + method + " (ID: " + id + ")");

                if ("initialize".equals(method)) {
                    sendInitializeResponse(id);
                } 
                else if ("ping".equals(method)) {
                    sendPingResponse(id);
                }
                else if ("tools/list".equals(method)) {
                    sendToolsListResponse(id);
                }
                else if ("tools/call".equals(method)) {
                    JsonObject paramsNode = mcpPayload.getJsonObject("params");
                    if (paramsNode != null && "read_soil_moisture".equals(paramsNode.getString("name", ""))) {
                        sendResponse(id, "The current soil moisture index is 62 percent.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error decoding encapsulated framing data: " + e.getMessage());
        }
    }

    private void sendXiaozhiHello() {
        try {
            JsonObject helloEnvelope = Json.createObjectBuilder()
                .add("type", "hello")
                .add("version", 1)
                .add("transport", "websocket")
                .add("features", Json.createObjectBuilder().add("mcp", true).build())
                .build();
            
            this.session.getBasicRemote().sendText(helloEnvelope.toString());
            System.out.println("Sent protocol wrapper initialization down the wire.");
        } catch (Exception e) {
            System.err.println("Failed to send protocol hello packet: " + e.getMessage());
        }
    }

    private void sendInitializeResponse(Object id) {
        try {
            JsonObject resultBody = Json.createObjectBuilder()
                .add("protocolVersion", "2024-11-05")
                .add("capabilities", Json.createObjectBuilder().add("tools", Json.createObjectBuilder().build()))
                .add("serverInfo", Json.createObjectBuilder().add("name", "tomcat-bridge").add("version", "1.0.0"))
                .build();

            JsonObjectBuilder responseBuilder = Json.createObjectBuilder()
                .add("jsonrpc", "2.0")
                .add("result", resultBody);
            if (id != null) responseBuilder.add("id", Integer.parseInt(id.toString().replace("\"", "")));

            // Wrap inside the outer envelope block expected by the platform
            JsonObject finalEnvelope = Json.createObjectBuilder()
                .add("type", "mcp")
                .add("mcp", responseBuilder.build())
                .build();

            this.session.getBasicRemote().sendText(finalEnvelope.toString());
            System.out.println("Dispatched Wrapped Handshake Response.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendPingResponse(Object id) {
        try {
            JsonObjectBuilder responseBuilder = Json.createObjectBuilder()
                .add("jsonrpc", "2.0")
                .add("result", Json.createObjectBuilder().build());
            if (id != null) responseBuilder.add("id", Integer.parseInt(id.toString().replace("\"", "")));

            JsonObject finalEnvelope = Json.createObjectBuilder()
                .add("type", "mcp")
                .add("mcp", responseBuilder.build())
                .build();

            this.session.getBasicRemote().sendText(finalEnvelope.toString());
            System.out.println("Dispatched Wrapped Pong Keep-Alive Response.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToolsListResponse(Object id) {
        try {
            JsonObject soilTool = Json.createObjectBuilder()
                .add("name", "read_soil_moisture")
                .add("description", "Reads the live moisture percentage from the physical soil sensor module.")
                .add("inputSchema", Json.createObjectBuilder().add("type", "object").add("properties", Json.createObjectBuilder().build()))
                .build();

            JsonObject resultBody = Json.createObjectBuilder()
                .add("tools", Json.createArrayBuilder().add(soilTool).build())
                .build();

            JsonObjectBuilder responseBuilder = Json.createObjectBuilder()
                .add("jsonrpc", "2.0")
                .add("result", resultBody);
            if (id != null) responseBuilder.add("id", Integer.parseInt(id.toString().replace("\"", "")));

            JsonObject finalEnvelope = Json.createObjectBuilder()
                .add("type", "mcp")
                .add("mcp", responseBuilder.build())
                .build();

            this.session.getBasicRemote().sendText(finalEnvelope.toString());
            System.out.println("Dispatched Wrapped Tool Catalog.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(Object id, String text) {
        try {
            JsonObject textContent = Json.createObjectBuilder().add("type", "text").add("text", text).build();
            JsonObject resultBody = Json.createObjectBuilder()
                .add("content", Json.createArrayBuilder().add(textContent).build())
                .add("isError", false)
                .build();

            JsonObjectBuilder responseBuilder = Json.createObjectBuilder()
                .add("jsonrpc", "2.0")
                .add("result", resultBody);
            if (id != null) responseBuilder.add("id", Integer.parseInt(id.toString().replace("\"", "")));

            JsonObject finalEnvelope = Json.createObjectBuilder()
                .add("type", "mcp")
                .add("mcp", responseBuilder.build())
                .build();

            this.session.getBasicRemote().sendText(finalEnvelope.toString());
            System.out.println("Dispatched Wrapped Data Execution Result back to AI.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}