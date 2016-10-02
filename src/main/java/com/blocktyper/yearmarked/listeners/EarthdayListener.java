package com.blocktyper.yearmarked.listeners;

import java.util.Random;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import com.blocktyper.yearmarked.MinecraftCalendar;
import com.blocktyper.yearmarked.MinecraftDayOfWeekEnum;
import com.blocktyper.yearmarked.YearmarkedPlugin;

import net.md_5.bungee.api.ChatColor;

public class EarthdayListener extends AbstractListener {

	private Random random = new Random();

	public EarthdayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onCropsBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		if (block.getType() != Material.CROPS && block.getType() != Material.CARROT
				&& block.getType() != Material.POTATO) {
			return;
		}

		MinecraftCalendar cal = new MinecraftCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(MinecraftDayOfWeekEnum.EARTHDAY)) {
			return;
		}

		if (block.getType() != Material.CROPS && block.getType() != Material.CARROT
				&& block.getType() != Material.POTATO) {
			return;
		}

		if (((Crops) block.getState().getData()).getState() != CropState.RIPE) {
			return;
		}

		int rewardCount = random.nextInt(3) + 1;
		event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "bonus[x" + rewardCount + "] " + block.getType().toString());
		reward(block, rewardCount);
	}

	private void reward(Block block, int rewardCount) {

		Material reward = Material.WHEAT;
		if (block.getType() == Material.CROPS) {
			reward = Material.WHEAT;
		} else if (block.getType() == Material.CARROT) {
			reward = Material.CARROT_ITEM;
		} else if (block.getType() == Material.POTATO) {
			reward = Material.POTATO_ITEM;
		} else {
			reward = Material.GRASS;
		}

		block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(reward, rewardCount));
	}
}
