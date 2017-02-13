package com.blocktyper.yearmarked.days.listeners.donnerstag;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedListenerBase;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.DayChangeEvent;
import com.blocktyper.yearmarked.items.YMRecipe;

public class ToggleLightningListener extends YearmarkedListenerBase {

	public ToggleLightningListener(YearmarkedPlugin plugin) {
		super(plugin);
		
	}

	@EventHandler
	public void onDayChange(DayChangeEvent event) {
		if (yearmarkedPlugin.getPlayersExemptFromLightning() == null) {
			yearmarkedPlugin.setPlayersExemptFromLightning(new HashSet<String>());
		} else {
			yearmarkedPlugin.getPlayersExemptFromLightning().clear();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockDamage(BlockDamageEvent event) {

		ItemStack thordFish = getItemFromRecipe(YMRecipe.THORDFISH, event.getPlayer(), null, null);

		if (thordFish == null) {
			debugInfo("There is no recipe defined for Thordfish");
			return;
		}

		if (!worldEnabled(event.getPlayer().getWorld().getName(), "Thordfish")) {
			return;
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(event.getPlayer().getWorld());
		DayOfWeek dayOfWeekEnum = cal.getDayOfWeekEnum();
		if (!dayOfWeekEnum.equals(DayOfWeek.DONNERSTAG)) {
			debugInfo("Not " + DayOfWeek.DONNERSTAG);
			return;
		}

		ItemStack itemInHand = event.getItemInHand();

		if (itemInHand == null) {
			debugWarning("Not holding an item");
			return;
		}

		boolean isThordfish = itemHasExpectedNbtKey(itemInHand, YMRecipe.THORDFISH);

		if (!isThordfish) {
			debugInfo("Not a Thordfish");
			return;
		}

		String itemName = itemInHand.getItemMeta().getDisplayName();

		String localizedAndTokenizedAffordMessage = getLocalizedMessage(LocalizedMessage.CANT_AFFORD.getKey(),
				event.getPlayer());

		if (!getConfig().getBoolean(ConfigKey.DONNERSTAG_ALLOW_LIGHTNING_TOGGLE_WITH_THORDFISH.getKey(), true)) {
			debugInfo(ConfigKey.DONNERSTAG_ALLOW_LIGHTNING_TOGGLE_WITH_THORDFISH.getKey() + ": false");
			return;
		}

		Set<String> playerExemptFromLightning = yearmarkedPlugin.getPlayersExemptFromLightning();
		if (playerExemptFromLightning == null) {
			playerExemptFromLightning = new HashSet<String>();
		}
		if (playerExemptFromLightning.contains(event.getPlayer().getName())) {

			int toggleCost = getConfig()
					.getInt(ConfigKey.DONNERSTAG_LIGHTNING_TOGGLE_ON_WITH_THORDFISH_COST.getKey(), 0);

			debugInfo("toggleCost: " + toggleCost);
			if (spendItemInHand(event.getPlayer(), itemInHand, toggleCost)) {
				playerExemptFromLightning.remove(event.getPlayer().getName());
				event.getPlayer().sendMessage(ChatColor.RED + ":(");
				event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),
						Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
						.format(new Object[] { toggleCost, itemName }));
			}
		} else {

			int toggleCost = getConfig()
					.getInt(ConfigKey.DONNERSTAG_LIGHTNING_TOGGLE_OFF_WITH_THORDFISH_COST.getKey(), 1);

			debugInfo("toggleCost: " + toggleCost);
			if (spendItemInHand(event.getPlayer(), itemInHand, toggleCost)) {
				playerExemptFromLightning.add(event.getPlayer().getName());
				event.getPlayer().sendMessage(ChatColor.GREEN + ":)");
				event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),
						Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
			} else {
				event.getPlayer().sendMessage(ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
						.format(new Object[] { toggleCost, itemName }));
			}
		}
		yearmarkedPlugin.setPlayersExemptFromLightning(playerExemptFromLightning);
	}
}
