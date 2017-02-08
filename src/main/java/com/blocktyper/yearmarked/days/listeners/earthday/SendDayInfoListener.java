package com.blocktyper.yearmarked.days.listeners.earthday;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.listeners.YearmarkedListenerBase;

public class SendDayInfoListener extends YearmarkedListenerBase {

	public SendDayInfoListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onFarmersMarketStandPlaced(BlockPlaceEvent event) {

		if (event.getBlock() == null || event.getPlayer() == null) {
			return;
		}

		Player player = event.getPlayer();

		ItemStack itemInHand = plugin.getPlayerHelper().getItemInHand(player);

		if (itemInHand == null) {
			return;
		}

		if (!itemHasExpectedNbtKey(itemInHand, "")) {
			return;
		}

		int playerX = player.getLocation().getBlockX();
		int playerZ = player.getLocation().getBlockY();

		int blockX = event.getBlock().getX();
		int blockZ = event.getBlock().getZ();

		int dx = playerX - blockX;
		int dz = playerZ - blockZ;

		if ((dx == 0 && dz == 0) || (dx != 0 && dz != 0)) {
			event.setCancelled(true);
		}

		boolean zAxis = dx != 0;

		if (!placeStall(player, zAxis, event.getBlock().getLocation(), itemInHand)) {
			event.setCancelled(true);
		}
	}

	private boolean placeStall(Player player, boolean zAxis, Location location, ItemStack stallItem) {
		return false;
	}

}
