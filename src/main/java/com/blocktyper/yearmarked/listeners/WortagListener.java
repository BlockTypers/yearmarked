package com.blocktyper.yearmarked.listeners;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.NetherWarts;

import com.blocktyper.yearmarked.ConfigKeyEnum;
import com.blocktyper.yearmarked.DayOfWeekEnum;
import com.blocktyper.yearmarked.LocalizedMessageEnum;
import com.blocktyper.yearmarked.YearmarkedCalendar;
import com.blocktyper.yearmarked.YearmarkedPlugin;

public class WortagListener extends AbstractListener {

	private Random random = new Random();

	public WortagListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCropsBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		YearmarkedCalendar cal = new YearmarkedCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeekEnum.WORTAG)) {
			return;
		}

		if (block.getType() != Material.NETHER_WARTS) {
			return;
		}

		if (!(block.getState().getData() instanceof NetherWarts)) {
			plugin.warning("Nethwart block did not have NetherWarts state data");
			return;
		}

		NetherWarts netherWarts = (NetherWarts) block.getState().getData();

		if (netherWarts.getState() != NetherWartsState.RIPE) {
			plugin.debugInfo("Nethwarts were not ripe");
			return;
		} else {
			plugin.debugInfo("Nethwarts were ripe");
		}

		if (!worldEnabled(event.getPlayer().getWorld().getName(), DayOfWeekEnum.WORTAG.getDisplayKey())) {
			return;
		}

		int high = plugin.getConfig().getInt(ConfigKeyEnum.WORTAG_BONUS_CROPS_RANGE_HIGH.getKey(), 3);
		int low = plugin.getConfig().getInt(ConfigKeyEnum.WORTAG_BONUS_CROPS_RANGE_LOW.getKey(), 1);

		int rewardCount = random.nextInt(high + 1);

		if (rewardCount < low) {
			rewardCount = low;
		}

		if (rewardCount > 0) {
			String bonus = plugin.getLocalizedMessage(LocalizedMessageEnum.BONUS.getKey(), event.getPlayer());
			event.getPlayer().sendMessage(
					ChatColor.DARK_PURPLE + bonus + "[x" + rewardCount + "] " + block.getType().toString());
			
			ItemStack reward = plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_WORTAG_NETHERWORT, event.getPlayer(), null, null);
			
			dropItemsInStacks(block.getLocation(), rewardCount, reward);
		} else {
			plugin.debugInfo("No luck on Wortag");
			event.getPlayer().sendMessage(ChatColor.RED + ":(");
		}

	}

}
