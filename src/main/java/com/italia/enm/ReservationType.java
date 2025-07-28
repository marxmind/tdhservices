package com.italia.enm;

public enum ReservationType {

	GONO_UDI_1(1, "GONO UDI 1"),
	GONO_UDI_2(2, "GONO UDI 2"),
	GONO_UDI_3(3, "GONO UDI 3"),
	SELONG_TONOK_1(4, "SELONG TONOK 1"),
	SELONG_TONOK_2(5, "SELONG TONOK 2"),
	SELONG_TONOK_3(6, "SELONG TONOK 3"),
	SELONG_TONOK_4(7, "SELONG TONOK 4"),
	SELONG_TONOK_5(8, "SELONG TONOK 5"),
	SELONG_TONOK_6(9, "SELONG TONOK 6"),
	GONO_DATU(10, "GONO DATU"),
	GONO_DATU_EXT(11, "GONO DATU EXTENSION"),
	SEMINAR_ROOM_RESERVATION(12, "SEMINAR ROOM RESERVATION"),
	PRENUP_RESERVATION(13, "PRENUP RESERVATION"),
	BIRTHDAY_PARY_RESERVATION(14, "BIRTHDAY PARTY RESERVATION"),
	SURPRISE_RSV(15, "SURPRISE RESERVATION"),
	OPEN_PARTY(16, "OPEN PARTY"),
	DEBUT(17, "DEBUT"),
	VILLA_1(18, "VILLA SOCIMER"),
	VILLA_2(19, "VILLA MARIE"),
	VILLA_3(20, "VILLA MERCEDES"),
	VILLA_4(21, "VILLA ADORA"),
	VILLA_5(22, "EXECUTIVE VILLA"),
	VILLA_6(23, "VILLA ADORA 2"),
	VILLA_7(24, "VILLA WITH POOL 1"),
	VILLA_8(25, "VILLA WITHH POOL 2"),
	GLAMPING_1(26, "GLAMPING 1"),
	GLAMPING_2(27, "GLAMPING 2"),
	GLAMPING_3(28, "GLAMPING 3"),
	OTHERS(29, "OTHERS"),
	GLAMPING_4(30, "GLAMPING 4"),
	GLAMPING_5(31, "GLAMPING 5"),
	GLAMPING_6(32, "GLAMPING 6"),
	;
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	private int id;
	private String name;
	
	private ReservationType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static ReservationType containId(int id) {
		for(ReservationType t : ReservationType.values()) {
			if(t.getId()==id) {
				return t;
			}
		}
		return OPEN_PARTY;
	}
	
	public static ReservationType containName(String name) {
		for(ReservationType t : ReservationType.values()) {
			if(t.getName().equalsIgnoreCase(name)) {
				return t;
			}
		}
		return OPEN_PARTY;
	}
	
}
