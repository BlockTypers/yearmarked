package com.blocktyper.yearmarked.listeners;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.ConfigKeyEnum;
import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class ThordfishListener extends AbstractListener {

	public ThordfishListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void blockDamage(BlockDamageEvent event) {

		ItemStack thordFish = plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_THORDFISH, event.getPlayer(),
				null, null);
		
		if (thordFish == null) {
			plugin.debugInfo("There is no recipe defined for " + ConfigKeyEnum.RECIPE_THORDFISH.getKey());
			return;
		}
		
		if (!worldEnabled(event.getPlayer().getWorld().getName(), "Thordfish")) {
			return;
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(event.getPlayer().getWorld());
		DayOfWeekEnum dayOfWeekEnum = cal.getDayOfWeekEnum();
		if (!dayOfWeekEnum.equals(DayOfWeekEnum.MONSOONDAY) && !dayOfWeekEnum.equals(DayOfWeekEnum.DONNERSTAG)) {
			plugin.debugInfo("Not " + DayOfWeekEnum.MONSOONDAY + " or " + DayOfWeekEnum.DONNERSTAG);
			return;
		}

		ItemStack itemInHand = event.getItemInHand();

		if (itemInHand == null) {
			plugin.debugWarning("Not holding an item");
			return;
		}

		if (!itemInHand.getType().equals(Material.RAW_FISH)) {
			plugin.debugWarning("Not holding a fish");
			return;
		}

		if (itemInHand.getItemMeta() == null || itemInHand.getItemMeta().getDisplayName() == null) {
			plugin.debugWarning("Not holding fish with a name.");
			return;
		}

		boolean isThordfish = plugin.itemHasExpectedNbtKey(itemInHand, YearmarkedPlugin.RECIPE_KEY_THORDFISH);

		if (!isThordfish) {
			plugin.debugInfo("Not a Thordfish");
			return;
		}
		
		String itemName = itemInHand.getItemMeta().getDisplayName();
		
		String localizedAndTokenizedAffordMessage = plugin.getLocalizedMessage(LocalizedMessageEnum.CANT_AFFORD.getKey(), event.getPlayer());

		if (dayOfWeekEnum.equals(DayOfWeekEnum.MONSOONDAY)) {
			if (event.getPlayer().getPlayerWeather().equals(WeatherType.CLEAR)) {

				int toggleCost = plugin.getConfig()
						.getInt(ConfigKeyEnum.MONSOONDAY_RAIN_TOGGLE_ON_WITH_THORDFISH_COST.getKey(), 0);

				if (spendThorfish(event.getPlayer(), itemInHand, toggleCost)) {
					if (!plugin.getConfig().getBoolean(
							ConfigKeyEnum.MONSOONDAY_ALLOW_RAIN_TOGGLE_WITH_THORDFISH.getKey(), true)) {
						plugin.debugInfo(
								ConfigKeyEnum.MONSOONDAY_ALLOW_RAIN_TOGGLE_WITH_THORDFISH.getKey() + ": false");
						return;
					}
					event.getPlayer().sendMessage(ChatColor.DARK_BLUE + ":(");
					event.getPlayer().setPlayerWeather(WeatherType.DOWNFALL);
				} else {
					event.getPlayer().sendMessage(
							ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
									.format(new Object[] { toggleCost + itemName, }));
				}

			} else {

				int toggleCost = plugin.getConfig()
						.getInt(ConfigKeyEnum.MONSOONDAY_RAIN_TOGGLE_OFF_WITH_THORDFISH_COST.getKey(), 0);

				if (spendThorfish(event.getPlayer(), itemInHand, toggleCost)) {
					event.getPlayer().sendMessage(ChatColor.AQUA + ":)");
					event.getPlayer().setPlayerWeather(WeatherType.CLEAR);
				} else {
					event.getPlayer().sendMessage(
							ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
									.format(new Object[] { toggleCost + itemName, }));
				}
			}

		} else if (dayOfWeekEnum.equals(DayOfWeekEnum.DONNERSTAG)) {
			if (!plugin.getConfig()
					.getBoolean(ConfigKeyEnum.DONNERSTAG_ALLOW_LIGHTNING_TOGGLE_WITH_THORDFISH.getKey(), true)) {
				plugin.debugInfo(
						ConfigKeyEnum.DONNERSTAG_ALLOW_LIGHTNING_TOGGLE_WITH_THORDFISH.getKey() + ": false");
				return;
			}

			Set<String> playerExemptFromLightning = plugin.getPlayersExemptFromLightning();
			if (playerExemptFromLightning == null) {
				playerExemptFromLightning = new HashSet<String>();
			}
			if (playerExemptFromLightning.contains(event.getPlayer().getName())) {

				int toggleCost = plugin.getConfig()
						.getInt(ConfigKeyEnum.DONNERSTAG_LIGHTNING_TOGGLE_ON_WITH_THORDFISH_COST.getKey(), 0);

				plugin.debugInfo("toggleCost: " + toggleCost);
				if (spendThorfish(event.getPlayer(), itemInHand, toggleCost)) {
					playerExemptFromLightning.remove(event.getPlayer().getName());
					event.getPlayer().sendMessage(ChatColor.RED + ":(");
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),
							Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
				} else {
					event.getPlayer().sendMessage(
							ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
									.format(new Object[] { toggleCost, itemName}));
				}
			} else {

				int toggleCost = plugin.getConfig()
						.getInt(ConfigKeyEnum.DONNERSTAG_LIGHTNING_TOGGLE_OFF_WITH_THORDFISH_COST.getKey(), 1);

				plugin.debugInfo("toggleCost: " + toggleCost);
				if (spendThorfish(event.getPlayer(), itemInHand, toggleCost)) {
					playerExemptFromLightning.add(event.getPlayer().getName());
					event.getPlayer().sendMessage(ChatColor.GREEN + ":)");
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),
							Sound.BLOCK_STONE_BUTTON_CLICK_OFF, 1, 1);
				} else {
					event.getPlayer().sendMessage(
							ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
									.format(new Object[] { toggleCost, itemName}));
				}
			}
			plugin.setPlayersExemptFromLightning(playerExemptFromLightning);
		}
	}

	private boolean spendThorfish(Player player, ItemStack itemInHand, int cost) {

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

	

}
