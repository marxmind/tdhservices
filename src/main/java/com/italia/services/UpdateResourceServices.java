package com.italia.services;

import java.io.File;

import com.italia.controller.VersionResponse;
import com.italia.utils.App;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

@Path("/app/update")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UpdateResourceServices {

	
	@GET
    @Path("/version")
    public Response checkForUpdate(@QueryParam("currentVersion") int currentVersion,
                                   @QueryParam("platform") @DefaultValue("android") String platform) {
        
		System.out.println("checking update.... ");
		
        // Handle iOS separately
        if ("ios".equalsIgnoreCase(platform)) {
            return getIosVersionResponse();
        }
        
       
        //android response
        VersionResponse response = VersionResponse.retriveAppLatest(platform);
        
        // Check if update is available
        boolean updateAvailable = currentVersion < response.getVersionCode();
        App app = App.getInstance();
        if (updateAvailable) {
            response.setDownloadUrl(app.getAppReleaseUrlAndroid() + "/app/update/apk");
            response.setReleaseNotes(response.getReleaseNotes());
            response.setFileSize(response.getFileSize());
            response.setRequired(true);
            return Response.ok(response).build();
        } else {
            // No update needed
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }
	
	private Response getIosVersionResponse() {
		App app = App.getInstance();
        VersionResponse response = VersionResponse.retriveAppLatest("ios");
        response.setDownloadUrl(app.getAppReleaseUrlIos());
        
        return Response.ok(response).build();
    }
	
	@GET
    @Path("/apk")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadApk() {
		System.out.println("downloading apk");
		App app = App.getInstance();
        File apkFile = new File(app.getAppApkLocation() + app.getAppReleaseName());
        
        if (!apkFile.exists()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("APK file not found")
                    .build();
        }
        
        ResponseBuilder response = Response.ok(apkFile);
        response.header("Content-Disposition", "attachment; filename=\"app-release.apk\"");
        response.header("Content-Length", apkFile.length());
        response.header("Content-Type", "application/vnd.android.package-archive");
        
        return response.build();
    }
	
		@HEAD
	    @Path("/version")
	    public Response checkVersionHeaders(@QueryParam("currentVersion") int currentVersion,
	                                        @QueryParam("platform") String platform) {
			VersionResponse latest = VersionResponse.retriveAppLatest(platform);
	        
	        if (latest != null && currentVersion < latest.getVersionCode()) {
	            return Response.ok()
	                    .header("X-Update-Available", "true")
	                    .header("X-Latest-Version", latest.getVersionCode())
	                    .build();
	        }
	        
	        return Response.ok()
	                .header("X-Update-Available", "false")
	                .build();
	    }
}
