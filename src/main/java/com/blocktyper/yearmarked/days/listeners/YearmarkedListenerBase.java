package com.blocktyper.yearmarked.days.listeners;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_2_0.nbt.NBTItem;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.listeners.diamonday.DiamondayListener;
import com.blocktyper.yearmarked.days.listeners.donnerstag.DonnerstagListener;
import com.blocktyper.yearmarked.days.listeners.earthday.EarthdayListener;
import com.blocktyper.yearmarked.days.listeners.feathersday.FeathersdayListener;
import com.blocktyper.yearmarked.days.listeners.fishfryday.FishfrydayListener;
import com.blocktyper.yearmarked.days.listeners.monsoonday.MonsoondayListener;
import com.blocktyper.yearmarked.days.listeners.wortag.WortagListener;

public abstract class YearmarkedListenerBase implements Listener {
	private Random random = new Random();
	
	protected YearmarkedPlugin plugin;

	public YearmarkedListenerBase(YearmarkedPlugin plugin) {
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
	
	protected boolean spendItemInHand(Player player, ItemStack itemInHand, int cost) {

		if (itemInHand.getAmount() == 0 || itemInHand.getAmount() < cost)
			return false;
		
		if(itemInHand.getAmount() == cost){
			player.getInventory().remove(itemInHand);
			return true;
		}

		itemInHand.setAmount(itemInHand.getAmount() - cost);
		
		//safety check
		if(itemInHand.getAmount() == 0){
			player.getInventory().remove(itemInHand);
		}

		return true;
	}
	
	protected boolean itemHasExpectedNbtKey(ItemStack item, String expectedKey) {
		return itemHasExpectedNbtKey(plugin, item, expectedKey);
	}
	
	public static boolean itemHasExpectedNbtKey(YearmarkedPlugin plugin, ItemStack item, String expectedKey) {
		if (item != null && expectedKey != null) {
			NBTItem nbtItem = new NBTItem(item);
			if (nbtItem.hasKey(plugin.getRecipesNbtKey())) {
				String value = nbtItem.getString(plugin.getRecipesNbtKey());
				if (value != null && value.equals(expectedKey)) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean worldEnabled(String worldName, String rewardNameToLog) {
		boolean enabled = plugin.worldEnabled(worldName);
		if (!enabled && rewardNameToLog != null) {
			plugin.debugInfo("Feature '" + rewardNameToLog + "' not enabled in the current world: " + worldName);
		}

		return enabled;
	}
	
	protected boolean rollIsLucky(double percentChanceOfTrue) {
		return rollIsLucky(percentChanceOfTrue, random);
	}
	
	public static boolean rollIsLucky(double percentChanceOfTrue, Random random) {
		if (percentChanceOfTrue <= 0 || (percentChanceOfTrue < 100 && random.nextDouble() > percentChanceOfTrue)) {
			return false;
		} else if (percentChanceOfTrue >= 100) {
			return true;
		} else if (percentChanceOfTrue <= random.nextDouble()) {
			return true;
		}
		return false;
	}
	
	
	public static void registerListeners(YearmarkedPlugin plugin) {
		//days
		new MonsoondayListener(plugin);
		new EarthdayListener(plugin);
		new WortagListener(plugin);
		new DonnerstagListener(plugin);
		new FishfrydayListener(plugin);
		new DiamondayListener(plugin);
		new FeathersdayListener(plugin);
		
		//send day info
		new SendDayInfoListener(plugin);
		
		
		//migration
		new DataMigrationListener_2_0_0(plugin);
	}

}
