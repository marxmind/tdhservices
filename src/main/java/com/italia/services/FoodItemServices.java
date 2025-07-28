package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.italia.controller.FoodItem;
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

@Path("fooditemorders")
public class FoodItemServices {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<FoodItem> list(@Context HttpHeaders headers){
		System.out.println("Loading fooditems...");
		List<FoodItem> items =  FoodItem.retrieve("", new String[0]);
		System.out.println("done Loaded "+ items.size() +" fooditems...");
		return items;
	}
	
	@GET
	@Path("/search/orders/{orderid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchOrder(@PathParam("orderid") int orderid) {
		System.out.println("GET fooditem orderid " + orderid);
		List<FoodItem> cash = FoodItem.retrieve(" AND i.orid=" + orderid, new String[0]);
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
	public Response add(FoodItem item) throws URISyntaxException {
		System.out.println("POST");
		long recId =  FoodItem.save(item).getId();
		URI uri = new URI("/add/" + recId);
	
		return Response.created(uri).build();
	}
	
	@GET
	@Path("/search/item/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response searchItem(@PathParam("id") int id) {
		System.out.println("GET fooditem id " + id);
		List<FoodItem> cash = FoodItem.retrieve(" AND i.ftid=" + id, new String[0]);
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Path("/combined/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response combined(@PathParam("param") String param) {
		System.out.println("GET fooditem param -------------------------------------------------------------- " + param);
		String fromDate="";
		String toDate="";
		int userEid = 0;
		int location = 0;//default resto
		if(param.contains(":")) {
			fromDate = param.split(":")[0];
			toDate = param.split(":")[1];
			userEid = Integer.valueOf(param.split(":")[2]);
			try {location = Integer.valueOf(param.split(":")[3]);}catch(NumberFormatException e) {}
		}else {
			fromDate = param;
			toDate = param;
		}
		
		String sql = " AND ( i.dateitem>='"+ fromDate +"' AND i.dateitem<='"+ toDate +"' )";
		
		if(userEid>0) {
			sql += " AND o.cashiereid=" + userEid;
		}
		
		String all = param.split(":")[3];
		//add location
		if("all".equalsIgnoreCase(all)) {
			//meaning all sale location retrieval
		}else {
			sql += " AND f.location=" + location;
		}
		
		
		List<FoodItem> cash = FoodItem.retrieve(sql, new String[0]);
		
		Map<Integer, FoodItem> map = new LinkedHashMap<Integer, FoodItem>();
		
		for(FoodItem item : cash) {
			int id = item.getFoodId();
			if(map!=null) {
				if(map.containsKey(id)) {
					double price = map.get(id).getPrice() + item.getPrice();
					double qty = map.get(id).getQty() + item.getQty();
					item.setPrice(price);
					item.setQty(qty);
					map.put(id, item);
				}else {
					map.put(id, item);
				}
			}else {
				map.put(id, item);
			}
			
		}
		cash = new ArrayList<FoodItem>();
		cash.addAll(map.values());
		
		//below code use to sort qty , ordering from lowest to highest
		Collections.sort(cash, new Comparator<FoodItem>() {
		    @Override
		    public int compare(FoodItem c1, FoodItem c2) {
		        return Double.compare(c1.getQty(), c2.getQty());
		    }
		});
		
		Collections.reverse(cash);//reverse the order showing the order from highest quantity to lowest
		
		if (cash != null) {
			return Response.ok(cash, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/item/delete/{id}")
	public Response delete(@PathParam("id") int id) {
		System.out.println("DELETE");
		System.out.println("orderitem Id:" + id);
		if (FoodItem.delete(id)) {
			return Response.ok().build();					
		} else {
			return Response.notModified().build();
		}
	}

}
