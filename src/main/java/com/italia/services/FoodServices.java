package com.italia.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import com.italia.controller.Food;
import com.italia.controller.Supplier;
import com.italia.controller.SupplierPayables;

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
@Path("foods")
public class FoodServices {
	
	private int fid;
	private String foodName;
	private String description;
	private double price;
	private int isActive;
	private int foodType;
	private double quantity;
	private String picture;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Food> list(@Context HttpHeaders headers){
		System.out.println("Loading food...");
		List<Food> foods = Food.getAllFood();
		System.out.println("Loaded "+ foods.size() +" foods...");
		return foods;
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam("id") int id) {
		System.out.println("GET Path id");
		Food food = Food.getByFoodId(id);
		if (food != null) {
			return Response.ok(food, MediaType.APPLICATION_JSON).build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("add")
	public Response add(Food food) throws URISyntaxException {
		System.out.println("POST");
		int newFoodId =  Food.save(food).getFid();
		URI uri = new URI("/add/" + newFoodId);
		return Response.created(uri).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response update(@PathParam("id") int id, Food food) {
		food.setFid(id);
		System.out.println("PUT:" + id);
		//System.out.println("id: "+ food.getFid() +" name: " + food.getFoodName() + " qty:" + food.getQuantity());
		if (Food.save(food).getFid()>0) {
			return Response.ok().build();
		} else {
			return Response.notModified().build();
		}
	}
	
	
	@GET
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response delete(@PathParam("id") int id) {
		System.out.println("GET food id " + id);
		if(Food.hasExistingTransaction(id)) {
			List<Food> rsvs = Food.getAllFood();
			return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
		}else {
			Supplier.delete("UPDATE food SET isactivefood=0 WHERE fid=" + id, new String[0]);
			List<Food> rsvs = Food.getAllFood();
			if (rsvs != null) {
				return Response.ok(rsvs, MediaType.APPLICATION_JSON).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		}
	}
	
}
