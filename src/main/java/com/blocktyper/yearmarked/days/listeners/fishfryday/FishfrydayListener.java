package com.blocktyper.yearmarked.days.listeners.fishfryday;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.YearmarkedListenerBase;

public class FishfrydayListener extends YearmarkedListenerBase {

	public FishfrydayListener(YearmarkedPlugin plugin) {
		super(plugin);

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCatchFish(PlayerFishEvent event) {
		YearmarkedCalendar cal = new YearmarkedCalendar(event.getPlayer().getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeek.FISHFRYDAY)) {
			return;
		}

		if (!worldEnabled(event.getPlayer().getWorld().getName(),
				plugin.getConfig().getString(DayOfWeek.FISHFRYDAY.getDisplayKey()))) {
			return;
		}

		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			String doubleXp = plugin.getLocalizedMessage(LocalizedMessage.DOUBLE_XP.getKey(), event.getPlayer());
			event.getPlayer().sendMessage(ChatColor.DARK_GREEN + doubleXp);
			event.setExpToDrop(event.getExpToDrop() * 2);

			boolean isOpLucky = event.getPlayer().isOp()
					&& plugin.getConfig().getBoolean(ConfigKey.FISHFRYDAY_OP_LUCK.getKey(), true);

			int percentChanceOfDiamond = plugin.getConfig()
					.getInt(ConfigKey.FISHFRYDAY_PERCENT_CHANCE_DIAMOND.getKey(), 1);
			int percentChanceOfEmerald = plugin.getConfig()
					.getInt(ConfigKey.FISHFRYDAY_PERCENT_CHANCE_EMERALD.getKey(), 10);
			int percentChanceOfGrass = plugin.getConfig().getInt(ConfigKey.FISHFRYDAY_PERCENT_CHANCE_GRASS.getKey(),
					10);
			int percentChanceOfThordfish = plugin.getConfig()
					.getInt(ConfigKey.FISHFRYDAY_PERCENT_CHANCE_THORDFISH.getKey(), 10);

			if (isOpLucky || rollIsLucky(percentChanceOfDiamond)) {
				String message = plugin.getLocalizedMessage(LocalizedMessage.FISH_HAD_DIAMOND.getKey(),
						event.getPlayer());
				ItemStack reward = plugin.recipeRegistrar().getItemFromRecipe(
						YearmarkedPlugin.RECIPE_KEY_FISHFRYDAY_DIAMOND, event.getPlayer(), null, null);
				doReward(event.getPlayer(), reward, message, ChatColor.BLUE);
			}

			if (isOpLucky || rollIsLucky(percentChanceOfEmerald)) {
				String message = plugin.getLocalizedMessage(LocalizedMessage.FISH_HAD_EMERALD.getKey(),
						event.getPlayer());
				ItemStack reward = plugin.recipeRegistrar().getItemFromRecipe(
						YearmarkedPlugin.RECIPE_KEY_FISHFRYDAY_EMERALD, event.getPlayer(), null, null);
				doReward(event.getPlayer(), reward, message, ChatColor.GREEN);
			}

			if (isOpLucky || rollIsLucky(percentChanceOfGrass)) {
				doReward(event.getPlayer(), new ItemStack(Material.GRASS), null, ChatColor.GREEN);
			}

			if (isOpLucky || rollIsLucky(percentChanceOfThordfish)) {
				ItemStack reward = plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_THORDFISH,
						event.getPlayer(), null, null);
				doReward(event.getPlayer(), reward, reward.getItemMeta().getDisplayName() + "!", ChatColor.DARK_GREEN);
			}

			if (isOpLucky) {
				event.getPlayer().sendMessage(ChatColor.GOLD + "OP!");
			}

		}
	}

	private void doReward(Player player, ItemStack reward, String message, ChatColor color) {
		if (reward != null) {
			player.getWorld().dropItem(player.getLocation(), reward);
			if (message != null)
				player.sendMessage(color + message);
		}
	}
}
