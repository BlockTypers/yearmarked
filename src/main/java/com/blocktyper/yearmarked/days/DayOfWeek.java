package com.blocktyper.yearmarked.days;

import com.blocktyper.yearmarked.LocalizedMessage;

public enum DayOfWeek {
	MONSOONDAY(1, "MONSOONDAY", LocalizedMessage.MONSOONDAY.getKey()),
	EARTHDAY(2, "EARTHDAY", LocalizedMessage.EARTHDAY.getKey()),
	WORTAG(3, "WORTAG", LocalizedMessage.WORTAG.getKey()),
	DONNERSTAG(4, "DONNERSTAG", LocalizedMessage.DONNERSTAG.getKey()),
	FISHFRYDAY(5, "FISHFRYDAY", LocalizedMessage.FISHFRYDAY.getKey()),
	DIAMONDAY(6, "DIAMONDAY", LocalizedMessage.DIAMONDAY.getKey()),
	FEATHERSDAY(7, "FEATHERSDAY", LocalizedMessage.FEATHERSDAY.getKey()),
	UNDEFINED(-1, "UNDEFINED", "UNDEFINED");

	private int dayOfWeek;
	private String code;
	private String displayKey;

	private DayOfWeek(int dayOfWeek, String code, String displayKey) {
		this.dayOfWeek = dayOfWeek;
		this.code = code;
		this.displayKey = displayKey;
	}

	public String getCode() {
		return code;
	}

	public String getDisplayKey() {
		return displayKey;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public static DayOfWeek findByNumber(int dayOfWeek) {
		for (DayOfWeek minecraftDay : DayOfWeek.values()) {
			if (minecraftDay.getDayOfWeek() == dayOfWeek)
				return minecraftDay;
		}
		return UNDEFINED;
	}

	public static DayOfWeek findByCode(String code) {
		for (DayOfWeek minecraftDay : DayOfWeek.values()) {
			if (minecraftDay.getCode().equals(code))
				return minecraftDay;
		}
		return UNDEFINED;
	}
}
