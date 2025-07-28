package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.italia.controller.Food;
import com.italia.controller.FoodItem;
import com.italia.controller.FoodOrder;
import com.italia.controller.KitchenOrder;
import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.utils.DateUtils;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("kitchenorder")
public class KitchenOrderServices {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<KitchenOrder> list(@Context HttpHeaders headers){
		System.out.println("Loading food order...");
		List<KitchenOrder> items =  KitchenOrder.retrieve(" ORDER BY k.kid DESC LIMIT 1", new String[0]);
		System.out.println("done Loaded "+ items.size() +" kitchen food order...");
		return items;
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<KitchenOrder> all(@Context HttpHeaders headers){
		System.out.println("Loading food order...");
		List<KitchenOrder> items =  KitchenOrder.retrieve(" ORDER BY k.kid DESC LIMIT 100", new String[0]);
		System.out.println("done Loaded "+ items.size() +" kitchen food order...");
		return items;
	}
	
	@GET
	@Path("/search/orders/{orderid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchOrder(@PathParam("orderid") int orderid) {
		System.out.println("GET KitchenOrder orderid " + orderid);
		List<KitchenOrder> cash = KitchenOrder.retrieve(" AND o.orid=" + orderid, new String[0]);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	
	@GET
	@Path("/search/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response bydate(@PathParam("param") String param) {
		System.out.println("GET KitchenOrder date " + param);
		String dateFrom = param;
		String dateTo = param;
		int eid = 0;
		if(param.contains(":")) {
		
			String[] par = param.split(":");
			dateFrom = par[0];
			dateTo = par[1];
			eid = Integer.valueOf(par[2]);
		
		}
		String sql = " AND (k.datekit>='"+ dateFrom +"' AND k.datekit<='"+ dateTo +"')";
		
		if(eid>0) {
			sql += " AND k.cashiereid=" + eid;
		}
		
		
		List<KitchenOrder> cash = KitchenOrder.retrieve(sql + " ORDER BY o.orid DESC", new String[0]);
		
		
		
		
		
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/update/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSearch(@PathParam("param") String param) {
		System.out.println("GET KitchenOrder date " + param);
		String[] vals = param.split(":");
		String dateFrom = vals[0];
		String dateTo = vals[0];
		long kitchenId = Long.valueOf(vals[1]);
		int orderStatus = Integer.valueOf(vals[2]);
		
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("UPDATE kitchenorder SET orderstatus=" + orderStatus + " WHERE kid=" + kitchenId);
		ps.executeUpdate();
		}catch(SQLException e) {}
		
		
		List<KitchenOrder> cash = KitchenOrder.retrieve(" AND (k.datekit>='"+ dateFrom +"' AND k.datekit<='"+ dateTo +"') ORDER BY o.orid DESC", new String[0]);
		
		
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/add/foodOrder")
	public Response add(FoodOrder order) throws URISyntaxException {
		System.out.println("POST==================== " + order.getCustomerName());
		//long recId =  KitchenOrder.save(kitchen).getId();
		String date = DateUtils.getCurrentDateYYYYMMDD();
		String time = DateUtils.getCurrentTIME();
		int orderType = 1;
		int status = 1;
		for(FoodItem item : order.getItems()) { 
			
			KitchenOrder kitData = KitchenOrder.retrieveOrderExisting(order.getId(), item.getId(), item.getFoodId());
			
			if(kitData!=null) {
				
				kitData.setQty(item.getQty());
				kitData.setServingDate(item.getDate());
				kitData.setOrderType(orderType);
				kitData.setOrderStatus(status);
				kitData.setProgressIndicator(status);
				kitData.save();
				
			} else {
				KitchenOrder.builder()
					.id(0)
					.date(date)
					.time(time)
					.servingDate(date)
					.orderType(orderType)
					.orderStatus(status)
					.orderNumber("0000000000")
					.progressIndicator(0.1)
					.food(Food.builder().fid(item.getFoodId()).build())
					.item(item)
					.order(order)
					.qty(item.getQty())
					.isActive(1)
					.build().save();
			}
			
		}
		
		URI uri = new URI("/add/" + 0);
	
		return Response.created(uri).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/add/kitchen")
	public Response addKitchen(KitchenOrder kitchen) throws URISyntaxException {
		System.out.println("POST==================== " + kitchen.getOrderNumber());
		long recId =  KitchenOrder.save(kitchen).getId();
		
		URI uri = new URI("/add/" + recId);
	
		return Response.created(uri).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/add/list")
	public Response addKitchen(List<KitchenOrder> kits) throws URISyntaxException {
		System.out.println("POST====================size: " + kits.size());
		//long recId =  KitchenOrder.save(kitchen).getId();
		for(KitchenOrder o : kits) {
			o.save();
		}
		
		URI uri = new URI("/add/" + 0);
		return Response.created(uri).build();
	}
	
}
