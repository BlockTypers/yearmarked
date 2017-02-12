package com.blocktyper.yearmarked.days.listeners.diamonday;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedListenerBase;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.items.YMRecipe;


public class DiamondayListener extends YearmarkedListenerBase {

	private Random random = new Random();

	public DiamondayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDiamondBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		if (!block.getType().equals(Material.DIAMOND_ORE)) {
			return;
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeek.DIAMONDAY)) {
			return;
		}
		
		if(!getConfig().getBoolean(ConfigKey.DIAMONDAY_BONUS_DIAMONDS.getKey(), true)){
			debugInfo(ConfigKey.DIAMONDAY_BONUS_DIAMONDS.getKey() + ": false");
			return;
		}
		
		if (!worldEnabled(block.getWorld().getName(), getConfig().getString(DayOfWeek.DIAMONDAY.getDisplayKey()))) {
			return;
		}

		ItemStack inhand = event.getPlayer().getEquipment().getItemInMainHand();
		final Enchantment SILK_TOUCH = new EnchantmentWrapper(33);
		if (inhand.containsEnchantment(SILK_TOUCH)) {
			return;
		}
		
		int high = getConfig().getInt(ConfigKey.DIAMONDAY_BONUS_DIAMONDS_RANGE_HIGH.getKey(), 3);
		int low = getConfig().getInt(ConfigKey.DIAMONDAY_BONUS_DIAMONDS_RANGE_LOW.getKey(), 1);

		int rewardCount = random.nextInt(high + 1);
		
		if(rewardCount < low){
			rewardCount = low;
		}
		
		String bonus = getLocalizedMessage(LocalizedMessage.BONUS.getKey(), event.getPlayer());
		
		if(rewardCount > 0){
			ItemStack diamond = getItemFromRecipe(YMRecipe.DIAMONDAY_DIAMOND, event.getPlayer(), null, null);
			String displayName = diamond.getItemMeta() != null && diamond.getItemMeta().getDisplayName() != null ? diamond.getItemMeta().getDisplayName() : "";
			event.getPlayer().sendMessage(ChatColor.BLUE + bonus + "[x" + rewardCount + "] " + displayName);
			dropItemsInStacks(block.getLocation(), rewardCount, diamond);
		}else{
			debugInfo("No luck on Diamonday");
			event.getPlayer().sendMessage(ChatColor.RED + ":(");
		}
		
	}
}
