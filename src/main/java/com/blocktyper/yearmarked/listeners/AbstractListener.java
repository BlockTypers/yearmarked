package com.blocktyper.yearmarked.listeners;

import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.YearmarkedPlugin;

public class AbstractListener implements Listener {
	protected YearmarkedPlugin plugin;

	public AbstractListener(YearmarkedPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	protected void warning(String msg) {
		plugin.getLogger().warning(msg);
	}

	protected void info(String msg) {
		plugin.getLogger().info(msg);
	}

	protected void dropItemsInStacks(Location location, int amount, ItemStack item) {
		if (item == null) {
			return;
		}
		// world permissions should not be checked at this low level
		if (amount > item.getType().getMaxStackSize()) {
			dropItemsInStacks(location, item.getType().getMaxStackSize(), item);
			dropItemsInStacks(location, amount - item.getType().getMaxStackSize(), item);

		} else {
			item.setAmount(amount);
			location.getWorld().dropItemNaturally(location, item);
		}
	}

	protected boolean worldEnabled(String worldName, String rewardNameToLog) {
		boolean enabled = plugin.worldEnabled(worldName);
		if (!enabled && rewardNameToLog != null) {
			plugin.debugInfo("Feature '" + rewardNameToLog + "' not enabled in the current world: " + worldName);
		}

		return enabled;
	}

}
