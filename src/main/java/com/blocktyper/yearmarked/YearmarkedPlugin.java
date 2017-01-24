package com.blocktyper.yearmarked;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.bukkit.scheduler.BukkitRunnable;

import com.blocktyper.v1_1_8.plugin.BlockTyperPlugin;
import com.blocktyper.yearmarked.commands.YmCommand;
import com.blocktyper.yearmarked.days.listeners.YearmarkedListenerBase;
import com.blocktyper.yearmarked.days.listeners.earthday.EarthdayListener;
import com.blocktyper.yearmarked.monitors.HolographicDisplayMonitor;
import com.blocktyper.yearmarked.monitors.TimeMonitor;

public class YearmarkedPlugin extends BlockTyperPlugin {

	public static String NBT_RECIPE_KEY = "YearmarkedPluginNBTRecipeKey";

	public static final String RESOURCE_NAME = "com.blocktyper.yearmarked.resources.YearmarkedMessages";

	public static String DEFAULT_WORLD = "world";

	int checkTimeInterval = 5;// sec

	private List<String> worlds;

	private Set<String> playersExemptFromLightning = new HashSet<>();

	boolean holographicDisplaysEnabled = false;

	public static String RECIPE_KEY_THORDFISH = "thord-fish";
	public static String RECIPE_KEY_DIAMONDAY_SWORD = "diamonday-sword";
	public static String RECIPE_KEY_FISH_SWORD = "fish-sword";
	public static String RECIPE_KEY_FISH_ARROW = "fish-arrow";
	public static String RECIPE_KEY_EARTHDAY_POT_PIE = "earth-day-pot-pie";
	public static String RECIPE_KEY_LIGHTNING_INHIBITOR = "lightning-inhibitor";
	public static String RECIPE_KEY_DIAMONDAY_DIAMOND = "diamonday-diamond";
	public static String RECIPE_KEY_FISHFRYDAY_DIAMOND = "fishfryday-diamond";
	public static String RECIPE_KEY_FISHFRYDAY_EMERALD = "fishfryday-emerald";
	public static String RECIPE_KEY_EARTHDAY_WHEAT = "earthday-wheat";
	public static String RECIPE_KEY_EARTHDAY_CARROT = "earthday-carrot";
	public static String RECIPE_KEY_EARTHDAY_POTATO = "earthday-potato";
	public static String RECIPE_KEY_WORTAG_NETHERWORT = "wortag-netherwort";

	public void onEnable() {
		super.onEnable();

		worlds = getConfig().getStringList(ConfigKey.WORLDS.getKey());

		if (worlds == null || worlds.isEmpty()) {
			worlds = worlds != null ? worlds : new ArrayList<String>();
			info("adding default world: " + DEFAULT_WORLD);
			worlds.add(DEFAULT_WORLD);
		}

		TimeMonitor.startWorldMonitors(this, checkTimeInterval);
		YearmarkedListenerBase.registerListeners(this);
		registerCommands();
		EarthdayListener.registerEarthDayArrowRecipes(this);

		if (!getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
			info("HolographicDisplays is not installed or not enabled.");
		} else {
			holographicDisplaysEnabled = true;
			BukkitRunnable hologramDeleter = new HolographicDisplayMonitor(this);
			hologramDeleter.runTaskTimer(this, 30 * 20L, 30 * 20L);// twice-a-minute
		}
	}

	private void registerCommands() {
		YmCommand yearmarkedCommand = new YmCommand(this);
		this.getCommand("yearmarked").setExecutor(yearmarkedCommand);
		this.getCommand("ym").setExecutor(yearmarkedCommand);
		debugInfo("'/yearmarked' registered to YmCommand");
		debugInfo("'/ym' registered to YmCommand");
	}

	public Set<String> getPlayersExemptFromLightning() {
		return playersExemptFromLightning;
	}

	public void setPlayersExemptFromLightning(Set<String> playersExemptFromLightning) {
		this.playersExemptFromLightning = playersExemptFromLightning;
	}

	// public helpers

	public boolean worldEnabled(String world) {
		return worlds != null && worlds.contains(world);
	}

	public List<String> getWorlds() {
		return new ArrayList<String>(worlds);
	}

	@Override
	public ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(RESOURCE_NAME, locale);
	}

	@Override
	public String getRecipesNbtKey() {
		return NBT_RECIPE_KEY;
	}

}
