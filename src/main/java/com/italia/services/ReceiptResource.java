package com.italia.services;

import java.util.ArrayList;
import java.util.List;

import com.italia.controller.PulseReport;
import com.italia.controller.Receipt;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/receipts")
public class ReceiptResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Receipt> list(@Context HttpHeaders headers){
		System.out.println("Loading PulseReport...");
		List<Receipt> recs = new ArrayList<Receipt>();
		System.out.println("Loaded "+ recs.size() +" PulseReport...");
		return recs;
	}
	
    @POST
    @Path("/uploads/xml")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response handleReceipt(Receipt receipt) {
        // Here, the 'receipt' object is already automatically 
        // populated from the XML by the JAXB provider
        
        System.out.println("Received: " + receipt.getMerchant());
        
        // Save to your database here
        
        return Response.ok("{\"status\":\"success\"}").build();
    }
}
