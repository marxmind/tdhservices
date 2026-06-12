package com.italia.mcp;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class McpContextListener implements ServletContextListener {

    // Replace with your true token URL from the xiaozhi.me dashboard
    private static final String XIAOZHI_MCP_URL = "wss://api.xiaozhi.me/mcp/?token=eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjkzNjQwNiwiYWdlbnRJZCI6MTg1MzA3MiwiZW5kcG9pbnRJZCI6ImFnZW50XzE4NTMwNzIiLCJwdXJwb3NlIjoibWNwLWVuZHBvaW50IiwiaWF0IjoxNzc5MDc1MDc0LCJleHAiOjE4MTA2MzI2NzR9.LoR2h9K0akvHyzSdgF05_JU3jGOBnSmuWQs2qALKYRztyhSp9WlEuVkBx54SW9qhGWUh4bRFLS57wRNHu5wYUQ";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("=================================================");
        System.out.println("Tomcat Context Initialized! Launching Xiaozhi MCP Client...");
        System.out.println("=================================================");
        
        try {
            //XiaozhiMcpClient client = new XiaozhiMcpClient();
            //client.connectToXiaozhi(XIAOZHI_MCP_URL);
        } catch (Exception e) {
            System.err.println("Failed to trigger initialization connection baseline.");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Tomcat shutting down. Terminating connections cleanly.");
    }
}