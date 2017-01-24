package com.blocktyper.yearmarked.days.listeners.wortag;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.YearmarkedListenerBase;

public class NetherstalkListener extends YearmarkedListenerBase {

	public static final String X_KEY = "x:";
	public static final String Y_KEY = "y:";
	public static final String Z_KEY = "z:";

	private Map<String, Date> teleportCooldownMap;

	public NetherstalkListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockDamage(BlockDamageEvent event) {

		Player player = event.getPlayer();

		if (!worldEnabled(player.getWorld().getName(), "Wortag Netherwort")) {
			return;
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(player.getWorld());
		DayOfWeek dayOfWeekEnum = cal.getDayOfWeekEnum();
		if (!dayOfWeekEnum.equals(DayOfWeek.WORTAG)) {
			plugin.debugInfo("Not " + DayOfWeek.WORTAG);
			return;
		}

		ItemStack itemInHand = event.getItemInHand();

		if (itemInHand == null) {
			plugin.debugWarning("Not holding an item");
			return;
		}
		
		boolean isWortagNetherwort = itemHasExpectedNbtKey(itemInHand, YearmarkedPlugin.RECIPE_KEY_WORTAG_NETHERWORT);

		if (!isWortagNetherwort) {
			plugin.debugInfo("Not Wortag Netherwort");
			return;
		}

		boolean isWortag = dayOfWeekEnum.equals(DayOfWeek.WORTAG);

		if (isWortag) {

			boolean isTeleport = itemInHand.getItemMeta() != null && itemInHand.getItemMeta().getLore() != null
					&& itemInHand.getItemMeta().getLore().stream().anyMatch(l -> l != null && l.contains(X_KEY));

			if (isTeleport) {

				if (teleportCooldownMap == null)
					teleportCooldownMap = new HashMap<>();

				Date lastTpTime = null;
				if (teleportCooldownMap.containsKey(event.getPlayer().getName()))
					lastTpTime = teleportCooldownMap.get(event.getPlayer().getName());

				Date now = new Date();
				if (lastTpTime != null && (now.getTime() - lastTpTime.getTime()) < 2000) {
					plugin.debugWarning("TP was too fast");
					return;
				}else{
					plugin.debugWarning("TP was slow enough");
				}
				
				teleportCooldownMap.put(event.getPlayer().getName(), now);

				int x = event.getPlayer().getLocation().getBlockX();
				int y = event.getPlayer().getLocation().getBlockY();
				int z = event.getPlayer().getLocation().getBlockZ();

				for (String loreLine : itemInHand.getItemMeta().getLore()) {
					if (loreLine == null)
						continue;

					if (loreLine.contains(X_KEY)) {
						x = getIntFromKey(loreLine, X_KEY, x);
					} else if (loreLine.contains(Y_KEY)) {
						y = getIntFromKey(loreLine, Y_KEY, y);
					} else if (loreLine.contains(Z_KEY)) {
						z = getIntFromKey(loreLine, Z_KEY, z);
					}
				}

				spendNetherwort(player, itemInHand, 1);
				Location location = player.getLocation();
				location.setX(x);
				location.setY(y);
				location.setZ(z);
				player.teleport(location);

			} else {

				int teleportalCreationCost = plugin.getConfig()
						.getInt(ConfigKey.WORTAG_TELEPORTAL_CREATION_COST.getKey(), 2);

				plugin.debugInfo("teleportalCreationCost: " + teleportalCreationCost);
				if (spendNetherwort(event.getPlayer(), itemInHand, teleportalCreationCost)) {
					ItemStack newStalk = new ItemStack(Material.NETHER_STALK);
					ItemMeta itemMeta = itemInHand.getItemMeta();
					List<String> lore = new ArrayList<>();
					lore.add(X_KEY + event.getPlayer().getLocation().getBlockX());
					lore.add(Y_KEY + event.getPlayer().getLocation().getBlockY());
					lore.add(Z_KEY + event.getPlayer().getLocation().getBlockZ());
					itemMeta.setLore(lore);
					newStalk.setItemMeta(itemMeta);
					HashMap<Integer, ItemStack> remaining = player.getInventory().addItem(newStalk);
					if (remaining != null && !remaining.values().isEmpty()) {
						remaining.values().forEach(i -> player.getWorld().dropItemNaturally(player.getLocation(), i));
					}

				} else {
					String localizedAndTokenizedAffordMessage = plugin
							.getLocalizedMessage(LocalizedMessage.CANT_AFFORD.getKey(), player);
					String typeName = itemInHand.getItemMeta().getDisplayName();
					event.getPlayer().sendMessage(ChatColor.RED + new MessageFormat(localizedAndTokenizedAffordMessage)
							.format(new Object[] { teleportalCreationCost, typeName }));
				}

			}
		}
	}

	private boolean spendNetherwort(Player player, ItemStack itemInHand, int cost) {

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

	private int getIntFromKey(String val, String key, int def) {
		if (key != null && val != null && val.contains(key)) {
			try {
				String parsed = val.substring(val.indexOf(key) + key.length());
				int ret = Integer.valueOf(parsed);
				return ret;
			} catch (Exception e) {
				plugin.debugInfo("issue parsing val: " + e.getMessage());
			}
		}

		plugin.debugInfo("returning default value for key");

		return def;
	}
}
