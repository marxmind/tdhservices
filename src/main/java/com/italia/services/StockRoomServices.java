package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.italia.controller.Stocks;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
@Path("stockroom")
public class StockRoomServices {

	private int id;
	private String stockName;
	private double quantity;
	private int isActive;
	private String barcode;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Stocks> list(@Context HttpHeaders headers){
		System.out.println("Loading stocks...");
		List<Stocks> stocks = Stocks.getStocks();
		System.out.println("Loaded "+ stocks.size() +" stocks...");
		return stocks;
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		System.out.println("GET Path id");
		Stocks stock = Stocks.getById(id);
		if (stock != null) {
			return Response.ok(stock, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/search/{stockname}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("stockname") String stockname) {
		System.out.println("GET search " + stockname);
		Stocks stock = Stocks.getByName(stockname);
		if (stock != null) {
			return Response.ok(stock, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(Stocks stocks) throws URISyntaxException {
		System.out.println("POST");
		int newStockId =  Stocks.save(stocks).getId();
		URI uri = new URI("/add/" + newStockId);
		return Response.created(uri).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(@PathParam("id") int id, Stocks stocks) {
		stocks.setId(id);
		System.out.println("PUT:" + id);
		System.out.println("id: "+ stocks.getId() +" name: " + stocks.getStockName() + " qty:" + stocks.getQuantity() + "barcode: " + stocks.getBarcode());
		if (Stocks.save(stocks).getId()>0) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response delete(@PathParam("id") int id) {
		System.out.println("DELETE");
		System.out.println("Stock Id:" + id);
		if (Stocks.delete(id)) {
			return Response.ok().build();					
		} else {
			return Response.notModified().build();
		}
	}
	
}