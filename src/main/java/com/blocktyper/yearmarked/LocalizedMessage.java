package com.blocktyper.yearmarked;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.items.YMRecipe;

public enum LocalizedMessage {

	WORLD("yearmarked-world"), 
	WORLDS("yearmarked-worlds"), 
	WORLD_NOT_ENABLED("yearmarked-word-not-enabled"), 
	ONLY_FOR_OP("yearmarked-only-for-op"), 
	NO_PERMISSION("yearmarked-no-permission"), 
	YM_COMMAND_NOT_A_VALID_NUMBER("yearmarked-ym-command-not-a-valid-number"),
	BONUS("yearmarked-bonus"),
	FALL_DAMAGE_PREVENTED("yearmarked-fall-damage-prevented"), 
	DOUBLE_XP("yearmarked-double-xp"), 
	FISH_HAD_DIAMOND("yearmarked-fish-had-diamond"), 
	FISH_HAD_EMERALD("yearmarked-fish-had-emerald"), 
	TODAY_IS("yearmarked-today-is"),
	SUPER_CREEPER_HAD_DIAMOND("yearmarked-super-creeper-had-diamond"), 
	SUPER_CREEPER_HAD_EMERALD("yearmarked-super-creeper-had-emerald"), 
	SUPER_CREEPER_HAD_THORDFISH("yearmarked-super-creeper-had-thordfish"),

	IT_IS_DAY_NUMBER("yearmarked-it-is-day-number"), 
	OF_MONTH_NUMBER("yearmarked-of-month-number"), 
	OF_YEAR_NUMBER("yearmarked-of-year-number"),

	SUPER_CREEPER_HAD_A_DIAMOND("yearmarked-super-creeper-had-diamond"), 
	SUPER_CREEPER_HAD_AN_EMERALD("yearmarked-super-creeper-had-emerald"), 
	SUPER_CREEPER_HAD_A_THORDFISH("yearmarked-super-creeper-had-thordfish"),

	CANT_AFFORD("yearmarked-cant-afford"),

	DESCRIPTION_MONSOONDAY("yearmarked-monsoonday-description"), 
	DESCRIPTION_EARTHDAY("yearmarked-earthday-description"), 
	DESCRIPTION_WORTAG("yearmarked-wortag-description"), 
	DESCRIPTION_DONNERSTAG("yearmarked-donnerstag-description"), 
	DESCRIPTION_FISHFRYDAY("yearmarked-fishfrytag-description"), 
	DESCRIPTION_DIAMONDAY("yearmarked-diamonday-description"), 
	DESCRIPTION_FEATHERSDAY("yearmarked-feathersday-description"),
	
	MARKET_DAY("yearmarked-market-day"),
	NOT_MARKET_DAY("yearmarked-not-market-day"),
	SOLD_OUT("yearmarked-sold-out"),

	MONSOONDAY("yearmarked-monsoonday"), 
	EARTHDAY("yearmarked-earthday"), 
	WORTAG("yearmarked-wortag"), 
	DONNERSTAG("yearmarked-donnerstag"), 
	FISHFRYDAY("yearmarked-fishfryday"), 
	DIAMONDAY("yearmarked-diamonday"), 
	FEATHERSDAY("yearmarked-feathersday");

	private String key;

	private LocalizedMessage(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public static List<String> getDayDesciptions(DayOfWeek dayOfWeekEnum, YearmarkedPlugin plugin,
			HumanEntity player) {
		List<String> returnList = new ArrayList<String>();
		if (dayOfWeekEnum != null) {
			String message = null;
			if (dayOfWeekEnum.equals(DayOfWeek.MONSOONDAY)) {
				message = plugin.getLocalizedMessage(DESCRIPTION_MONSOONDAY.getKey(), player);
				message = new MessageFormat(message).format(new Object[] { getNameOfThordfish(player, plugin) });
				returnList.add(message);
			} else if (dayOfWeekEnum.equals(DayOfWeek.EARTHDAY)) {
				returnList.add(plugin.getLocalizedMessage(DESCRIPTION_EARTHDAY.getKey(), player));
			} else if (dayOfWeekEnum.equals(DayOfWeek.WORTAG)) {
				returnList.add(plugin.getLocalizedMessage(DESCRIPTION_WORTAG.getKey(), player));
			} else if (dayOfWeekEnum.equals(DayOfWeek.DONNERSTAG)) {
				message = plugin.getLocalizedMessage(DESCRIPTION_DONNERSTAG.getKey(), player);
				message = new MessageFormat(message).format(new Object[] { getNameOfFishSword(player, plugin) });
				returnList.add(message);
			} else if (dayOfWeekEnum.equals(DayOfWeek.FISHFRYDAY)) {
				message = plugin.getLocalizedMessage(DESCRIPTION_FISHFRYDAY.getKey(), player);
				message = new MessageFormat(message)
						.format(new Object[] { getNameOfThordfish(player, plugin), getNameOfFishSword(player, plugin) });
				returnList.add(message);
			} else if (dayOfWeekEnum.equals(DayOfWeek.DIAMONDAY)) {
				returnList.add(plugin.getLocalizedMessage(DESCRIPTION_DIAMONDAY.getKey(), player));
			} else if (dayOfWeekEnum.equals(DayOfWeek.FEATHERSDAY)) {
				message = plugin.getLocalizedMessage(DESCRIPTION_FEATHERSDAY.getKey(), player);
				message = new MessageFormat(message).format(new Object[] { getNameOfFishSword(player, plugin) });
				returnList.add(message);
			}
		}

		return returnList;
	}
	
	public static String getNameOfLightningInhibitor(HumanEntity player, YearmarkedPlugin plugin) {
		ItemStack item = plugin.recipeRegistrar().getItemFromRecipe(YMRecipe.LIGHTNING_INHIBITOR.key, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public static String getNameOfEarthdayPotPie(HumanEntity player, YearmarkedPlugin plugin) {
		ItemStack item = plugin.recipeRegistrar().getItemFromRecipe(YMRecipe.EARTHDAY_POT_PIE.key, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public static String getNameOfThordfish(HumanEntity player, YearmarkedPlugin plugin) {
		ItemStack item = plugin.recipeRegistrar().getItemFromRecipe(YMRecipe.THORDFISH.key, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public static String getNameOfFishSword(HumanEntity player, YearmarkedPlugin plugin) {
		ItemStack item = plugin.recipeRegistrar().getItemFromRecipe(YMRecipe.FISH_SWORD.key, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public static String getNameOfFishArrow(HumanEntity player, YearmarkedPlugin plugin) {
		ItemStack item = plugin.recipeRegistrar().getItemFromRecipe(YMRecipe.FISH_ARROW.key, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public static String getNameOfWortagNetherwort(HumanEntity player, YearmarkedPlugin plugin) {
		ItemStack item = plugin.recipeRegistrar().getItemFromRecipe(YMRecipe.WORTAG_NETHERWORT.key, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

}
