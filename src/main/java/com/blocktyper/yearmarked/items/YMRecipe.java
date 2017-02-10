package com.blocktyper.yearmarked.items;

public enum YMRecipe {
	THORDFISH("thord-fish"),
	DIAMONDAY_SWORD("diamonday-sword"),
	FISH_SWORD("fish-sword"),
	FISH_ARROW("fish-arrow"),
	EARTHDAY_POT_PIE("earth-day-pot-pie"),
	LIGHTNING_INHIBITOR("lightning-inhibitor"),
	DIAMONDAY_DIAMOND("diamonday-diamond"),
	FISHFRYDAY_DIAMOND("fishfryday-diamond"),
	FISHFRYDAY_EMERALD("fishfryday-emerald"),
	FISHFRYDAY_GRASS("fishfryday-grass"),
	EARTHDAY_WHEAT("earthday-wheat"),
	EARTHDAY_CARROT("earthday-carrot"),
	EARTHDAY_POTATO("earthday-potato"),
	WORTAG_NETHERWORT("wortag-netherwort"),
	LLAMA_SPIT_BUCKET("llama-spit-bucket"),
	LLAMA_SPIT_WAND("llama-spit-wand");
	
	public String key;

	private YMRecipe(String key) {
		this.key = key;
	}
}
