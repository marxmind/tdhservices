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
public class Resto{

	private int id;
	private String stockName;
	private double quantity;
	private int isActive;
	private int hasUpdate;
	private String barcode;
	
	
	public static List<Resto> getStockAll(){
		List<Resto> restos = new ArrayList<Resto>();
		
		for(RestoStocks s : RestoStocks.getStocksAll()) {
			final Resto resto = builder()
					.id(s.getId())
					.stockName(s.getStock().getStockName())
					.quantity(s.getQuantity())
					.isActive(s.getIsActive())
					.hasUpdate(s.getHasUpdate())
					.barcode(s.getStock().getBarcode())
					.build();
			restos.add(resto);
		}
		
		return restos;
	}
	
	public static Resto getById(int id){
		Resto kitchen = new Resto();
		
		RestoStocks s = RestoStocks.getById(id);
			kitchen = builder()
					.id(s.getId())
					.stockName(s.getStock().getStockName())
					.quantity(s.getQuantity())
					.isActive(s.getIsActive())
					.hasUpdate(s.getHasUpdate())
					.barcode(s.getStock().getBarcode())
					.build();
		
		return kitchen;
	}
	
	public static Resto save(Resto kit) {
		
		RestoStocks stock = RestoStocks.getById(kit.id);
		
		RestoStocks kitchen = RestoStocks.builder()
				.id(kit.getId())
				.quantity(kit.getQuantity())
				.isActive(kit.getIsActive())
				.hasUpdate(kit.getHasUpdate())
				.stock(stock.getStock())
				.build();
		RestoStocks s = RestoStocks.save(kitchen);
		
		final Resto kitchenData = builder()
				.id(s.getId())
				.stockName(s.getStock().getStockName())
				.quantity(s.getQuantity())
				.isActive(s.getIsActive())
				.hasUpdate(s.getHasUpdate())
				.build();
		
		return kitchenData;
	}
	
	public static boolean delete(int id){
		return RestoStocks.delete(id);
	}
}