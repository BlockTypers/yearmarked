package com.blocktyper.yearmarked.days.listeners.monsoonday;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.YearmarkedListenerBase;

public class ToggleRainListener extends YearmarkedListenerBase {

	public ToggleRainListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockDamage(BlockDamageEvent event) {
		
		if (!worldEnabled(event.getPlayer().getWorld().getName(), "Thordfish")) {
			return;
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(event.getPlayer().getWorld());
		DayOfWeek dayOfWeekEnum = cal.getDayOfWeekEnum();
		if (!dayOfWeekEnum.equals(DayOfWeek.MONSOONDAY)) {
			debugInfo("Not " + DayOfWeek.MONSOONDAY);
			return;
		}

		ItemStack itemInHand = event.getItemInHand();

		if (itemInHand == null) {
			debugWarning("Not holding an item");
			return;
		}

		boolean isThordfish = itemHasExpectedNbtKey(itemInHand, YearmarkedPlugin.RECIPE_KEY_THORDFISH);

		if (!isThordfish) {
			debugInfo("Not a Thordfish");
			return;
		}

		String itemName = itemInHand.getItemMeta().getDisplayName();

		String localizedAndTokenizedAffordMessage = plugin
				.getLocalizedMessage(LocalizedMessage.CANT_AFFORD.getKey(), event.getPlayer());

		if (event.getPlayer().getPlayerWeather().equals(WeatherType.CLEAR)) {

			int toggleCost = getConfig()
					.getInt(ConfigKey.MONSOONDAY_RAIN_TOGGLE_ON_WITH_THORDFISH_COST.getKey(), 0);

			if (spendItemInHand(event.getPlayer(), itemInHand, toggleCost)) {
				if (!getConfig().getBoolean(ConfigKey.MONSOONDAY_ALLOW_RAIN_TOGGLE_WITH_THORDFISH.getKey(),
						true)) {
					debugInfo(ConfigKey.MONSOONDAY_ALLOW_RAIN_TOGGLE_WITH_THORDFISH.getKey() + ": false");
					return;
				}
				event.getPlayer().sendMessage(ChatColor.DARK_BLUE + ":(");
				event.getPlayer().setPlayerWeather(WeatherType.DOWNFALL);
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
						.format(new Object[] { toggleCost + itemName, }));
			}

		} else {

			int toggleCost = getConfig()
					.getInt(ConfigKey.MONSOONDAY_RAIN_TOGGLE_OFF_WITH_THORDFISH_COST.getKey(), 0);

			if (spendItemInHand(event.getPlayer(), itemInHand, toggleCost)) {
				event.getPlayer().sendMessage(ChatColor.AQUA + ":)");
				event.getPlayer().setPlayerWeather(WeatherType.CLEAR);
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
						.format(new Object[] { toggleCost + itemName, }));
			}
		}
	}

}
