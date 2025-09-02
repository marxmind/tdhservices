package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.controller.Food;
import com.italia.controller.FoodItem;
import com.italia.controller.FoodOrder;
import com.italia.controller.KitchenOrder;
import com.italia.db.conf.Conf;
import com.italia.db.conf.DBConnect;
import com.italia.enm.OrderStatus;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("foodorder")
public class FoodOrderServices {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<FoodOrder> list(@Context HttpHeaders headers){
		System.out.println("Loading food order...");
		List<FoodOrder> items =  FoodOrder.retrieve(" ORDER BY orid DESC LIMIT 1", new String[0]);
		System.out.println("done Loaded "+ items.size() +" food order...");
		return items;
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FoodOrder> all(@Context HttpHeaders headers){
		System.out.println("all Loading food order...");
		List<FoodOrder> itemsPaid =  new ArrayList<FoodOrder>();//FoodOrder.retrieve(" AND iscompleted="+ OrderStatus.COMPLETED.getId() + " ORDER BY orid DESC LIMIT 100", new String[0]);
		
		List<FoodOrder> itemsUnpaid = new ArrayList<FoodOrder>(); //FoodOrder.retrieve(" AND iscompleted="+ OrderStatus.FOR_SERVING.getId() +" ORDER BY orid DESC LIMIT 100", new String[0]);
		
		List<FoodOrder> tmpItems =  FoodOrder.retrieve(" ORDER BY orid DESC LIMIT 1000", new String[0]);
		List<FoodOrder> items = new ArrayList<FoodOrder>();
		for(FoodOrder order : tmpItems) {
			if(OrderStatus.FOR_SERVING.getId()==order.getIsCompleted()) {
				itemsUnpaid.add(order);
			}else {
				itemsPaid.add(order);
			}
		}
		items.addAll(itemsUnpaid);
		items.addAll(itemsPaid);
		System.out.println("done Loaded "+ items.size() +" food order...");
		return items;
	}
	
	@GET
	@Path("/cashier/history")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FoodOrder> cashierHistory(@Context HttpHeaders headers){
		System.out.println("all Loading food order...");
		List<FoodOrder> itemsPaid =  new ArrayList<FoodOrder>();
		
		List<FoodOrder> itemsUnpaid = new ArrayList<FoodOrder>();
		
		List<FoodOrder> tmpItems =  FoodOrder.retrieveHistory(" ORDER BY orid DESC LIMIT 50", new String[0]);
		List<FoodOrder> items = new ArrayList<FoodOrder>();
		for(FoodOrder order : tmpItems) {
			if(OrderStatus.FOR_SERVING.getId()==order.getIsCompleted()) {
				itemsUnpaid.add(order);
			}else {
				itemsPaid.add(order);
			}
		}
		items.addAll(itemsUnpaid);
		items.addAll(itemsPaid);
		System.out.println("done Loaded "+ items.size() +" food order...");
		return items;
	}
	
	@GET
	@Path("/search/orders/{orderid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchOrder(@PathParam("orderid") int orderid) {
		System.out.println("GET FoodOrder orderid " + orderid);
		List<FoodOrder> cash = FoodOrder.retrieve(" AND orid=" + orderid, new String[0]);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(FoodOrder order) throws URISyntaxException {
		System.out.println("POST==================== " + order.getCustomerName());
		long recId =  FoodOrder.save(order).getId();
		for(FoodItem item : order.getItems()) { 
			if(item.getQty()==0) {
				item.delete(true);
			}else {
				item.setFoodOrderId(recId);   
				item.save();
			}
		}
		
		FoodOrder updatedOrder = FoodOrder.retrieveOrder(recId);
		for(FoodItem item : updatedOrder.getItems()) {
			
			
			KitchenOrder kitData = KitchenOrder.retrieveOrderExisting(updatedOrder.getId(), item.getId(), item.getFoodId());
			
			if(kitData!=null) {
				
				kitData.setQty(item.getQty());
				//kitData.setServingDate(item.getDate());
				//kitData.setOrderType(orderType);
				//kitData.setOrderStatus(status);
				//kitData.setProgressIndicator(status);
				kitData.save();
				
			} else {
			
				KitchenOrder.builder()
				.id(0)
				.date(updatedOrder.getDate())
				.time(updatedOrder.getTime())
				.servingDate(updatedOrder.getDate())
				.orderNumber("0")
				.qty(item.getQty())
				.progressIndicator(0.1)
				.orderStatus(0)
				.orderType(0)
				.isActive(1)
				.order(updatedOrder)
				.item(item)
				.food(Food.builder().fid(item.getFoodId()).build())
				.build().save();
			}
		}
		
		
		URI uri = new URI("/add/" + recId);
	
		return Response.created(uri).build();
	}
	
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/order/delete/{id}")
	public Response delete(@PathParam("id") int id) {
		System.out.println("DELETE");
		System.out.println("orderitem Id:" + id);
		if (FoodOrder.delete(id)) {
			
			FoodItem.delete("UPDATE fooditem SET isactiverepft=0 WHERE orid=" + id, new String[0]);
			KitchenOrder.delete("UPDATE kitchenorder SET isactivek=0 WHERE orid=" + id, new String[0]);
			
			return Response.ok().build();					
		} else {
			return Response.notModified().build();
		}
	}
	
	@GET
	@Path("/update/cashier/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCashier(@PathParam("param") String param) {
		System.out.println("GET food order " + param);
		String[] vals = param.split(":");
		long foodOrderId = Long.valueOf(vals[0]);
		long userEid = Long.valueOf(vals[1]);
		
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		try{
		conn = DBConnect.getConnection(Conf.getInstance().getDatabaseMain());
		ps = conn.prepareStatement("UPDATE foodorder SET cashiereid=" + userEid + " WHERE orid=" + foodOrderId);
		ps.executeUpdate();
		}catch(SQLException e) {}
		
		
		List<FoodOrder> food = FoodOrder.retrieve(" AND orid="+ foodOrderId, new String[0]);
		
		
		if (food != null) {
			return Response.ok(food, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

}
