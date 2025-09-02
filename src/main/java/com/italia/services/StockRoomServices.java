package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.italia.controller.HouseStocks;
import com.italia.controller.Kitchen;
import com.italia.controller.KitchenStocks;
import com.italia.controller.RestoStocks;
import com.italia.controller.Stocks;
import com.italia.controller.SupplierPayables;
import com.italia.enm.Department;

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
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("stocks")
	public Response stock(Stocks stocks) throws URISyntaxException {
		System.out.println("POST");
		int departmentId = Integer.valueOf(stocks.getBarcode());
		
		if(Department.KITCHEN.getId()==departmentId) {
			
			boolean isExistStock = KitchenStocks.isStockIdExist(stocks.getId());
			
			if(!isExistStock) {
			KitchenStocks.builder()
					.id(0)
					.quantity(1)
					.isActive(1)
					.hasUpdate(stocks.getHasUpdate())
					.stocksId(Stocks.builder().id(stocks.getId()).build())
					.build()
					.save();
			}
		}else if(Department.RESTAURANT.getId()==departmentId) {
			
			boolean isExistStock = RestoStocks.isStockIdExist(stocks.getId());
			
			if(!isExistStock) {
			RestoStocks.builder()
			.id(0)
			.quantity(1)
			.isActive(1)
			.stock(Stocks.builder().id(stocks.getId()).build())
			.hasUpdate(stocks.getHasUpdate())
			.build()
			.save();
			}
		}else if(Department.HOUSEKEEPING.getId()==departmentId) {
			boolean isExistStock = HouseStocks.isStockIdExist(stocks.getId());
			
			if(!isExistStock) {
			HouseStocks.builder()
			.id(0)
			.quantity(1)
			.isActive(1)
			.hasUpdate(stocks.getHasUpdate())
			.stocksId(Stocks.builder().id(stocks.getId()).build())
			.build()
			.save();
			}
		}
		
		int newStockId =  0;//Stocks.save(stocks).getId();
		URI uri = new URI("/add/" + newStockId);
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/department/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response department(@PathParam("id") int id) {
		System.out.println("GET department " + id);
		List<Stocks> sups =  new ArrayList<Stocks>();
		
		if(Department.KITCHEN.getId()==id) {
			for(KitchenStocks s : KitchenStocks.getStocks()) {
				Stocks st = Stocks.builder()
						.id(s.getId())
						.stockName(s.getStocksId().getStockName())
						.barcode(s.getStocksId().getBarcode())
						.isActive(s.getIsActive())
						.hasUpdate(s.getHasUpdate())
						.quantity(s.getQuantity())
						.build();
				sups.add(st);
			}
		}else if(Department.RESTAURANT.getId()==id) {
			for(RestoStocks s : RestoStocks.getStocksAll()) {
				Stocks st = Stocks.builder()
						.id(s.getId())
						.stockName(s.getStock().getStockName())
						.barcode(s.getStock().getBarcode())
						.isActive(s.getIsActive())
						.hasUpdate(s.getHasUpdate())
						.quantity(s.getQuantity())
						.build();
				sups.add(st);
			}
		}else if(Department.HOUSEKEEPING.getId()==id) {
			for(HouseStocks s : HouseStocks.getStocksAll()) {
				Stocks st = Stocks.builder()
						.id(s.getId())
						.stockName(s.getStocksId().getStockName())
						.barcode(s.getStocksId().getBarcode())
						.isActive(s.getIsActive())
						.hasUpdate(s.getHasUpdate())
						.quantity(s.getQuantity())
						.build();
				sups.add(st);
			}
		}else if(Department.STOCKROOM.getId()==id) {
			sups = Stocks.getStocks();
		}
		
		
		if (sups != null) {
			return Response.ok(sups, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	
} 