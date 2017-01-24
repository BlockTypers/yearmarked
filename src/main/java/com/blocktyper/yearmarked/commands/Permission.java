package com.blocktyper.yearmarked.commands;

public enum Permission {
	TIMELORD("yearmarked.timelord");
	
	
	private String name;
	
	private Permission(String name){
		this.name= name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
