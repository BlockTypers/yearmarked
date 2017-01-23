package com.blocktyper.yearmarked;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.HumanEntity;

public enum LocalizedMessageEnum {

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

	MONSOONDAY("yearmarked-monsoonday"), 
	EARTHDAY("yearmarked-earthday"), 
	WORTAG("yearmarked-wortag"), 
	DONNERSTAG("yearmarked-donnerstag"), 
	FISHFRYDAY("yearmarked-fishfryday"), 
	DIAMONDAY("yearmarked-diamonday"), 
	FEATHERSDAY("yearmarked-feathersday");

	private String key;

	private LocalizedMessageEnum(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public static List<String> getDayDesciptions(DayOfWeekEnum dayOfWeekEnum, YearmarkedPlugin plugin,
			HumanEntity player) {
		List<String> returnList = new ArrayList<String>();
		if (dayOfWeekEnum != null) {
			String message = null;
			if (dayOfWeekEnum.equals(DayOfWeekEnum.MONSOONDAY)) {
				message = plugin.getLocalizedMessage(DESCRIPTION_MONSOONDAY.getKey(), player);
				message = new MessageFormat(message).format(new Object[] { plugin.getNameOfThordfish(player) });
				returnList.add(message);
			} else if (dayOfWeekEnum.equals(DayOfWeekEnum.EARTHDAY)) {
				returnList.add(plugin.getLocalizedMessage(DESCRIPTION_EARTHDAY.getKey(), player));
			} else if (dayOfWeekEnum.equals(DayOfWeekEnum.WORTAG)) {
				returnList.add(plugin.getLocalizedMessage(DESCRIPTION_WORTAG.getKey(), player));
			} else if (dayOfWeekEnum.equals(DayOfWeekEnum.DONNERSTAG)) {
				message = plugin.getLocalizedMessage(DESCRIPTION_DONNERSTAG.getKey(), player);
				message = new MessageFormat(message).format(new Object[] { plugin.getNameOfFishSword(player) });
				returnList.add(message);
			} else if (dayOfWeekEnum.equals(DayOfWeekEnum.FISHFRYDAY)) {
				message = plugin.getLocalizedMessage(DESCRIPTION_FISHFRYDAY.getKey(), player);
				message = new MessageFormat(message)
						.format(new Object[] { plugin.getNameOfThordfish(player), plugin.getNameOfFishSword(player) });
				returnList.add(message);
			} else if (dayOfWeekEnum.equals(DayOfWeekEnum.DIAMONDAY)) {
				returnList.add(plugin.getLocalizedMessage(DESCRIPTION_DIAMONDAY.getKey(), player));
			} else if (dayOfWeekEnum.equals(DayOfWeekEnum.FEATHERSDAY)) {
				message = plugin.getLocalizedMessage(DESCRIPTION_FEATHERSDAY.getKey(), player);
				message = new MessageFormat(message).format(new Object[] { plugin.getNameOfFishSword(player) });
				returnList.add(message);
			}
		}

		return returnList;
	}

}
