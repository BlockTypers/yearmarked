package com.blocktyper.yearmarked.days.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.v1_2_0.nbt.NBTItem;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.listeners.earthday.EarthdayListener;

public class DataMigrationListener_2_0_0 extends YearmarkedListenerBase {

	private String earthday;
	private String wortag;
	private String fishfryday;
	private String diamonday;

	private String fishboneArrow;
	private String lightningInhibitor;
	private String earthdayPotPie;
	private String thordfish;

	private String needle1;
	private String needle2;

	private Material needle1Output;
	private Material needle2Output;

	public DataMigrationListener_2_0_0(YearmarkedPlugin plugin) {
		super(plugin);

		//days
		earthday = plugin.getConfig().getString("migration-yearmarked-earthday", null);
		earthday = earthday == null || earthday.trim().isEmpty() ? "Earthday" : earthday;

		wortag = plugin.getConfig().getString("migration-yearmarked-wortag", null);
		wortag = wortag == null || wortag.trim().isEmpty() ? "Wortag" : wortag;

		fishfryday = plugin.getConfig().getString("migration-yearmarked-fishfryday", null);
		fishfryday = fishfryday == null || fishfryday.trim().isEmpty() ? "Fishfryday" : fishfryday;
		
		diamonday = plugin.getConfig().getString("migration-yearmarked-diamonday", null);
		diamonday = diamonday == null || diamonday.trim().isEmpty() ? "Diamonday" : diamonday;
		
		//crafted items
		earthdayPotPie = plugin.getConfig().getString("migration-earthday-pot-pie", null);
		earthdayPotPie = earthdayPotPie == null || earthdayPotPie.trim().isEmpty() ? "Earthday Pot Pie"
				: earthdayPotPie;

		fishboneArrow = plugin.getConfig().getString("migration-fish-arrow", null);
		fishboneArrow = fishboneArrow == null || fishboneArrow.trim().isEmpty() ? "Fishbone Arrow" : fishboneArrow;
		
		lightningInhibitor = plugin.getConfig().getString("migration-lightning-inhibitor", null);
		lightningInhibitor = lightningInhibitor == null || lightningInhibitor.trim().isEmpty() ? "Lightning Inhibitor" : lightningInhibitor;
		
		thordfish = plugin.getConfig().getString("migration-thordfish", null);
		thordfish = thordfish == null || thordfish.trim().isEmpty() ? "Thordfish" : thordfish;

		needle1 = plugin.getConfig().getString("migration-fish-sword", null);
		needle1 = needle1 == null || needle1.trim().isEmpty() ? "Needle" : needle1;
		String needle1Output = plugin.getConfig().getString("migration-fish-sword-output", null);
		needle1Output = needle1Output == null || needle1Output.trim().isEmpty() ? "STONE_SWORD" : needle1Output;
		this.needle1Output = Material.matchMaterial(needle1Output);

		needle2 = plugin.getConfig().getString("migration-diamond-fish-sword", null);
		needle2 = needle2 == null || needle2.trim().isEmpty() ? "Needle" : needle2;
		String needle2Output = plugin.getConfig().getString("migration-diamond-fish-sword-output", null);
		needle2Output = needle2Output == null || needle2Output.trim().isEmpty() ? "DIAMOND_SWORD" : needle2Output;
		this.needle2Output = Material.matchMaterial(needle2Output);

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getCurrentItem() == null) {
			return;
		}

		if (event.getCurrentItem().getItemMeta() == null
				|| event.getCurrentItem().getItemMeta().getDisplayName() == null) {
			return;
		}

		NBTItem nbtItem = new NBTItem(event.getCurrentItem());

		if (nbtItem.hasKey(plugin.getRecipesNbtKey())) {
			return;
		}

		String displayName = event.getCurrentItem().getItemMeta().getDisplayName();

		ItemStack replacementItem = null;

		if (displayName.startsWith(earthday)) {
			replacementItem = getNonCraftedItem(earthday, displayName, event.getCurrentItem(), event.getWhoClicked());
		} else if (displayName.startsWith(wortag)) {
			replacementItem = getNonCraftedItem(wortag, displayName, event.getCurrentItem(), event.getWhoClicked());
		} else if (displayName.startsWith(fishfryday)) {
			replacementItem = getNonCraftedItem(fishfryday, displayName, event.getCurrentItem(), event.getWhoClicked());
		} else if (displayName.startsWith(diamonday)) {
			replacementItem = getNonCraftedItem(diamonday, displayName, event.getCurrentItem(), event.getWhoClicked());
		} else {
			replacementItem = getCraftedItem(displayName, event.getCurrentItem(), event.getWhoClicked());
		}

		if (replacementItem != null) {
			event.setCurrentItem(replacementItem);
			event.setCancelled(true);
			return;
		}
	}

	private ItemStack getCraftedItem(String displayName, ItemStack item, HumanEntity player) {
		if (displayName.equals(needle1) && item.getType() == needle1Output) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_FISH_SWORD, player, item,
					item.getAmount());
		} else if (displayName.equals(needle2) && item.getType() == needle2Output) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_DIAMONDAY_SWORD, player, item,
					item.getAmount());
		} else if (displayName.equals(fishboneArrow)) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_FISH_ARROW, player, item,
					item.getAmount());
		} else if (displayName.equals(earthdayPotPie)) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_EARTHDAY_POT_PIE, player,
					item, item.getAmount());
		} else if (displayName.equals(lightningInhibitor)) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_LIGHTNING_INHIBITOR, player,
					item, item.getAmount());
		} else if (displayName.equals(thordfish)) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_THORDFISH, player,
					item, item.getAmount());
		}
		return null;
	}

	private ItemStack getNonCraftedItem(String day, String displayName, ItemStack item, HumanEntity player) {

		if (item.getType() == Material.CROPS || item.getType() == Material.WHEAT) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_EARTHDAY_WHEAT, player, item,
					item.getAmount());
		} else if (item.getType() == Material.CARROT_ITEM) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_EARTHDAY_CARROT, player, item,
					item.getAmount());
		} else if (item.getType() == Material.POTATO_ITEM) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_EARTHDAY_POTATO, player, item,
					item.getAmount());
		} else if (item.getType() == Material.NETHER_STALK) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_WORTAG_NETHERWORT, player, item,
					item.getAmount());
		} else if (item.getType() == Material.DIAMOND) {
			if (day.equals(diamonday)) {
				return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_DIAMONDAY_DIAMOND, player,
						item, item.getAmount());
			} else if (day.equals(fishfryday)) {
				return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_FISHFRYDAY_DIAMOND,
						player, item, item.getAmount());
			}
		} else if (item.getType() == Material.GRASS) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_FISHFRYDAY_GRASS, player,
					item, item.getAmount());

		} else if (item.getType() == Material.EMERALD) {
			return plugin.recipeRegistrar().getItemFromRecipe(YearmarkedPlugin.RECIPE_KEY_FISHFRYDAY_EMERALD, player,
					item, item.getAmount());

		} else if (item.getType() == Material.ARROW) {
			String entityName = displayName.substring(displayName.indexOf(day) + day.length());
			entityName = entityName.trim();
			EntityType entityType = EntityType.valueOf(entityName);
			String arrowRecipeKey = EarthdayListener.getEarthdayEntityArrowRecipeKey(entityType);
			return plugin.recipeRegistrar().getItemFromRecipe(arrowRecipeKey, player, item, item.getAmount());
		}

		return null;
	}

}
