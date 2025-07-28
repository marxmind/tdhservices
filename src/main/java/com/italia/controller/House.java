package com.italia.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class House {

	private int id;
	private String stockName;
	private double quantity;
	private int isActive;
	private int hasUpdate;
	private String barcode;
	
	
	public static List<House> getStocksAll(){
		List<House> houses = new ArrayList<House>();
		
		for(HouseStocks s : HouseStocks.getStocksAll()) {
			final House kitchen = builder()
					.id(s.getId())
					.stockName(s.getStocksId().getStockName())
					.quantity(s.getQuantity())
					.isActive(s.getIsActive())
					.hasUpdate(s.getHasUpdate())
					.barcode(s.getStocksId().getBarcode())
					.build();
			houses.add(kitchen);
		}
		
		return houses;
	}
	
	public static House getById(int id){
		House house = new House();
		
		HouseStocks s = HouseStocks.getById(id);
			house = builder()
					.id(s.getId())
					.stockName(s.getStocksId().getStockName())
					.quantity(s.getQuantity())
					.isActive(s.getIsActive())
					.hasUpdate(s.getHasUpdate())
					.barcode(s.getStocksId().getBarcode())
					.build();
		
		return house;
	}
	
	public static House save(House kit) {
		
		HouseStocks stock = HouseStocks.getById(kit.id);
		
		HouseStocks kitchen = HouseStocks.builder()
				.id(kit.getId())
				.quantity(kit.getQuantity())
				.isActive(kit.getIsActive())
				.hasUpdate(kit.getHasUpdate())
				.stocksId(stock.getStocksId())
				.build();
		HouseStocks s = HouseStocks.save(kitchen);
		
		final House houseData = builder()
				.id(s.getId())
				.stockName(s.getStocksId().getStockName())
				.quantity(s.getQuantity())
				.isActive(s.getIsActive())
				.hasUpdate(s.getHasUpdate())
				.build();
		
		return houseData;
	}
	
	public static boolean delete(int id){
		return HouseStocks.delete(id);
	}
	
}
