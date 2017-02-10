package com.blocktyper.yearmarked.days.listeners.feathersday;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedListenerBase;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.items.YMRecipe;

public class FeathersdayListener extends YearmarkedListenerBase {

	public FeathersdayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}
	
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerFallDamageEvent(final EntityDamageEvent event) {
		YearmarkedCalendar cal = new YearmarkedCalendar(event.getEntity().getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeek.FEATHERSDAY)) {
			return;
		}
		if (!(event.getEntity() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getEntity();
		if (event.getCause() == DamageCause.FALL) {
			
			if (!getConfig().getBoolean(ConfigKey.FEATHERSDAY_PREVENT_FALL_DAMAGE.getKey(), true)) {
				debugInfo(ConfigKey.FEATHERSDAY_PREVENT_FALL_DAMAGE.getKey() + ": false");
				return;
			}
			
			if (!worldEnabled(player.getWorld().getName(), getConfig().getString(DayOfWeek.FEATHERSDAY.getDisplayKey()))) {
				return;
			}
			
			String fallDamagePrevented = plugin
					.getLocalizedMessage(LocalizedMessage.FALL_DAMAGE_PREVENTED.getKey(), player);
			player.sendMessage(ChatColor.YELLOW + fallDamagePrevented);
			event.setCancelled(true);

			if (!getConfig().getBoolean(ConfigKey.FEATHERSDAY_BOUNCE.getKey(), true)) {
				debugInfo(ConfigKey.FEATHERSDAY_BOUNCE.getKey() + ": false");
				return;
			}

			ItemStack itemInHand = getPlayerHelper().getItemInHand(player);

			if (itemInHand == null) {
				debugInfo("Item in hand was null");
				return;
			}

			boolean isFishSword = itemHasExpectedNbtKey(itemInHand, YMRecipe.FISH_SWORD);
			boolean isDiamondFishSword = itemHasExpectedNbtKey(itemInHand, YMRecipe.DIAMONDAY_SWORD);

			if (!isFishSword && !isDiamondFishSword) {
				debugInfo("Item in hand is not a fish sword'");
				return;
			}

			Double amoundToSpeedXAndZ = getConfig()
					.getDouble(ConfigKey.FEATHERSDAY_BOUNCE_XZ_MULTIPLIER.getKey(), 2.5);

			Vector velocity = player.getVelocity();
			velocity.setY(10.0);
			velocity.setX(velocity.getX() * amoundToSpeedXAndZ);
			velocity.setZ(velocity.getZ() * amoundToSpeedXAndZ);
			player.setVelocity(velocity);
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);

			return;
		}
	}

}
