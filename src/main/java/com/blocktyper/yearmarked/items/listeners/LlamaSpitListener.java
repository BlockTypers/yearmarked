package com.blocktyper.yearmarked.items.listeners;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.blocktyper.yearmarked.YearmarkedListenerBase;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.items.YMRecipe;

public class LlamaSpitListener extends YearmarkedListenerBase {

	public LlamaSpitListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	/*
	 * @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	 * public void playerSwingsSpitWand(PlayerInteractEvent event) {
	 * spit(event.getPlayer(), event); }
	 */

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDropsSpitWand(PlayerDropItemEvent event) {
		spit(event.getPlayer(), event);
	}

	private void spit(Player player, Cancellable event) {
		if (!itemHasExpectedNbtKey(getPlayerHelper().getItemInHand(player), YMRecipe.LLAMA_SPIT_WAND)) {
			return;
		}

		World world = player.getWorld();
		LlamaSpit spit = (LlamaSpit) world.spawnEntity(player.getLocation().add(0, .5, 0), EntityType.LLAMA_SPIT);
		spit.setShooter(player);

		Location playerLocation = player.getLocation().clone();

		spit.setVelocity(playerLocation.getDirection().multiply(12));

		world.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SPIT, 3F, .5F);

		event.setCancelled(true);
	}

}
