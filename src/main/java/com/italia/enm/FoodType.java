package com.italia.enm;

/**
 * 
 * @author Mark Italia
 * @since 11-18-2020
 * @version 1.0
 *
 */
public enum FoodType {
		FOOD_TYPE(0, "Food Type"),
		FISH(1, "Fish"),
		CHICKEN(2, "Chicken"),
		SEAFOOD(3, "Seafood"),
		BEEF(4, "Beef"),
		NODDLE_PASTA(5, "Noddles/Pasta"),
		VEGETABLES(6,"Vegetables"),
		RICE(7, "Rice"),
		DESSERT(8, "Desserts"),
		SHAKES(9, "Shakes"),
		HOT_DRINKS(10, "Hot Drinks"),
		COLD_DRINKS(11, "Cold Drinks"),
		PITCHER(12, "Pitcher"),
		COCKTAILS(13, "Cocktails"),
		SHOTTER(14, "Shotter"),
		BODDLE_FIGHT(15, "Boddle Fight"),
		SNACKS(16, "Snacks"),
		BEVERAGES(17, "Beverages"),
		OTHERS(18, "Others"),
		APPETIZER(19, "Appetizers/Salad"),
		BREAKFAST(20, "Breakfast"),
		ACCOMMODATION(21, "Accommodation"),
		LIQUOR(22, "LIQUOR");
		
		private int id;
		private String name;
		
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		
		private FoodType(int id, String name) {
			this.id = id;
			this.name = name;
		}
		public static FoodType containId(int id) {
			for(FoodType t : FoodType.values()) {
				if(t.getId()==id) {
					return t;
				}
			}
			return OTHERS;
		}
		
		public static FoodType containName(String name) {
			for(FoodType t : FoodType.values()) {
				if(t.getName().equalsIgnoreCase(name)) {
					return t;
				}
			}
			return OTHERS;
		}
	
}
