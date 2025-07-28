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
public class Kitchen {

	private int id;
	private String stockName;
	private double quantity;
	private int isActive;
	private int hasUpdate;
	private String barcode;
	
	
	public static List<Kitchen> getStocks(){
		List<Kitchen> kitchens = new ArrayList<Kitchen>();
		
		for(KitchenStocks s : KitchenStocks.getStocks()) {
			final Kitchen kitchen = builder()
					.id(s.getId())
					.stockName(s.getStocksId().getStockName())
					.quantity(s.getQuantity())
					.isActive(s.getIsActive())
					.hasUpdate(s.getHasUpdate())
					.barcode(s.getStocksId().getBarcode())
					.build();
			kitchens.add(kitchen);
		}
		
		return kitchens;
	}
	
	public static Kitchen getById(int id){
		Kitchen kitchen = new Kitchen();
		
		KitchenStocks s = KitchenStocks.getById(id);
			kitchen = builder()
					.id(s.getId())
					.stockName(s.getStocksId().getStockName())
					.quantity(s.getQuantity())
					.isActive(s.getIsActive())
					.hasUpdate(s.getHasUpdate())
					.barcode(s.getStocksId().getBarcode())
					.build();
		
		return kitchen;
	}
	
	public static Kitchen save(Kitchen kit) {
		
		KitchenStocks stock = KitchenStocks.getById(kit.id);
		
		KitchenStocks kitchen = KitchenStocks.builder()
				.id(kit.getId())
				.quantity(kit.getQuantity())
				.isActive(kit.getIsActive())
				.hasUpdate(kit.getHasUpdate())
				.stocksId(stock.getStocksId())
				.build();
		KitchenStocks s = KitchenStocks.save(kitchen);
		
		final Kitchen kitchenData = builder()
				.id(s.getId())
				.stockName(s.getStocksId().getStockName())
				.quantity(s.getQuantity())
				.isActive(s.getIsActive())
				.hasUpdate(s.getHasUpdate())
				.build();
		
		return kitchenData;
	}
	
	public static boolean delete(int id){
		return KitchenStocks.delete(id);
	}
	
}
