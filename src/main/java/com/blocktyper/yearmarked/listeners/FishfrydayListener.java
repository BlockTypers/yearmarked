package com.blocktyper.yearmarked.listeners;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.YearmarkedPlugin;

public class FishfrydayListener extends AbstractListener {

	private Random random = new Random();

	public FishfrydayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerCatchFish(PlayerFishEvent event) {
		if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "Double XP!");
			event.setExpToDrop(event.getExpToDrop() * 2);

			Material reward = null;
			String message = null;
			ChatColor color = null;
			if (random.nextInt(100) == 50) {
				reward = Material.DIAMOND;
				message = "This fish had a diamond caught in its mouth!";
				color = ChatColor.BLUE;
			} else if (random.nextInt(10) == 5) {
				reward = Material.EMERALD;
				message = "This fish had an emerald caught in its mouth!";
				color = ChatColor.GREEN;
			}

			if (reward != null) {
				ItemStack emerald = new ItemStack(reward);
				event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), emerald);
				event.getPlayer().sendMessage(color + message);
			}
		}
	}
}
