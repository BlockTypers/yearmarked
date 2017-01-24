package com.blocktyper.yearmarked.days.listeners.donnerstag;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.listeners.YearmarkedListenerBase;

public class DonnerstagListener extends YearmarkedListenerBase {

	public static final String IS_FISH_ARROW = "IS_FISH_ARROW";

	public DonnerstagListener(YearmarkedPlugin plugin) {
		super(plugin);
		new ToggleLightningListener(plugin);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void entityShootBow(EntityShootBowEvent event) {

		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		if (!worldEnabled(player.getWorld().getName(), "Fish Arrow")) {
			return;
		}

		ItemStack firstArrowStack = plugin.getPlayerHelper().getFirstArrowStack(player);

		if (firstArrowStack != null) {
			plugin.debugInfo("arrow stack located. size: " + firstArrowStack.getAmount());

			if (firstArrowStack.getItemMeta() == null || firstArrowStack.getItemMeta().getDisplayName() == null) {
				plugin.debugInfo("arrows have no display name");
				return;
			}

			ItemStack bow = plugin.getPlayerHelper().getItemInHand(player);
			if (plugin.getPlayerHelper().itemHasEnchantment(bow, Enchantment.ARROW_INFINITE)) {
				plugin.debugInfo("Infinite enchantment not approved.");
			} else {
				// name it whatever the item stack is named
				// we will worry about if it is configured in the
				// EntityDamageByEntityEvent handler playerKillSuperCreeper
				event.getProjectile().setCustomName(firstArrowStack.getItemMeta().getDisplayName());

				MetadataValue isFishArrowMetaDataValue = new FixedMetadataValue(plugin, true);
				event.getProjectile().setMetadata(IS_FISH_ARROW, isFishArrowMetaDataValue);
			}

		} else {
			plugin.debugInfo("no arrows found");
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerKillSuperCreeper(EntityDamageByEntityEvent event) {

		if (!worldEnabled(event.getDamager().getWorld().getName(), "Super Creeper hit")) {
			return;
		}

		Player player = null;
		boolean fishArrowDamage = false;

		if (!(event.getDamager() instanceof Player)) {

			ItemStack fishArrow = plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_FISH_ARROW,
					player, null, null);

			if (fishArrow == null) {
				return;
			} else {
				if (event.getCause().equals(DamageCause.PROJECTILE)) {

					boolean isFishArrow = event.getEntity().hasMetadata(IS_FISH_ARROW)
							? event.getEntity().getMetadata(IS_FISH_ARROW).get(0).asBoolean() : false;

					if (event.getDamager() instanceof Projectile && isFishArrow) {
						fishArrowDamage = true;
						plugin.debugInfo("[playerKillSuperCreeper] damage from: Fish Arrow");

						if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
							player = (Player) ((Projectile) event.getDamager()).getShooter();
						}
					}
				}

				if (!fishArrowDamage) {
					return;
				}
			}

		} else {
			player = (Player) event.getDamager();
		}

		if (!(event.getEntity() instanceof Creeper)) {
			return;
		}

		Creeper creeper = (Creeper) event.getEntity();
		if (!creeper.isPowered()) {
			return;
		}

		if (!fishArrowDamage) {
			ItemStack itemInHand = plugin.getPlayerHelper().getItemInHand(player);

			if (itemInHand == null) {
				return;
			}

			if (itemInHand.getItemMeta() == null || itemInHand.getItemMeta().getDisplayName() == null) {
				return;
			}
		}

		if (event.getFinalDamage() < creeper.getHealth()) {
			return;
		}

		boolean isOpLucky = player.isOp()
				&& plugin.getConfig().getBoolean(ConfigKey.DONNERSTAG_SUPER_CREEPER_OP_LUCK.getKey(), true);

		if (isOpLucky)
			player.sendMessage(ChatColor.GOLD + "OP!");

		double dropDiamondPercent = plugin.getConfig()
				.getDouble(ConfigKey.DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_DIAMOND_PERCENT_CHANCE.getKey(), 5);
		double dropEmeraldPercent = plugin.getConfig()
				.getDouble(ConfigKey.DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_EMERALD_PERCENT_CHANCE.getKey(), 10);
		double dropThordfishPercent = plugin.getConfig()
				.getDouble(ConfigKey.DONNERSTAG_SUPER_CREEPER_SPAWN_DROPS_THORDFISH_PERCENT_CHANCE.getKey(), 15);

		// boolean spawnCreeper = plugin.rollTrueOrFalse(dropDiamondPercent);

		if (isOpLucky || rollIsLucky(dropDiamondPercent)) {
			Material reward = Material.DIAMOND;
			String message = plugin.getLocalizedMessage(LocalizedMessage.SUPER_CREEPER_HAD_DIAMOND.getKey(),
					player);
			ChatColor color = ChatColor.BLUE;
			doReward(creeper, player, new ItemStack(reward), message, color);
		}
		if (isOpLucky || rollIsLucky(dropEmeraldPercent)) {
			Material reward = Material.EMERALD;
			String message = plugin.getLocalizedMessage(LocalizedMessage.SUPER_CREEPER_HAD_EMERALD.getKey(),
					player);
			ChatColor color = ChatColor.GREEN;
			doReward(creeper, player, new ItemStack(reward), message, color);
		}
		if (isOpLucky || rollIsLucky(dropThordfishPercent)) {
			ItemStack reward = plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_THORDFISH, player,
					null, null);
			if (reward != null) {
				String message = String.format(
						plugin.getLocalizedMessage(LocalizedMessage.SUPER_CREEPER_HAD_THORDFISH.getKey(), player));
				doReward(creeper, player, reward, message, ChatColor.DARK_GREEN);
			} else {
				plugin.debugInfo("[playerKillSuperCreeper] no thordfish info for super creeper to drop one");
			}
		}
	}

	private void doReward(Creeper creeper, Player player, ItemStack reward, String message, ChatColor color) {
		if (reward != null) {
			player.getWorld().dropItem(creeper.getLocation(), reward);
			if (message != null) {
				player.sendMessage(color + message);
			}
		}
	}

}
