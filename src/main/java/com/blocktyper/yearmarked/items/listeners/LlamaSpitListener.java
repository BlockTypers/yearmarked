package com.blocktyper.yearmarked.items.listeners;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blocktyper.v1_2_4.helpers.InvisHelper;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.blocktyper.v1_2_4.IBlockTyperPlugin;
import com.blocktyper.v1_2_4.helpers.InvisHelper;
import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.YearmarkedListenerBase;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.items.YMRecipe;

public class LlamaSpitListener extends YearmarkedListenerBase {

	private Map<String, Date> spitWandCoolDownMap;
	private double spitWantCoolDown;

	public static String ROUNDS_INVIS_LORE_KEY = "YEARMARKED_LLAMA_SPIT_WAND_ROUNDS";

	public LlamaSpitListener(YearmarkedPlugin plugin) {
		super(plugin);
		spitWandCoolDownMap = new HashMap<>();
		spitWantCoolDown = getConfig().getDouble(ConfigKey.LLAMA_SPIT_WAND_COOL_DOWN.getKey(), .5);
	}

	/*
	 * @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	 * public void playerSwingsSpitWand(PlayerInteractEvent event) {
	 * spit(event.getPlayer(), event); }
	 */

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerDropsSpitWand(PlayerDropItemEvent event) {
		spit(event.getPlayer(), event);
	}

	private void spit(Player player, PlayerDropItemEvent event) {
		ItemStack wand = event.getItemDrop().getItemStack();
		ItemStack itemInHand = getPlayerHelper().getItemInHand(player);

		if (!itemHasExpectedNbtKey(wand, YMRecipe.LLAMA_SPIT_WAND)
				|| !itemHasExpectedNbtKey(itemInHand, YMRecipe.LLAMA_SPIT_WAND)) {
			return;
		}
		
		if(itemInHand.getAmount() > 0 || wand.getAmount() > 1){
			return;
		}

		event.setCancelled(true);

		if (!plugin.getPlayerHelper().updateCooldownIfPossible(spitWandCoolDownMap, player, spitWantCoolDown)) {
			return;
		}

		int rounds = 0;
		List<String> spitRoundLore = InvisHelper.getInvisibleLore(wand, ROUNDS_INVIS_LORE_KEY);
		if (spitRoundLore != null && !spitRoundLore.isEmpty()) {
			for (String loreLine : spitRoundLore) {
				loreLine = loreLine == null ? ""
						: InvisHelper.convertToVisibleString(loreLine).replace(ROUNDS_INVIS_LORE_KEY, "");
				if (loreLine.startsWith("[") && loreLine.endsWith("]")) {
					loreLine = loreLine.replace("[", "");
					loreLine = loreLine.replace("]", "");
					rounds = Integer.parseInt(loreLine);
					break;
				} else {
					player.sendMessage(loreLine);
				}
			}
		}

		World world = player.getWorld();

		if (rounds < 1) {
			world.playSound(player.getLocation(), Sound.ENTITY_LLAMA_DEATH, .5F, .5F);
			event.getItemDrop().setItemStack(new ItemStack(wand.getType()));
			return;
		}

		LlamaSpit spit = (LlamaSpit) world.spawnEntity(player.getLocation().add(0, .5, 0), EntityType.LLAMA_SPIT);
		spit.setShooter(player);

		Location playerLocation = player.getLocation().clone();

		spit.setVelocity(playerLocation.getDirection().multiply(12));

		world.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SPIT, .5F, .5F);

		rounds--;
		setSpitRounds(wand, rounds);
		event.getItemDrop().setItemStack(wand);
	}

	public static ItemStack initSpitRounds(ItemStack wand, IBlockTyperPlugin plugin) {
		return setSpitRounds(wand, plugin.getConfig().getInt(ConfigKey.LLAMA_SPIT_WAND_ROUNDS.getKey(), 200));
	}

	public static ItemStack setSpitRounds(ItemStack wand, int rounds) {
		ItemMeta itemMeta = wand.getItemMeta();
		List<String> lore = InvisHelper.removeLoreWithInvisibleKey(wand, ROUNDS_INVIS_LORE_KEY);
		lore.add(InvisHelper.convertToInvisibleString(ROUNDS_INVIS_LORE_KEY) + "[" + rounds + "]");
		itemMeta.setLore(lore);
		wand.setItemMeta(itemMeta);
		return wand;
	}

}
