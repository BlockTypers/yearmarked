package com.blocktyper.yearmarked.days.listeners;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedListenerBase;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.special.marketday.MarketDayListener;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;

public class SendDayInfoListener extends YearmarkedListenerBase {

	public SendDayInfoListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onDayChange(DayChangeEvent event) {
		sendDayInfo(event.getDay(), event.getWorld().getPlayers());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerJoin(PlayerJoinEvent event) {
		initPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void playerChangedWorld(PlayerChangedWorldEvent event) {
		initPlayer(event.getPlayer());
	}

	private void initPlayer(Player player) {
		YearmarkedCalendar cal = new YearmarkedCalendar(player.getWorld().getFullTime());
		if (getConfig().getBoolean(ConfigKey.SHOW_JOIN_MESSAGE.getKey(), true))
			sendPlayerDayInfo(player, cal);
	}

	private void sendPlayerDayInfo(Player player, YearmarkedCalendar cal) {
		if (plugin.worldEnabled(player.getWorld().getName())) {
			List<Player> playerInAList = new ArrayList<Player>();
			playerInAList.add(player);
			sendDayInfo(cal, playerInAList);
		}
	}

	private void sendDayInfo(YearmarkedCalendar cal, List<Player> players) {
		sendDayInfo(plugin, cal, players);

	}

	public static void sendDayInfo(YearmarkedPlugin plugin, YearmarkedCalendar cal, List<Player> players) {

		plugin.debugInfo("sendDayInfo --> " + cal.getDayOfWeekEnum().getDisplayKey());

		boolean holographicDisplays = plugin.getConfig()
				.getBoolean(ConfigKey.HOLOGRAPHIC_DISPLAYS_SHOW_DAY_CHANGE_MESSAGE.getKey());

		if (holographicDisplays) {
			holographicDisplays = plugin.getServer().getPluginManager().isPluginEnabled("HolographicDisplays");
			if (!holographicDisplays) {
				plugin.getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
				plugin.warning("HolographicDisplays is not installed or not enabled.");
				plugin.warning("Turn off the following config setting if you do not want to use holographic displays: "
						+ ConfigKey.HOLOGRAPHIC_DISPLAYS_SHOW_DAY_CHANGE_MESSAGE.getKey());
			}
		}

		if (players != null && !players.isEmpty()) {
			for (Player player : players) {
				String dayName = plugin.getLocalizedMessage(cal.getDayOfWeekEnum().getDisplayKey(), player);
				String todayIs = String.format(plugin.getLocalizedMessage(LocalizedMessage.TODAY_IS.getKey(), player),
						dayName);
				String dayOfMonthMessage = new MessageFormat(
						plugin.getLocalizedMessage(LocalizedMessage.IT_IS_DAY_NUMBER.getKey(), player))
								.format(new Object[] { cal.getDayOfMonth() + "", cal.getMonthOfYear() + "",
										cal.getYear() + "" });

				player.sendMessage(ChatColor.GREEN + "#----------------");
				player.sendMessage(ChatColor.GREEN + "#----------------");
				player.sendMessage(ChatColor.YELLOW + todayIs);
				player.sendMessage(dayOfMonthMessage);
				player.sendMessage(ChatColor.GREEN + "#----------------");
				player.sendMessage(ChatColor.GREEN + "#----------------");
				
				if(MarketDayListener.isMarketDay(cal)){
					player.sendMessage(ChatColor.YELLOW + plugin.getLocalizedMessage(LocalizedMessage.MARKET_DAY.getKey(), player));
				}

				if (holographicDisplays) {
					Location where = player.getLocation();
					where.setY(where.getY() + 2);
					Hologram hologram = HologramsAPI.createHologram(plugin, where);

					VisibilityManager visibilityManager = hologram.getVisibilityManager();
					visibilityManager.showTo(player);
					visibilityManager.setVisibleByDefault(false);
					hologram.appendTextLine(ChatColor.YELLOW + todayIs);

					List<String> descriptions = LocalizedMessage.getDayDesciptions(cal.getDayOfWeekEnum(), plugin,
							player);

					if (descriptions != null && !descriptions.isEmpty()) {
						for (String description : descriptions) {
							hologram.appendTextLine(ChatColor.GREEN + description);
						}
						if(MarketDayListener.isMarketDay(cal)){
							hologram.appendTextLine(ChatColor.YELLOW + plugin.getLocalizedMessage(LocalizedMessage.MARKET_DAY.getKey(), player));
						}
					}
				}
			}
		}
	}
}
