package com.blocktyper.yearmarked;

public enum ConfigKeyEnum {
	
	WORLDS("yearmarked-worlds"),
	
	// MONSOONDAY
	MONSOONDAY("yearmarked-monsoonday"),
	MONSOONDAY_RAIN("yearmarked-monsoonday-rain"),
	MONSOONDAY_ALLOW_RAIN_TOGGLE_WITH_THORDFISH("yearmarked-monsoonday-allow-rain-toggle-with-thordfish"),
	MONSOONDAY_RAIN_TOGGLE_OFF_WITH_THORDFISH_COST("yearmarked-monsoonday-rain-toggle-off-with-thordfish-cost"),
	MONSOONDAY_RAIN_TOGGLE_ON_WITH_THORDFISH_COST("yearmarked-monsoonday-rain-toggle-on-with-thordfish-cost"),

	// EARTHDAY
	EARTHDAY("yearmarked-earthday"),
	EARTHDAY_BONUS_CROPS("yearmarked-earthday-bonus-crops"),
	EARTHDAY_BONUS_CROPS_RANGE_HIGH("yearmarked-earthday-bonus-crops-range-high"),
	EARTHDAY_BONUS_CROPS_RANGE_LOW("yearmarked-earthday-bonus-crops-range-low"),
	EARTHDAY_POT_PIE_BUFF_DURATION_SEC("yearmarked-earthday-pot-pie-buff-duration-sec"),
	EARTHDAY_POT_PIE_BUFF_MAGNITUDE("yearmarked-earthday-pot-pie-buff-magnitude"),
	EARTHDAY_POT_PIE_AFFECT_ARROWS_DURATION_SEC("yearmarked-earthday-pot-pie-affect-entity-arrows-duration-sec"),
	EARTHDAY_ALLOW_ENTITY_ARROWS("yearmarked-earthday-allow-entity-arrows"),
	EARTHDAY_ALLOW_ENTITY_ARROWS_COSTS("yearmarked-earthday-entity-arrows-costs"),
	
	// WORTAG 
	WORTAG("yearmarked-wortag"),
	WORTAG_BONUS_CROPS("yearmarked-wortag-bonus-crops"),
	WORTAG_BONUS_CROPS_RANGE_HIGH("yearmarked-wortag-bonus-crops-range-high"),
	WORTAG_BONUS_CROPS_RANGE_LOW("yearmarked-wortag-bonus-crops-range-low"),

	// DONNERSTAG
	DONNERSTAG("yearmarked-donnerstag"),
	DONNERSTAG_LIGHTNING("yearmarked-donnerstag-lightning"),
	DONNERSTAG_ALLOW_LIGHTNING_TOGGLE_WITH_THORDFISH("yearmarked-donnerstag-allow-lightning-toggle-with-thordfish"),
	DONNERSTAG_LIGHTNING_TOGGLE_OFF_WITH_THORDFISH_COST("yearmarked-donnerstag-lightning-toggle-off-with-thordfish-cost"),
	DONNERSTAG_LIGHTNING_TOGGLE_ON_WITH_THORDFISH_COST("yearmarked-donnerstag-lightning-toggle-on-with-thordfish-cost"),

	DONNERSTAG_ALLOW_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD("yearmarked-donnerstag-allow-super-creeper-spawn-with-fish-sword"),
	DONNERSTAG_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD_PERCENT_CHANCE("yearmarked-donnerstag-super-creeper-spawn-with-fish-sword-percent-chance"),
	DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_DIAMOND_PERCENT_CHANCE("yearmarked-donnerstag-super-creeper-drops-diamond-percent-chance"),
	DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_EMERALD_PERCENT_CHANCE("yearmarked-donnerstag-super-creeper-drops-emerald-percent-chance"),
	DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_THORDFISH_PERCENT_CHANCE("yearmarked-donnerstag-super-creeper-drops-thordfish-percent-chance"),
	DONNERSTAG_SUPER_CREEPER_OP_LUCK("yearmarked-donnerstag-super-creeper-op-luck"),
	DONNERSTAG_LIGHTNING_INHIBITOR_RANGE("yearmarked-donnerstag-lightning-inhibitor-range"),

	// FISHFRYDAY
	FISHFRYDAY("yearmarked-fishfryday"),
	FISHFRYDAY_BINUS_XP_MULTIPLIER("yearmarked-fishfryday-bonus-xp-multiplier"),
	FISHFRYDAY_PERCENT_CHANCE_DIAMOND("yearmarked-fishfryday-percent-chance-diamond"),
	FISHFRYDAY_PERCENT_CHANCE_EMERALD("yearmarked-fishfryday-percent-chance-emerald"),
	FISHFRYDAY_PERCENT_CHANCE_GRASS("yearmarked-fishfryday-percent-chance-grass"),
	FISHFRYDAY_PERCENT_CHANCE_THORDFISH("yearmarked-fishfryday-percent-chance-thordfish"),
	FISHFRYDAY_OP_LUCK("yearmarked-fishfryday-op-luck"),

	// DIAMONDAY
	DIAMONDAY("yearmarked-diamonday"),
	DIAMONDAY_BONUS_DIAMONDS("yearmarked-diamonday-bonus-diamonds"),
	DIAMONDAY_BONUS_DIAMONDS_RANGE_HIGH("yearmarked-diamonday-bonus-diamonds-range-high"),
	DIAMONDAY_BONUS_DIAMONDS_RANGE_LOW("yearmarked-diamonday-bonus-diamonds-range-low"),

	// FEATHERSDAY
	FEATHERSDAY("yearmarked-feathersday"),
	FEATHERSDAY_PREVENT_FALL_DAMAGE("yearmarked-feathersday-prevent-fall-damage"),
	FEATHERSDAY_BOUNCE("yearmarked-feathersday-bounce"),
	FEATHERSDAY_BOUNCE_XZ_MULTIPLIER("yearmarked-feathersday-bounce-xz-multiplier"),
	
	//RECIPES
	RECIPE_THORDFISH("recipe.thord-fish.name"),
	RECIPE_FISH_SWORD("recipe.fish-sword.name"),
	RECIPE_FISH_ARROW("recipe.fish-arrow.name"),
	RECIPE_EARTHDAY_POT_PIE("recipe.earth-day-pot-pie.name"),
	RECIPE_LIGHTNING_INHIBITOR("recipe.lightning-inhibitor.name");


	private String key;

	private ConfigKeyEnum(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
