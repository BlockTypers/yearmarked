package com.blocktyper.yearmarked;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_2_0.BlockTyperListener;
import com.blocktyper.v1_2_0.nbt.NBTItem;
import com.blocktyper.yearmarked.items.YMRecipe;

public abstract class YearmarkedListenerBase extends BlockTyperListener {
	protected Random random = new Random();

	protected YearmarkedPlugin plugin;

	public YearmarkedListenerBase(YearmarkedPlugin plugin) {
		init(plugin);
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

		if (itemInHand.getAmount() == cost) {
			player.getInventory().remove(itemInHand);
			return true;
		}

		itemInHand.setAmount(itemInHand.getAmount() - cost);

		// safety check
		if (itemInHand.getAmount() == 0) {
			player.getInventory().remove(itemInHand);
		}

		return true;
	}

	protected boolean itemHasExpectedNbtKey(ItemStack item, YMRecipe recipe) {
		return itemHasExpectedNbtKey(plugin, item, recipe);
	}

	public static boolean itemHasExpectedNbtKey(YearmarkedPlugin plugin, ItemStack item, YMRecipe recipe) {
		if (item != null && recipe != null) {
			NBTItem nbtItem = new NBTItem(item);
			if (nbtItem.hasKey(plugin.getRecipesNbtKey())) {
				String value = nbtItem.getString(plugin.getRecipesNbtKey());
				if (value != null && value.equals(recipe.key)) {
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

	protected ItemStack getItemFromRecipe(YMRecipe recipe, HumanEntity player, ItemStack baseItem, Integer amount) {
		return recipeRegistrar().getItemFromRecipe(recipe.key, player, baseItem, amount);
	}

}
