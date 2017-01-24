package com.blocktyper.yearmarked.days.listeners.donnerstag;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.YearmarkedListenerBase;

public class DonnerstagUtils {

	private YearmarkedPlugin plugin = null;
	private World world;

	private Random random = new Random();

	public DonnerstagUtils(YearmarkedPlugin plugin, World world) {
		this.plugin = plugin;
		this.world = world;
	}

	public void checkForConstantLightning(YearmarkedCalendar cal) {
		if (!cal.getDayOfWeekEnum().equals(DayOfWeek.DONNERSTAG)) {
			return;
		}
		if (world.getPlayers() == null) {
			return;
		}
		boolean strikeForOddPlayers = random.nextBoolean();
		int i = 0;
		for (Player player : world.getPlayers()) {
			i++;
			if (plugin.getPlayersExemptFromLightning() == null
					|| !plugin.getPlayersExemptFromLightning().contains(player.getName())) {
				boolean doStrike = (strikeForOddPlayers && i % 2 > 0) || (!strikeForOddPlayers && i % 2 == 0);
				if (doStrike) {
					Location loc = player.getLocation();

					int xDelta = random.nextInt(15);
					int zDelta = random.nextInt(15);

					int lightningInhibitorPersonalRange = plugin.getConfig()
							.getInt(ConfigKey.DONNERSTAG_LIGHTNING_INHIBITOR_PERSONAL_RANGE.getKey(), 5);
					if (lightningInhibitorPersonalRange > 0) {
						if (xDelta < lightningInhibitorPersonalRange && zDelta < lightningInhibitorPersonalRange) {
							if (player.getInventory() != null && player.getInventory().getContents() != null)
								for (ItemStack item : player.getInventory().getContents()) {
									if (itemHasExpectedNbtKey(item,
											YearmarkedPlugin.RECIPE_KEY_LIGHTNING_INHIBITOR)) {
										plugin.debugInfo("Personal lightning inhibitor trigger.");
										if (random.nextBoolean()) {
											xDelta = lightningInhibitorPersonalRange;
										} else {
											zDelta = lightningInhibitorPersonalRange;
										}
									}
								}
						}
					}

					int x = loc.getBlockX() + (xDelta * (random.nextBoolean() ? -1 : 1));
					int z = loc.getBlockZ() + (zDelta * (random.nextBoolean() ? -1 : 1));

					if (isStrikeInSafeZone(world, player.getLocation().getBlockX(), player.getLocation().getBlockZ(), x,
							z)) {
						plugin.debugInfo("Lightning in safe zone.");
						continue;
					} else if (isInhibitorNear(world, x, z)) {
						plugin.debugInfo("Lightning inhibited.");
						continue;
					} else {
						plugin.debugInfo("Lightning NOT inhibited.");
					}

					double y = loc.getBlockY();

					if (plugin.getConfig().getBoolean(ConfigKey.DONNERSTAG_STRIKE_HIGHEST_BLOCKS.getKey(), false)) {
						Block highestBlock = loc.getWorld().getHighestBlockAt(loc);
						y = highestBlock.getY();
					}

					Location newLocation = new Location(world, x, y, z);

					if (plugin.getConfig().getBoolean(ConfigKey.DONNERSTAG_NO_FIRE_LIGHTNING.getKey(), false)) {
						strikeFakeLightning(newLocation);
					} else {
						world.strikeLightning(newLocation);
					}

					if (!plugin.getConfig().getBoolean(
							ConfigKey.DONNERSTAG_ALLOW_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD.getKey(), true)) {
						plugin.debugInfo(ConfigKey.DONNERSTAG_ALLOW_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD.getKey()
								+ ": false");
						return;
					}

					double creeperSpawnPercentChance = plugin.getConfig().getDouble(
							ConfigKey.DONNERSTAG_SUPER_CREEPER_SPAWN_WITH_FISH_SWORD_PERCENT_CHANCE.getKey(), 100);

					boolean spawnCreeper = YearmarkedListenerBase.rollIsLucky(creeperSpawnPercentChance, random);
					if (!spawnCreeper) {
						plugin.debugInfo("no super creeper spawns due to good luck");
						return;
					}

					ItemStack itemInHand = plugin.getPlayerHelper().getItemInHand(player);

					// spawn a creeper if they are holding the fish sword or a
					// bow with Fishbone arrows active
					if (itemInHand.getType().equals(Material.BOW)) {

						ItemStack firstArrowStack = plugin.getPlayerHelper().getFirstArrowStack(player);

						if (firstArrowStack != null) {
							plugin.debugInfo("arrow stack located. size: " + firstArrowStack.getAmount());

							if (!itemHasExpectedNbtKey(firstArrowStack,
									YearmarkedPlugin.RECIPE_KEY_FISH_ARROW)) {
								plugin.debugInfo(
										"Player does not have a special arrow' in firing position during lightning strike.");
								continue;
							}

						} else {
							plugin.debugInfo("no arrows found");
							continue;
						}

					} else {

						if (!itemHasExpectedNbtKey(itemInHand, YearmarkedPlugin.RECIPE_KEY_FISH_SWORD) && 
								!itemHasExpectedNbtKey(itemInHand, YearmarkedPlugin.RECIPE_KEY_DIAMONDAY_SWORD)) {
							plugin.debugInfo("Player does not have a special weapon' in hand during lightning strike.");
							continue;
						}
					}

					final Location spawnLocation = new Location(world, newLocation.getBlockX(),
							newLocation.getBlockY() + 2, newLocation.getBlockZ());
					player.sendMessage(ChatColor.RED + "Creeper!");

					new BukkitRunnable() {

						@Override
						public void run() {
							if (!plugin.worldEnabled(world.getName())) {
								plugin.debugInfo("no spawn. world not enabled.");
								return;
							}

							String message = new MessageFormat("Spawning zombie in world {0} a ({1},{2},{3}")
									.format(new Object[] { world.getName(), spawnLocation.getBlockX(),
											spawnLocation.getBlockY(), spawnLocation.getBlockZ() });
							plugin.debugInfo(message);
							Creeper creeper = (Creeper) world.spawnEntity(spawnLocation, EntityType.CREEPER);
							creeper.setPowered(true);

						}
					}.runTaskLater(plugin, 20L * 1);

				}
			}
		}
	}

	private void strikeFakeLightning(Location location) {
		plugin.debugInfo("No fire Lightning strike.");

		int damageHearts = plugin.getConfig().getInt(ConfigKey.DONNERSTAG_NO_FIRE_LIGHTNING_DAMAGE_HEARTS.getKey(),
				3);
		location.getWorld().strikeLightningEffect(location);
		for (LivingEntity entity : location.getWorld().getLivingEntities()) {
			if (entity.getLocation().distance(location) < 3D) {

				if (entity.getType().equals(EntityType.PIG)) {
					PigZombie pigZombie = (PigZombie) entity.getWorld().spawnEntity(entity.getLocation(),
							EntityType.PIG_ZOMBIE);
					entity.remove();
					pigZombie.damage(damageHearts * 2);
				} else if (entity.getType().equals(EntityType.CREEPER)) {
					Creeper creeper = (Creeper) entity;
					creeper.setPowered(true);
					creeper.damage(damageHearts * 2);
				} else {
					entity.damage(damageHearts * 2);
				}
			}
		}
	}

	private boolean isStrikeInSafeZone(World world, int playerX, int playerZ, int strikeX, int strikeZ) {
		List<String> safeZoneStrings = plugin.getConfig()
				.getStringList(ConfigKey.DONNERSTAG_NO_LIGHTNING_ZONES.getKey());

		if (safeZoneStrings == null || safeZoneStrings.isEmpty())
			return false;

		for (String safeZoneString : safeZoneStrings) {
			try {
				if (safeZoneString == null)
					continue;
				safeZoneString = safeZoneString.replace(" ", "");

				if (!safeZoneString.contains(")("))
					continue;

				String point1String = safeZoneString.substring(0, safeZoneString.indexOf(")("));
				String point2String = safeZoneString.substring(safeZoneString.indexOf(")(") + 2);

				point1String = point1String.replace("(", "");

				point2String = point2String.replace(")", "");

				int x1 = Integer.parseInt(point1String.substring(0, point1String.indexOf(",")));
				int z1 = Integer.parseInt(point1String.substring(point1String.indexOf(",") + 1));

				int x2 = Integer.parseInt(point2String.substring(0, point2String.indexOf(",")));
				int z2 = Integer.parseInt(point2String.substring(point2String.indexOf(",") + 1));

				if (x1 == x2 || z1 == z2)
					continue;

				int xLeft = x1 < x2 ? x1 : x2;
				int xRight = x2 > x1 ? x2 : x1;

				int zBottom = z1 < z2 ? z1 : z2;
				int zTop = z2 > z1 ? z2 : z1;

				if (playerX >= xLeft && playerX <= xRight && strikeZ >= zBottom && strikeZ <= zTop) {
					plugin.debugInfo("Player was in safe zone during lightning strike. " + safeZoneString);
					return true;
				} else if (strikeX >= xLeft && strikeX <= xRight && playerZ >= zBottom && playerZ <= zTop) {
					plugin.debugInfo("Lighting strike would have landed in safe zone. " + safeZoneString);
					return true;
				} else {
					plugin.debugInfo("Player and lighting strike not in safe zone. " + safeZoneString);
				}

			} catch (Exception e) {
				plugin.warning(
						"Error parsing lighting safe zone string [" + safeZoneString + "]. Message: " + e.getMessage());
			}
		}

		return false;
	}

	private boolean isInhibitorNear(World world, int xOfStrike, int zOfStrike) {

		int radius = plugin.getConfig().getInt(ConfigKey.DONNERSTAG_LIGHTNING_INHIBITOR_RANGE.getKey(), 25);

		if (radius <= 0)
			return false;

		for (int x = xOfStrike - radius; x < xOfStrike + radius; x++) {
			for (int z = zOfStrike - radius; z < zOfStrike + radius; z++) {
				Block block = world.getHighestBlockAt(x, z);

				if (block == null)
					continue;

				if (block.getType().equals(Material.CHEST)) {

					Chest chest = (Chest) block.getState();

					Inventory inventory = chest != null ? chest.getBlockInventory() : null;

					ItemStack[] items = inventory != null ? inventory.getContents() : null;

					if (items != null && items.length > 0) {
						for (ItemStack item : items) {
							if (itemHasExpectedNbtKey(item, YearmarkedPlugin.RECIPE_KEY_LIGHTNING_INHIBITOR)) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}
	
	private boolean itemHasExpectedNbtKey(ItemStack item, String expectedKey) {
		return YearmarkedListenerBase.itemHasExpectedNbtKey(plugin, item, expectedKey);
	}
}
