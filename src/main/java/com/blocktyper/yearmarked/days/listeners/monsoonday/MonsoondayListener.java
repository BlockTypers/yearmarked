package com.blocktyper.yearmarked.days.listeners.monsoonday;

import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.DayChangeEvent;
import com.blocktyper.yearmarked.days.listeners.YearmarkedListenerBase;

public class MonsoondayListener extends YearmarkedListenerBase {

	public MonsoondayListener(YearmarkedPlugin plugin) {
		super(plugin);
		new ToggleRainListener(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerJoin(PlayerJoinEvent event) {
		initPlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerChangedWorld(PlayerChangedWorldEvent event) {
		initPlayer(event.getPlayer());
	}

	private void initPlayer(Player player) {
		if (!worldEnabled(player.getWorld().getName(),
				plugin.getConfig().getString(DayOfWeek.MONSOONDAY.getDisplayKey()))) {
			return;
		}
		if (!plugin.getConfig().getBoolean(ConfigKey.MONSOONDAY_RAIN.getKey(), true)) {
			plugin.debugInfo(ConfigKey.MONSOONDAY_RAIN.getKey() + ": false");
			return;
		}
		YearmarkedCalendar cal = new YearmarkedCalendar(player.getWorld().getFullTime());
		if (DayOfWeek.MONSOONDAY.equals(cal.getDayOfWeekEnum())) {
			player.setPlayerWeather(WeatherType.DOWNFALL);
		} else {
			player.setPlayerWeather(WeatherType.CLEAR);
		}
	}

	@EventHandler
	public void onDayChange(DayChangeEvent event) {
		changeDay(event.getDay(), event.getWorld());
	}

	private void changeDay(YearmarkedCalendar cal, World world) {
		if (plugin.getConfig().getBoolean(ConfigKey.MONSOONDAY_RAIN.getKey(), true)) {
			boolean isMonsoonday = cal.getDayOfWeekEnum().equals(DayOfWeek.MONSOONDAY);
			boolean isEarthday = cal.getDayOfWeekEnum().equals(DayOfWeek.EARTHDAY);
			if (world.getPlayers() != null) {
				for (Player player : world.getPlayers()) {
					if (isEarthday) {
						player.setPlayerWeather(WeatherType.CLEAR);
					} else if (isMonsoonday) {
						player.setPlayerWeather(WeatherType.DOWNFALL);
					}
				}
			}
		}
	}
}
