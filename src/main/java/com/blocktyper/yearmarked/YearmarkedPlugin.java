package com.blocktyper.yearmarked;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.blocktyper.v1_1_8.nbt.NBTItem;
import com.blocktyper.v1_1_8.plugin.BlockTyperPlugin;
import com.blocktyper.yearmarked.commands.YmCommand;
import com.blocktyper.yearmarked.listeners.DiamondayListener;
import com.blocktyper.yearmarked.listeners.EarthdayListener;
import com.blocktyper.yearmarked.listeners.FeathersdayListener;
import com.blocktyper.yearmarked.listeners.FishfrydayListener;
import com.blocktyper.yearmarked.listeners.MonsoondayListener;
import com.blocktyper.yearmarked.listeners.NetherstalkListener;
import com.blocktyper.yearmarked.listeners.SuperCreeperDamageListener;
import com.blocktyper.yearmarked.listeners.ThordfishListener;
import com.blocktyper.yearmarked.listeners.WortagListener;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;

public class YearmarkedPlugin extends BlockTyperPlugin implements Listener {

	private Random random = new Random();
	public static String NBT_RECIPE_KEY = "YearmarkedPluginNBTRecipeKey";

	public static final String RESOURCE_NAME = "com.blocktyper.yearmarked.resources.YearmarkedMessages";

	public static String DEFAULT_WORLD = "world";

	int checkTimeInterval = 5;// sec

	private List<String> worlds;

	private Set<String> playersExemptFromLightning = null;

	boolean holographicDisplaysEnabled = false;
	boolean showHolographicDisplaysOnDayChange = false;

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
		createConfig();

		getServer().getPluginManager().registerEvents(this, this);

		info(getLocalizedMessage(LocalizedMessageEnum.WORLDS.getKey()) + ": ");
		worlds = getConfig().getStringList(ConfigKeyEnum.WORLDS.getKey());
		if (worlds != null) {
			if (worlds.isEmpty()) {
				info("[empty]");
			} else {
				for (String world : worlds) {
					info("  - " + world);
				}
			}

		} else {
			info("[null]");
		}

		if (worlds == null || worlds.isEmpty()) {
			worlds = worlds != null ? worlds : new ArrayList<String>();
			info("adding default world: " + DEFAULT_WORLD);
			worlds.add("world");
		}

		info("starting world monitors");

		startWorldMonitors();
		registerListeners();
		registerCommands();
		registerEarthDayArrowRecipes();

		if (!getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
			getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
		} else {
			holographicDisplaysEnabled = true;
			showHolographicDisplaysOnDayChange = getConfig()
					.getBoolean(ConfigKeyEnum.HOLOGRAPHIC_DISPLAYS_SHOW_DAY_CHANGE_MESSAGE.getKey());
			BukkitRunnable hologramDeleter = new HolographicDisplayMonitor(this);
			hologramDeleter.runTaskTimer(this, 30 * 20L, 30 * 20L);// twice a
																	// minute
		}
	}

	private void startWorldMonitors() {

		for (String world : worlds) {
			try {
				startWorldMonitor(world);
			} catch (IllegalArgumentException e) {
				warning("IllegalArgumentException while starting world monitor[" + world + "]. Message: "
						+ e.getMessage());
				// e.printStackTrace();
				continue;
			} catch (IllegalStateException e) {
				warning("IllegalArgumentException while starting world monitor[" + world + "]. Message: "
						+ e.getMessage());
				// e.printStackTrace();
				continue;
			} catch (Exception e) {
				warning("General Exception while starting world monitor[" + world + "]. Message: " + e.getMessage());
				// e.printStackTrace();
				continue;
			}
		}
	}

	private void startWorldMonitor(String world) {
		info("LOADING... " + getLocalizedMessage(LocalizedMessageEnum.WORLD.getKey()) + "(" + world + ")");
		TimeMonitor timeMonitor = new TimeMonitor(this, world);

		if (timeMonitor.getWorld() == null) {
			warning("   -" + world + " was not recognized");
			return;
		} else {
			info("   -" + world + " was loaded");
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(timeMonitor.getWorld());

		try {
			sendDayInfo(cal, timeMonitor.getWorld().getPlayers());
		} catch (Exception e) {
			this.warning("Errors while sending day info. Message: " + e.getMessage());
			return;
			// e.printStackTrace();
		}

		timeMonitor.checkForDayChange(cal);

		this.info("Checking time every " + checkTimeInterval + " sec.");
		timeMonitor.runTaskTimer(this, checkTimeInterval, checkTimeInterval * 20L);
	}

	private void registerCommands() {
		YmCommand yearmarkedCommand = new YmCommand(this);
		this.getCommand("yearmarked").setExecutor(yearmarkedCommand);
		this.getCommand("ym").setExecutor(yearmarkedCommand);
		getLogger().info("'/yearmarked' registered to YmCommand");
		getLogger().info("'/ym' registered to YmCommand");
	}

	private void registerListeners() {
		new MonsoondayListener(this);
		new EarthdayListener(this);
		new WortagListener(this);
		new NetherstalkListener(this);
		new ThordfishListener(this);// Donnerstag is handled by logic in
									// TimeMonitor. ThorsdayListener is only for
									// paying attention to when a user hits a
									// tree with a Thordfish. It pertains to
									// both Moonsoonday and DOnnerstag
		new SuperCreeperDamageListener(this);// this only pertains to Donnerstag
		new FishfrydayListener(this);
		new DiamondayListener(this);
		new FeathersdayListener(this);
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
		if (getConfig().getBoolean(ConfigKeyEnum.SHOW_JOIN_MESSAGE.getKey(), true))
			sendPlayerDayInfo(player, cal);
	}

	private void sendPlayerDayInfo(Player player, YearmarkedCalendar cal) {
		if (worlds.contains(player.getWorld().getName())) {
			List<Player> playerInAList = new ArrayList<Player>();
			playerInAList.add(player);
			sendDayInfo(cal, playerInAList);
		}
	}

	// begin config file initialization
	private void createConfig() {
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}

	}
	// end config file initialization

	public Set<String> getPlayersExemptFromLightning() {
		return playersExemptFromLightning;
	}

	public void setPlayersExemptFromLightning(Set<String> playersExemptFromLightning) {
		this.playersExemptFromLightning = playersExemptFromLightning;
	}

	public String getNameOfLightningInhibitor(HumanEntity player) {
		ItemStack item = recipeRegistrar().getItemFromRecipe(RECIPE_KEY_LIGHTNING_INHIBITOR, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public String getNameOfEarthdayPotPie(HumanEntity player) {
		ItemStack item = recipeRegistrar().getItemFromRecipe(RECIPE_KEY_EARTHDAY_POT_PIE, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public String getNameOfThordfish(HumanEntity player) {
		ItemStack item = recipeRegistrar().getItemFromRecipe(RECIPE_KEY_THORDFISH, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public String getNameOfFishSword(HumanEntity player) {
		ItemStack item = recipeRegistrar().getItemFromRecipe(RECIPE_KEY_FISH_SWORD, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public String getNameOfFishArrow(HumanEntity player) {
		ItemStack item = recipeRegistrar().getItemFromRecipe(RECIPE_KEY_FISH_ARROW, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	public String getNameOfWortagNetherwort(HumanEntity player) {
		ItemStack item = recipeRegistrar().getItemFromRecipe(RECIPE_KEY_WORTAG_NETHERWORT, player, null, null);
		return item.getItemMeta().getDisplayName();
	}

	// public helpers
	public boolean rollIsLucky(double percentChanceOfTrue) {
		if (percentChanceOfTrue <= 0 || (percentChanceOfTrue < 100 && random.nextDouble() > percentChanceOfTrue)) {
			return false;
		} else if (percentChanceOfTrue >= 100) {
			return true;
		} else if (percentChanceOfTrue <= random.nextDouble()) {
			return true;
		}
		return false;
	}

	public boolean worldEnabled(String world) {
		return worlds != null && worlds.contains(world);
	}

	public void sendDayInfo(YearmarkedCalendar cal, List<Player> players) {

		debugInfo("sendDayInfo --> displayKey: " + cal.getDayOfWeekEnum().getDisplayKey());
		String dayName = getConfig().getString(cal.getDayOfWeekEnum().getDisplayKey(), "A DAY");
		debugInfo("sendDayInfo --> dayName: " + dayName);

		if (players != null && !players.isEmpty()) {
			for (Player player : players) {
				String todayIs = String.format(getLocalizedMessage(LocalizedMessageEnum.TODAY_IS.getKey(), player),
						dayName);
				String dayOfMonthMessage = new MessageFormat(
						getLocalizedMessage(LocalizedMessageEnum.IT_IS_DAY_NUMBER.getKey(), player))
								.format(new Object[] { cal.getDayOfMonth() + "", cal.getMonthOfYear() + "",
										cal.getYear() + "" });

				player.sendMessage(ChatColor.GREEN + "#----------------");
				player.sendMessage(ChatColor.GREEN + "#----------------");
				player.sendMessage(ChatColor.YELLOW + todayIs);
				player.sendMessage(dayOfMonthMessage);
				player.sendMessage(ChatColor.GREEN + "#----------------");
				player.sendMessage(ChatColor.GREEN + "#----------------");

				if (showHolographicDisplaysOnDayChange) {
					Location where = player.getLocation();
					where.setY(where.getY() + 2);
					Hologram hologram = HologramsAPI.createHologram(this, where);

					VisibilityManager visibilityManager = hologram.getVisibilityManager();
					visibilityManager.showTo(player);
					visibilityManager.setVisibleByDefault(false);
					hologram.appendTextLine(ChatColor.YELLOW + todayIs);

					List<String> descriptions = LocalizedMessageEnum.getDayDesciptions(cal.getDayOfWeekEnum(), this,
							player);

					if (descriptions != null && !descriptions.isEmpty()) {
						for (String description : descriptions) {
							hologram.appendTextLine(ChatColor.GREEN + description);
						}
					}
				}
			}
		}
	}

	@Override
	public ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle(RESOURCE_NAME, locale);
	}

	@Override
	public String getRecipesNbtKey() {
		return NBT_RECIPE_KEY;
	}

	public boolean itemHasExpectedNbtKey(ItemStack item, String expectedKey) {
		if (item != null && expectedKey != null) {
			NBTItem nbtItem = new NBTItem(item);
			if (nbtItem.hasKey(getRecipesNbtKey())) {
				String value = nbtItem.getString(getRecipesNbtKey());
				if (value != null && value.equals(expectedKey)) {
					return true;
				}
			}
		}
		return false;
	}

	private void registerEarthDayArrowRecipes() {
		for (EntityType entityType : EntityType.values()) {
			Map<String, String> nbtStringData = new HashMap<String, String>();
			nbtStringData.put(EarthdayListener.ENTITY_TYPE, entityType.name());
			EarthdayListener.registerEarthdayArrowRecipe(entityType, nbtStringData, this);

		}
	}
}
