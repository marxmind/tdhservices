package com.italia.services;

import java.io.File;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/windows")
public class WindowsResourceUpdate {

    // Define the absolute or relative path to where your MSIX file sits on the server
    private static final String MSIX_FILE_PATH = "C:/tdh/msix/tdhapp.msix"; 
    private static final String LATEST_VERSION = "1.0.0.1"; // Update this when you release new versions
    //private static final String DOWNLOAD_URL = "https://resort.dreamweavershill.com/tdhservices/windows/download";
    private static final String DOWNLOAD_URL = "http://192.168.1.197:8085/tdhservices/windows/download";

    @GET
    @Path("/latest-info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVersionInfo() {
        // Returns the version configuration to Flutter
        UpdateManifest manifest = new UpdateManifest(LATEST_VERSION, DOWNLOAD_URL);
        return Response.ok(manifest).build();
    }

    @GET
    @Path("/download")
    @Produces("application/msix")
    public Response downloadMsix() {
        File file = new File(MSIX_FILE_PATH);
        System.out.println("downloadMsix : " + MSIX_FILE_PATH);
        // Safety check: Make sure the file actually exists on your server storage
        if (!file.exists()) {
        	
        	System.out.println("file is not present");
            return Response.status(Status.NOT_FOUND)
                    .entity("{\"error\": \"The requested installer file was not found on the server.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        
        System.out.println("file is present");
        
        // Stream the MSIX file down to the Flutter client app
        return Response.ok(file)
                .header("Content-Disposition", "attachment; filename=\"tdhapp_update.msix\"")
                .header("Content-Length", file.length()) // Helps Flutter show a download progress percentage bar
                .build();
    }

    // --- Helper Class / DTO for JSON Serialization ---
    public static class UpdateManifest {
        private String version;
        private String downloadUrl;

        // No-arg constructor required by JAX-RS JSON providers (like Jackson/Moxy)
        public UpdateManifest() {}

        public UpdateManifest(String version, String downloadUrl) {
            this.version = version;
            this.downloadUrl = downloadUrl;
        }

        // Getters and Setters are required for the server to map fields to JSON keys
        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }
}
