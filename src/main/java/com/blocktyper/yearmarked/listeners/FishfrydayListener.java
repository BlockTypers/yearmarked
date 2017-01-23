package com.blocktyper.yearmarked.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.ConfigKeyEnum;
import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class FishfrydayListener extends AbstractListener {

	public FishfrydayListener(YearmarkedPlugin plugin) {
		super(plugin);

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCatchFish(PlayerFishEvent event) {
		YearmarkedCalendar cal = new YearmarkedCalendar(event.getPlayer().getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.FISHFRYDAY)) {
			return;
		}

		if (!worldEnabled(event.getPlayer().getWorld().getName(),
				plugin.getConfig().getString(DayOfWeekEnum.FISHFRYDAY.getDisplayKey()))) {
			return;
		}

		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			String doubleXp = plugin.getLocalizedMessage(LocalizedMessageEnum.DOUBLE_XP.getKey(), event.getPlayer());
			event.getPlayer().sendMessage(ChatColor.DARK_GREEN + doubleXp);
			event.setExpToDrop(event.getExpToDrop() * 2);

			boolean isOpLucky = event.getPlayer().isOp()
					&& plugin.getConfig().getBoolean(ConfigKeyEnum.FISHFRYDAY_OP_LUCK.getKey(), true);

			int percentChanceOfDiamond = plugin.getConfig()
					.getInt(ConfigKeyEnum.FISHFRYDAY_PERCENT_CHANCE_DIAMOND.getKey(), 1);
			int percentChanceOfEmerald = plugin.getConfig()
					.getInt(ConfigKeyEnum.FISHFRYDAY_PERCENT_CHANCE_EMERALD.getKey(), 10);
			int percentChanceOfGrass = plugin.getConfig().getInt(ConfigKeyEnum.FISHFRYDAY_PERCENT_CHANCE_GRASS.getKey(),
					10);
			int percentChanceOfThordfish = plugin.getConfig()
					.getInt(ConfigKeyEnum.FISHFRYDAY_PERCENT_CHANCE_THORDFISH.getKey(), 10);

			if (isOpLucky || plugin.rollIsLucky(percentChanceOfDiamond)) {
				String message = plugin.getLocalizedMessage(LocalizedMessageEnum.FISH_HAD_DIAMOND.getKey(),
						event.getPlayer());
				ItemStack reward = plugin.recipeRegistrar().getItemFromRecipe(
						YearmarkedPlugin.RECIPE_KEY_FISHFRYDAY_DIAMOND, event.getPlayer(), null, null);
				doReward(event.getPlayer(), reward, message, ChatColor.BLUE);
			}

			if (isOpLucky || plugin.rollIsLucky(percentChanceOfEmerald)) {
				String message = plugin.getLocalizedMessage(LocalizedMessageEnum.FISH_HAD_EMERALD.getKey(),
						event.getPlayer());
				ItemStack reward = plugin.recipeRegistrar().getItemFromRecipe(
						YearmarkedPlugin.RECIPE_KEY_FISHFRYDAY_EMERALD, event.getPlayer(), null, null);
				doReward(event.getPlayer(), reward, message, ChatColor.GREEN);
			}

			if (isOpLucky || plugin.rollIsLucky(percentChanceOfGrass)) {
				doReward(event.getPlayer(), new ItemStack(Material.GRASS), null, ChatColor.GREEN);
			}

			if (isOpLucky || plugin.rollIsLucky(percentChanceOfThordfish)) {
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
