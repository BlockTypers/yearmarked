package com.blocktyper.yearmarked.monitors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.DayChangeEvent;
import com.blocktyper.yearmarked.days.listeners.donnerstag.DonnerstagUtils;

public class TimeMonitor extends BukkitRunnable {

	private YearmarkedPlugin plugin = null;
	private World world;
	private DonnerstagUtils donnerstagUtils;

	private long previousDay = 1;

	public TimeMonitor(YearmarkedPlugin plugin, String world) {
		super();
		this.plugin = plugin;
		this.world = this.plugin.getServer().getWorld(world);
		donnerstagUtils = new DonnerstagUtils(plugin, this.world);
	}

	// BEGIN BukkitRunnable
	public void run() {
		if (!plugin.worldEnabled(world.getName())) {
			plugin.debugInfo("no time monitor. world not enabled.");
			return;
		}

		plugin.debugInfo(this.getWorld().getName() + "[fulltime]: " + this.getWorld().getFullTime());

		YearmarkedCalendar cal = new YearmarkedCalendar(world.getFullTime());
		checkForDayChange(cal);
		donnerstagUtils.checkForConstantLightning(cal);
	}
	// END BukkitRunnable

	public void checkForDayChange(YearmarkedCalendar cal) {
		if (cal.getDay() != previousDay) {
			changeDay(cal);
		}

	}
	// END Public Utility Methods

	// BEGIN Private Utility Methods
	private void changeDay(YearmarkedCalendar cal) {
		previousDay = cal.getDay();
		Bukkit.getServer().getPluginManager().callEvent(new DayChangeEvent(cal, world));
	}
	// END Private Utility Methods

	// BEGIN Getters and Setters
	public World getWorld() {
		return world;
	}
	// END Getters and Setters

	public static void startWorldMonitors(YearmarkedPlugin plugin, long checkTimeInterval) {

		for (String world : plugin.getWorlds()) {
			try {
				startWorldMonitor(world, plugin, checkTimeInterval);
			} catch (IllegalArgumentException e) {
				plugin.warning("IllegalArgumentException while starting world monitor[" + world + "]. Message: "
						+ e.getMessage());
				// e.printStackTrace();
				continue;
			} catch (IllegalStateException e) {
				plugin.warning("IllegalArgumentException while starting world monitor[" + world + "]. Message: "
						+ e.getMessage());
				// e.printStackTrace();
				continue;
			} catch (Exception e) {
				plugin.warning(
						"General Exception while starting world monitor[" + world + "]. Message: " + e.getMessage());
				// e.printStackTrace();
				continue;
			}
		}
	}

	private static void startWorldMonitor(String world, YearmarkedPlugin plugin, long checkTimeInterval) {
		plugin.info("LOADING... " + plugin.getLocalizedMessage(LocalizedMessage.WORLD.getKey()) + "(" + world + ")");
		TimeMonitor timeMonitor = new TimeMonitor(plugin, world);

		if (timeMonitor.getWorld() == null) {
			plugin.warning("   -" + world + " was not recognized");
			return;
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(timeMonitor.getWorld());
		timeMonitor.checkForDayChange(cal);

		plugin.debugInfo("Checking time every " + checkTimeInterval + " sec.");
		timeMonitor.runTaskTimer(plugin, checkTimeInterval, checkTimeInterval * 20L);
	}

}
