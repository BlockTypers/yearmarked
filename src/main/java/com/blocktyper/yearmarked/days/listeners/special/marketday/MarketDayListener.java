package com.blocktyper.yearmarked.days.listeners.special.marketday;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedListenerBase;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.days.listeners.DayChangeEvent;
import com.blocktyper.yearmarked.days.listeners.earthday.EarthdayListener;
import com.blocktyper.yearmarked.items.YMRecipe;

public class MarketDayListener extends YearmarkedListenerBase {

	public static final String MARKET_DAY = "market-day";
	public static final String GOODS = "goods";
	public static final String VANILLA = "vanilla";
	public static final String CUSTOM = "custom";

	public static final String ENABLED = "enabled";
	public static final String PROFESSION_RESTRICTED = "profession-restricted";
	public static final String PROFESSIONS = "professions";

	public static final String PERCENT_CHANCE = "percent-chance";

	public static final String VANILLA_PAYMENT = "vanilla-payment";
	public static final String CUSTOM_PAYMENT = "custom-payment";
	public static final String COST_AMOUNT = "cost-amount";
	public static final String SALE_AMOUNT = "amount";
	public static final String STOCK_LIMIT = "stock-limit";

	private static final int MERCHANT_RECIPE_VERSION_LIMIT = 20;
	private static final String YEARMARKED_RANDOM = "YEARMARKED_RANDOM";
	private static final String RANGE_INT_DELIMITER = "~";

	private static final String RESTRICTED_RANDOM_MATERIALS = "restricted-random-materials";
	public static Set<Material> restrictedRandomMaterials; 

	private static final String RESTRICTED_RANDOM_RECIPES = "restricted-random-recipes";
	public static Set<String> restrictedRandomRecipes = new HashSet<>();

	private Map<String, Merchant> villagerGoods = new HashMap<>();

	public MarketDayListener(YearmarkedPlugin plugin) {
		super(plugin);
		initializeRestrictedMaterials();
	}

	@EventHandler
	public void onDayChange(DayChangeEvent event) {
		villagerGoods.clear();
	}

	@EventHandler
	public void onVillagerInteractWithFishdayEmerald(PlayerInteractEntityEvent event) {

		Player player = event.getPlayer();

		if (event.getRightClicked() == null || event.getRightClicked().getType() != EntityType.VILLAGER) {
			return;
		}

		if (!itemHasExpectedNbtKey(getPlayerHelper().getItemInHand(player), YMRecipe.FISHFRYDAY_EMERALD)) {
			return;
		}

		event.setCancelled(true);

		Villager villager = (Villager) event.getRightClicked();
		int villagerCareerId = getVillagerHelper().getVillagerCareer(villager);

		YearmarkedCalendar cal = new YearmarkedCalendar(player.getWorld());
		if (!isMarketDay(cal)) {
			player.sendMessage(getLocalizedMessage(LocalizedMessage.NOT_MARKET_DAY.getKey(), player));
			return;
		}
		
		if (!worldEnabled(player.getWorld().getName(), getConfig().getString(DayOfWeek.EARTHDAY.getDisplayKey()))) {
			return;
		}

		String villagerId = villager.getUniqueId().toString();

		if (!villagerGoods.containsKey(villagerId)) {
			List<MarketGood> potentialGoods = new ArrayList<>();

			for (int version = 1; version <= MERCHANT_RECIPE_VERSION_LIMIT; version++) {
				for (Material material : Material.values()) {
					MarketGood marketGood = getMarketGoodFromMaterial(villager, player, villagerCareerId,
							material.name(), version);
					if (marketGood != null) {
						potentialGoods.add(marketGood);
					}
				}

				MarketGood randomMarketVanillaGood = getMarketGoodFromMaterial(villager, player, villagerCareerId,
						YEARMARKED_RANDOM, version);
				if (randomMarketVanillaGood != null) {
					potentialGoods.add(randomMarketVanillaGood);
				}

				for (String recipeKey : getConfig().getStringList("recipes")) {
					MarketGood marketGood = getMarketGoodFromRecipe(villager, player, villagerCareerId, recipeKey,
							version);
					if (marketGood != null) {
						potentialGoods.add(marketGood);
					}
				}

				for (EntityType entityType : EntityType.values()) {
					String recipeKey = EarthdayListener.getEarthdayEntityArrowRecipeKey(entityType);
					MarketGood marketGood = getMarketGoodFromRecipe(villager, player, villagerCareerId, recipeKey,
							version);
					if (marketGood != null) {
						potentialGoods.add(marketGood);
					}
				}

				MarketGood randomMarketCustomGood = getMarketGoodFromRecipe(villager, player, villagerCareerId,
						YEARMARKED_RANDOM, version);
				if (randomMarketCustomGood != null) {
					potentialGoods.add(randomMarketCustomGood);
				}
			}

			if (potentialGoods.isEmpty()) {
				return;
			}

			Merchant merchant = getMerchant(sort(potentialGoods), player);

			villagerGoods.put(villagerId, merchant);
		}

		Merchant merchant = villagerGoods.get(villagerId);

		if (merchant != null) {
			player.openMerchant(merchant, false);
		} else {
			player.sendMessage(getLocalizedMessage(LocalizedMessage.SOLD_OUT.getKey(), player));
		}

	}
	
	private void initializeRestrictedMaterials(){
		restrictedRandomMaterials = new HashSet<>();
		restrictedRandomMaterials.add(Material.AIR);
		restrictedRandomMaterials.add(Material.WATER);
		restrictedRandomMaterials.add(Material.LAVA);
		restrictedRandomMaterials.add(Material.STATIONARY_WATER);
		restrictedRandomMaterials.add(Material.STATIONARY_LAVA);
		restrictedRandomMaterials.add(Material.MOB_SPAWNER);
		restrictedRandomMaterials.add(Material.DRAGON_EGG);
		restrictedRandomMaterials.add(Material.MONSTER_EGG);
		restrictedRandomMaterials.add(Material.BEDROCK);
		restrictedRandomMaterials.add(Material.BED_BLOCK);
		restrictedRandomMaterials.add(Material.ACACIA_DOOR);
		restrictedRandomMaterials.add(Material.BIRCH_DOOR);
		restrictedRandomMaterials.add(Material.DARK_OAK_DOOR);
		restrictedRandomMaterials.add(Material.IRON_DOOR);
		restrictedRandomMaterials.add(Material.JUNGLE_DOOR);
		restrictedRandomMaterials.add(Material.SPRUCE_DOOR);
		restrictedRandomMaterials.add(Material.DARK_OAK_DOOR);
		restrictedRandomMaterials.add(Material.NETHER_BRICK);
		restrictedRandomMaterials.add(Material.CARROT);
		restrictedRandomMaterials.add(Material.POTATO);
		restrictedRandomMaterials.add(Material.NETHER_WARTS);
		restrictedRandomMaterials.add(Material.CROPS);

		List<String> configRestrictedRandomMaterials = plugin.getConfig()
				.getStringList(key(MARKET_DAY).end(RESTRICTED_RANDOM_MATERIALS));
		if (configRestrictedRandomMaterials != null) {
			for (String configRestrictedRandomMaterial : configRestrictedRandomMaterials) {
				restrictedRandomMaterials.add(Material.matchMaterial(configRestrictedRandomMaterial));
			}
		}

		List<String> configRestrictedRandomRecipes = plugin.getConfig()
				.getStringList(key(MARKET_DAY).end(RESTRICTED_RANDOM_RECIPES));
		if (configRestrictedRandomRecipes != null) {
			for (String configRestrictedRandomRecipe : configRestrictedRandomRecipes) {
				restrictedRandomRecipes.add(configRestrictedRandomRecipe);
			}
		}
	}

	/**
	 * 
	 * @param materialName
	 * @return
	 */
	private Material getMaterialCheckRandom(String materialName) {
		Material material = null;
		if (materialName.equals(YEARMARKED_RANDOM)) {
			material = getRandomMaterial();
		} else {
			material = Material.matchMaterial(materialName);
		}
		return material;
	}

	/**
	 * 
	 * @return
	 */
	private Material getRandomMaterial() {
		int randomIndex = random.nextInt(Material.values().length - 1);

		Material randomMaterial = Material.values()[randomIndex];

		if (restrictedRandomMaterials.contains(randomMaterial)) {
			return getRandomMaterial();
		}

		return randomMaterial;
	}

	/**
	 * 
	 * @param villager
	 * @param player
	 * @param villagerCareerId
	 * @param materialName
	 * @param version
	 * @return
	 */
	private MarketGood getMarketGoodFromMaterial(Villager villager, Player player, int villagerCareerId,
			String materialName, int version) {
		Key rootKey = key(MARKET_DAY).__(GOODS).__(VANILLA).__(materialName).__("v" + version);

		Material material = getMaterialCheckRandom(materialName);

		ItemStack saleItem = new ItemStack(material);
		MarketGood marketGood = getMarketGood(villager.getProfession(), villagerCareerId, rootKey, player, saleItem);

		if (marketGood == null) {
			return null;
		}

		return marketGood;
	}

	/**
	 * 
	 * @param villager
	 * @param player
	 * @param villagerCareerId
	 * @param recipeKey
	 * @param version
	 * @return
	 */
	private MarketGood getMarketGoodFromRecipe(Villager villager, Player player, int villagerCareerId, String recipeKey,
			int version) {
		Key rootKey = key(MARKET_DAY).__(GOODS).__(CUSTOM).__(recipeKey).__("v" + version);

		recipeKey = getRecipeKeyCheckRandom(recipeKey);

		ItemStack saleItem = recipeRegistrar().getItemFromRecipe(recipeKey, player, null, null);
		MarketGood marketGood = getMarketGood(villager.getProfession(), villagerCareerId, rootKey, player, saleItem);

		if (marketGood == null) {
			return null;
		}

		marketGood.isCustom = true;
		return marketGood;
	}

	/**
	 * 
	 * @param recipeKey
	 * @return
	 */
	private String getRecipeKeyCheckRandom(String recipeKey) {
		if (YEARMARKED_RANDOM.equals(recipeKey)) {
			recipeKey = getRandomRecipe();
		}
		return recipeKey;
	}

	/**
	 * 
	 * @return
	 */
	private String getRandomRecipe() {
		int randomIndex = random.nextInt(getConfig().getStringList("recipes").size() - 1);
		String randomRecipe = getConfig().getStringList("recipes").get(randomIndex);
		if (restrictedRandomRecipes.contains(randomRecipe)) {
			return getRandomRecipe();
		}
		return getConfig().getStringList("recipes").get(randomIndex);
	}

	/**
	 * 
	 * @param goods
	 * @return
	 */
	private List<MarketGood> sort(List<MarketGood> goods) {
		if (goods == null || goods.isEmpty()) {
			return null;
		}
		goods.sort(MarketGood.byCustomAndPercentChance);
		return goods;
	}

	/**
	 * 
	 * @param goods
	 * @param player
	 * @return
	 */
	private Merchant getMerchant(List<MarketGood> goods, Player player) {
		if (goods == null || goods.isEmpty()) {
			return null;
		}

		Merchant merchant = Bukkit.createMerchant(getLocalizedMessage(LocalizedMessage.MARKET_DAY.getKey(), player));
		List<MerchantRecipe> recipes = new ArrayList<>();
		for (MarketGood good : goods) {
			MerchantRecipe recipe = new MerchantRecipe(good.saleItem, good.stockLimit < 0 ? 1000000 : good.stockLimit);

			List<ItemStack> costItems = new ArrayList<>();
			costItems.add(good.costItem);
			if (good.costItem2 != null) {
				costItems.add(good.costItem2);
			}
			recipe.setIngredients(costItems);
			recipes.add(recipe);
		}
		merchant.setRecipes(recipes);

		return merchant;
	}

	/**
	 * 
	 * @param villagerProfession
	 * @param villagerCareerId
	 * @param rootKey
	 * @param player
	 * @param saleItem
	 * @return
	 */
	private MarketGood getMarketGood(Profession villagerProfession, int villagerCareerId, Key rootKey, Player player,
			ItemStack saleItem) {

		boolean enabled = getConfig().getBoolean(rootKey.end(ENABLED), false);
		if (!enabled) {
			return null;
		}

		boolean villiageTypeRestricted = getConfig().getBoolean(rootKey.end(PROFESSION_RESTRICTED), false);

		if (villiageTypeRestricted) {
			List<String> villiagerProfessions = getConfig().getStringList(rootKey.end(PROFESSIONS));
			if (villiagerProfessions == null || villiagerProfessions.isEmpty()) {
				return null;
			} else if (!villiagerProfessions.contains(villagerProfession.name())
					&& !villiagerProfessions.contains(villagerProfession.name() + "-" + villagerCareerId)) {
				return null;
			}
		}

		double percentChance = getConfig().getDouble(rootKey.end(PERCENT_CHANCE), 100.0);

		if (percentChance <= 0) {
			return null;
		}

		double randomDouble = random.nextDouble() * 100;

		if (randomDouble > percentChance) {
			return null;
		}

		String saleAmountString = getConfig().getString(rootKey.end(SALE_AMOUNT), null);
		String itemDisplayForLogging = "[" + saleItem.getType() + "] "
				+ (saleItem.getItemMeta() != null && saleItem.getItemMeta().getDisplayName() != null
						? saleItem.getItemMeta().getDisplayName() : "");

		if (saleAmountString == null) {
			warning(SALE_AMOUNT + " not set for sale item: " + itemDisplayForLogging);
			return null;
		}

		Integer saleAmount = getRangeInt(saleAmountString);

		if (saleAmount < 0) {
			warning(SALE_AMOUNT + " not valid [" + saleAmount + "] for sale item: " + itemDisplayForLogging);
			return null;
		}

		int maxStackSize = saleItem.getType().getMaxStackSize();
		if (maxStackSize < saleAmount) {
			saleAmount = maxStackSize;
		}

		saleItem.setAmount(saleAmount);

		ItemStack costItem = getCostItem(rootKey, player, false);

		if (costItem == null) {
			return null;
		}

		MarketGood marketGood = new MarketGood();
		marketGood.saleItem = saleItem;

		marketGood.costItem = costItem;
		marketGood.costItem2 = getCostItem(rootKey, player, true);

		marketGood.stockLimit = getRangeInt(getConfig().getString(rootKey.end(STOCK_LIMIT), "-1"));
		marketGood.percentChance = percentChance;

		return marketGood;
	}

	/**
	 * 
	 * @param stringVal
	 * @return
	 */
	private int getRangeInt(String stringVal) {
		Integer val = null;

		try {
			if (stringVal.contains(RANGE_INT_DELIMITER)) {
				String lowString = stringVal.substring(0, stringVal.indexOf(RANGE_INT_DELIMITER));
				String highString = stringVal.substring(stringVal.indexOf(RANGE_INT_DELIMITER) + 1);

				int low = Integer.parseInt(lowString);
				int high = Integer.parseInt(highString);

				val = random.nextInt(high - low) + low;
			} else {
				val = Integer.parseInt(stringVal);
			}
		} catch (NumberFormatException e) {
			warning(e.getMessage());
			return -1;
		}

		return val;
	}

	/**
	 * 
	 * @param rootKey
	 * @param player
	 * @param isSecondary
	 * @return
	 */
	private ItemStack getCostItem(Key rootKey, Player player, boolean isSecondary) {

		String vannillaConfigKey = rootKey.end(VANILLA_PAYMENT + (isSecondary ? "-2" : ""));
		String customConfigKey = rootKey.end(CUSTOM_PAYMENT + (isSecondary ? "-2" : ""));

		String vanillaPayment = getConfig().getString(vannillaConfigKey, null);
		String customPayment = null;

		if (vanillaPayment == null) {
			customPayment = getConfig().getString(customConfigKey, null);
		}

		String costNameForLogging = null;

		ItemStack paymentItem = null;

		if (vanillaPayment != null) {
			costNameForLogging = vanillaPayment;
			Material costMaterial = getMaterialCheckRandom(vanillaPayment);
			if (costMaterial == null) {
				warning(VANILLA_PAYMENT + " material type not recognized: " + vanillaPayment + ". " + customConfigKey);
				return null;
			}
			paymentItem = new ItemStack(costMaterial);
		} else if (customPayment != null) {
			costNameForLogging = customPayment;
			customPayment = getRecipeKeyCheckRandom(customPayment);
			paymentItem = recipeRegistrar().getItemFromRecipe(customPayment, player, null, null);

			if (paymentItem == null) {
				warning(CUSTOM_PAYMENT + " recipe not recognized: " + customPayment + ". " + customConfigKey);
				return null;
			}
		} else if (!isSecondary) {
			warning(VANILLA_PAYMENT + " vanilla material not recognized: " + vannillaConfigKey);
			warning(CUSTOM_PAYMENT + " recipe not recognized: " + customConfigKey);
			return null;
		}

		if (paymentItem != null) {

			String costAmountString = getConfig().getString(rootKey.end(COST_AMOUNT), null);

			if (costAmountString == null) {
				warning(COST_AMOUNT + " not set for cost item: " + costNameForLogging);
			}

			Integer costAmount = getRangeInt(costAmountString);

			if (costAmount < 0) {
				warning(COST_AMOUNT + " not valid [" + costAmountString + "] for cost item: " + costNameForLogging);
				return null;
			}
			int maxStackSize = paymentItem.getType().getMaxStackSize();
			if (maxStackSize < costAmount) {
				costAmount = maxStackSize;
			}

			paymentItem.setAmount(costAmount);
		}

		return paymentItem;
	}
	
	
	
	public static boolean isMarketDay(YearmarkedCalendar cal){
		if (!cal.getDayOfWeekEnum().equals(DayOfWeek.EARTHDAY)) {
			return false;
		}

		if (cal.getDayOfMonth() != cal.getDayOfWeek()) {
			return false;
		}
		
		return true;
	}
	

	/**
	 * 
	 * @param val
	 * @return
	 */
	private Key key(String val) {
		Key key = new Key();
		key.val = val;
		return key;
	}

	// region PRIVATE CLASSES
	private static class MarketGood {
		ItemStack saleItem;
		ItemStack costItem;
		ItemStack costItem2;

		Integer stockLimit;
		boolean isCustom = false;
		double percentChance;

		static Comparator<MarketGood> byCustomAndPercentChance = new Comparator<MarketGood>() {
			public int compare(MarketGood left, MarketGood right) {
				if (left.isCustom && !right.isCustom) {
					return 1;
				} else if (!left.isCustom && right.isCustom) {
					return -1;
				} else if (left.percentChance < right.percentChance) {
					return 1;
				} else {
					return -1;
				}
			}
		};
	}

	private static class Key {
		String val;

		Key __(String subKey) {
			val = val + "." + subKey;
			return this;
		}

		String end(String subKey) {
			return val + "." + subKey;
		}
	}
	// endregion

}
