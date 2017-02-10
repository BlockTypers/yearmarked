package com.blocktyper.yearmarked.days.listeners.earthday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Crops;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.blocktyper.v1_2_0.nbt.NBTItem;
import com.blocktyper.v1_2_0.recipes.AbstractBlockTyperRecipe;
import com.blocktyper.v1_2_0.recipes.BlockTyperRecipe;
import com.blocktyper.yearmarked.ConfigKey;
import com.blocktyper.yearmarked.LocalizedMessage;
import com.blocktyper.yearmarked.YearmarkedListenerBase;
import com.blocktyper.yearmarked.YearmarkedPlugin;
import com.blocktyper.yearmarked.days.DayOfWeek;
import com.blocktyper.yearmarked.days.YearmarkedCalendar;
import com.blocktyper.yearmarked.items.YMRecipe;

public class EarthdayListener extends YearmarkedListenerBase {

	public static final String LAST_POT_PIE_TIME_KEY = "last-pot-pie-time";

	public static final String ENTITY_TYPE = "EARTHDAY_EntityType";
	public static final String TAMED = "♥";
	public static final String NAMED = "➢";
	public static final String CHESTED = "☐";

	public static final List<EntityType> ANIMAL_ARROW_TYPES = Arrays.asList(EntityType.COW, EntityType.HORSE,
			EntityType.DONKEY, EntityType.CHICKEN, EntityType.SHEEP, EntityType.PIG, EntityType.SHEEP, EntityType.LLAMA,
			EntityType.RABBIT, EntityType.WOLF);

	private Random random = new Random();

	private static ArrayList<Material> dropableEquipment;

	static {
		dropableEquipment = new ArrayList<>();
		dropableEquipment.add(Material.DIAMOND_BARDING);
		dropableEquipment.add(Material.GOLD_BARDING);
		dropableEquipment.add(Material.IRON_BARDING);
		dropableEquipment.add(Material.SADDLE);
		dropableEquipment.add(Material.CARPET);
	}

	public EarthdayListener(YearmarkedPlugin plugin) {
		super(plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCropsBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();

		if (block.getType() != Material.CROPS && block.getType() != Material.CARROT
				&& block.getType() != Material.POTATO) {
			return;
		}

		YearmarkedCalendar cal = new YearmarkedCalendar(block.getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeek.EARTHDAY)) {
			return;
		}

		if (!getConfig().getBoolean(ConfigKey.EARTHDAY_BONUS_CROPS.getKey(), true)) {
			debugInfo(ConfigKey.EARTHDAY_BONUS_CROPS.getKey() + ": false");
			return;
		}

		if (block.getType() != Material.CROPS && block.getType() != Material.CARROT
				&& block.getType() != Material.POTATO) {
			return;
		}

		if (((Crops) block.getState().getData()).getState() != CropState.RIPE) {
			debugInfo("Ripe " + block.getType() + " on earthday.");
			return;
		}

		if (!worldEnabled(block.getWorld().getName(),
				getConfig().getString(DayOfWeek.EARTHDAY.getDisplayKey()))) {
			return;
		}

		int high = getConfig().getInt(ConfigKey.EARTHDAY_BONUS_CROPS_RANGE_HIGH.getKey(), 3);
		int low = getConfig().getInt(ConfigKey.EARTHDAY_BONUS_CROPS_RANGE_LOW.getKey(), 1);

		int rewardCount = random.nextInt(high + 1);

		if (rewardCount < low) {
			rewardCount = low;
		}

		if (rewardCount > 0) {
			String bonus = getLocalizedMessage(LocalizedMessage.BONUS.getKey(), event.getPlayer());
			event.getPlayer()
					.sendMessage(ChatColor.DARK_GREEN + bonus + "[x" + rewardCount + "] " + block.getType().toString());
			reward(block, rewardCount, event.getPlayer());
		} else {
			debugInfo("No luck on Earthday");
			event.getPlayer().sendMessage(ChatColor.RED + ":(");
		}

	}

	/**
	 * Drop the Earthday reward
	 * 
	 * @param block
	 * @param rewardCount
	 */
	private void reward(Block block, int rewardCount, HumanEntity player) {

		ItemStack reward = null;
		if (block.getType() == Material.CROPS) {
			reward = getItemFromRecipe(YMRecipe.EARTHDAY_WHEAT, player, null, null);
		} else if (block.getType() == Material.CARROT) {
			reward = getItemFromRecipe(YMRecipe.EARTHDAY_CARROT, player, null, null);
		} else if (block.getType() == Material.POTATO) {
			reward = getItemFromRecipe(YMRecipe.EARTHDAY_POTATO, player, null, null);
		} else {
			reward = new ItemStack(Material.GRASS);
		}
		
		dropItemsInStacks(block.getLocation(), rewardCount, reward);
	}

	/**
	 * Eating an Earthday pot pie buffs the player with FAST_DIGGING,
	 * DAMAGE_RESISTANCE and SPEED for a time and with a magnitude specified in
	 * the config file with the keys
	 * 'yearmarked-earthday-pot-pie-buff-duration-sec' and
	 * 'yearmarked-earthday-pot-pie-buff-magnitude' respectively.
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPotPieEat(PlayerItemConsumeEvent event) {

		YearmarkedCalendar cal = new YearmarkedCalendar(event.getPlayer().getWorld());
		if (!cal.getDayOfWeekEnum().equals(DayOfWeek.EARTHDAY)) {
			return;
		}

		ItemMeta meta = event.getItem().getItemMeta();
		if (meta == null || meta.getDisplayName() == null)
			return;

		if (!itemHasExpectedNbtKey(event.getItem(), YMRecipe.EARTHDAY_POT_PIE)){
			return;
		}

		int buffDuration = getConfig().getInt(ConfigKey.EARTHDAY_POT_PIE_BUFF_DURATION_SEC.getKey(), 30);
		int buffMagnitude = getConfig().getInt(ConfigKey.EARTHDAY_POT_PIE_BUFF_MAGNITUDE.getKey(), 5);

		event.getPlayer()
				.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, buffDuration * 20, buffMagnitude));

		event.getPlayer().addPotionEffect(
				new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, buffDuration * 20, buffMagnitude));

		event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, buffDuration * 20, buffMagnitude));

		setMetadata(event.getPlayer(), LAST_POT_PIE_TIME_KEY, new Date().getTime());

	}

	public void setMetadata(Entity player, String key, Object value) {
		player.setMetadata(key, new FixedMetadataValue(plugin, value));
	}

	/**
	 * When a player hits an entity with an Earthday crop, that animal will be
	 * converted into an entity arrow and the player will be charged a number of
	 * the Earthday crops specified in the config using a string list of 'equals
	 * expressions' under the key 'yearmarked-earthday-entity-arrows-costs' The
	 * entity-arrow mechanic only works with specific combinations of crops and
	 * entities. If the player has recently eaten an Earthday pot pie, then the
	 * player can use any Earthday crop on any entity with a cost defined under
	 * 'yearmarked-earthday-entity-arrows-costs'
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void playerHitsAnimalWithEarthdayCrop(EntityDamageByEntityEvent event) {

		if (!worldEnabled(event.getDamager().getWorld().getName(), "earth-day-pot-pie")) {
			debugInfo("earth-day-pot-pie not enabled.");
			return;
		}

		if (event.getCause() != null && event.getCause().equals(DamageCause.PROJECTILE))
			return;

		Player player = null;

		if (!(event.getDamager() instanceof Player)) {
			debugInfo("[playerHitsAnimalWithEarthdayCrop] EntityDamageByEntityEvent - Not a player");
		} else {
			player = (Player) event.getDamager();
		}

		ItemStack itemInHand = getPlayerHelper().getItemInHand(player);

		if (itemInHand == null) {
			debugInfo("[playerHitsAnimalWithEarthdayCrop] EntityDamageByEntityEvent - No item in hand");
			return;
		}

		if (!itemHasExpectedNbtKey(itemInHand, YMRecipe.EARTHDAY_CARROT) 
				&& !itemHasExpectedNbtKey(itemInHand, YMRecipe.EARTHDAY_POTATO) 
				&& !itemHasExpectedNbtKey(itemInHand, YMRecipe.EARTHDAY_WHEAT)) {
			debugInfo("[playerHitsAnimalWithEarthdayCrop] EntityDamageByEntityEvent - No Earthday item in hand");
			return;
		}

		List<MetadataValue> lastPotPieTimeMetaList = player.getMetadata(LAST_POT_PIE_TIME_KEY);
		boolean userHasPotPieBuff = false;

		if (lastPotPieTimeMetaList != null) {
			Date lastPotPieTimeDate = null;

			try {

				long timePieWasLastEatenMS = lastPotPieTimeMetaList.get(0).asLong();

				lastPotPieTimeDate = new Date(timePieWasLastEatenMS);

				int lastPotPieTimeLimitSec = getConfig()
						.getInt(ConfigKey.EARTHDAY_POT_PIE_AFFECT_ARROWS_DURATION_SEC.getKey(), 15);
				if (lastPotPieTimeDate != null
						&& (new Date().getTime() - lastPotPieTimeDate.getTime()) / 1000 < lastPotPieTimeLimitSec) {
					userHasPotPieBuff = true;
					debugInfo("userHasPotPieBuff");
				} else {
					debugInfo("expired pot pie buff time");
				}
			} catch (Exception e) {
				debugWarning("Error determining if user has pot pie buff. Message: " + e.getMessage());
			}
		} else {
			debugInfo("playerHitsAnimalWithEarthdayCrop - no pot pie buff time");
		}

		if (!userHasPotPieBuff && !ANIMAL_ARROW_TYPES.contains(event.getEntity().getType())) {
			debugInfo("playerHitsAnimal - not an earthday arrow animal");
			return;
		}

		if (itemInHand.getType().equals(Material.POTATO_ITEM)) {
			if (userHasPotPieBuff || (event.getEntity().getType().equals(EntityType.RABBIT)
					|| event.getEntity().getType().equals(EntityType.CHICKEN)
					|| event.getEntity().getType().equals(EntityType.WOLF))) {
				replaceEntityWithEntityArrow(event.getEntity(), itemInHand, player);
			}
		} else if (itemInHand.getType().equals(Material.WHEAT)) {
			if (userHasPotPieBuff || (event.getEntity().getType().equals(EntityType.COW)
					|| event.getEntity().getType().equals(EntityType.SHEEP)
					|| event.getEntity().getType().equals(EntityType.LLAMA))) {
				replaceEntityWithEntityArrow(event.getEntity(), itemInHand, player);
			}
		} else if (itemInHand.getType().equals(Material.CARROT_ITEM)) {
			if (userHasPotPieBuff
					|| (event.getEntity().getType().equals(EntityType.PIG)
							|| event.getEntity().getType().equals(EntityType.HORSE))
					|| event.getEntity().getType().equals(EntityType.DONKEY)) {
				replaceEntityWithEntityArrow(event.getEntity(), itemInHand, player);
			}
		} else {
			debugInfo("playerHitsAnimalWithEarthdayCrop - not an earthday crop");
		}
	}

	/**
	 * Replaces entity with an arrow. Charges the player a number of of the
	 * itemInHand determined by costs specified in the config using a string
	 * list of 'equals expressions' under the key
	 * 'yearmarked-earthday-entity-arrows-costs'
	 * 
	 * @param entity
	 * @param itemInHand
	 * @param player
	 */
	private void replaceEntityWithEntityArrow(Entity entity, ItemStack itemInHand, Player player) {

		debugInfo("replaceEntityWithEntityArrow - Entity type: " + entity.getType());
		// equals expressions for each entity type and the number of the
		// itemInHand which will be
		// charged to convert the entity into an entity arrow
		List<String> costs = getConfig()
				.getStringList(ConfigKey.EARTHDAY_ALLOW_ENTITY_ARROWS_COSTS.getKey());

		Integer cost = null;

		if (costs != null) {
			for (String costEqualsExpression : costs) {
				String entityEqualsPrefix = entity.getType().toString() + "=";
				if (costEqualsExpression.startsWith(entityEqualsPrefix)
						&& costEqualsExpression.length() > entityEqualsPrefix.length()) {
					try {
						cost = Integer.parseInt(costEqualsExpression.substring(
								costEqualsExpression.indexOf(entityEqualsPrefix) + entityEqualsPrefix.length()));
						break;
					} catch (NumberFormatException e) {
						warning("Unable to parse cost of entity arrow: " + costEqualsExpression);
						return;
					}
				}
			}
		}

		// this only supports charging a player up to the max stack size for the
		// given material
		if (cost == null || cost < 0) {
			debugInfo("replaceEntityWithEntityArrow - missing or negative cost found for entity type: "
					+ entity.getType());
			return;
		} else if (cost == 0) {
			debugInfo("replaceEntityWithEntityArrow - no cost for entity arrow type: " + entity.getType());
		} else if (itemInHand.getAmount() == cost) {
			debugInfo("replaceEntityWithEntityArrow - exact payment [" + cost + "] for entity arrow type: "
					+ entity.getType());
			player.getInventory().remove(itemInHand);
		} else if (itemInHand.getAmount() < cost) {
			debugInfo(
					"replaceEntityWithEntityArrow - cant afford [" + cost + "] entity arrow type: " + entity.getType());
			String missingPayment = "";
			for (int i = 0; i < cost - itemInHand.getAmount(); i++) {
				missingPayment += "$";
			}
			player.sendMessage(ChatColor.RED + missingPayment);
			return;
		} else if (itemInHand.getAmount() > cost) {
			debugInfo("replaceEntityWithEntityArrow - more than enough [" + cost + "] for entity arrow type: "
					+ entity.getType());
			itemInHand.setAmount(itemInHand.getAmount() - cost);
		} else {
			warning("replaceEntityWithEntityArrow - Unexpected use case[ItemInHand: " + itemInHand.getAmount()
					+ " - cost: " + cost + "]");
			return;
		}
		
		ItemStack arrow = recipeRegistrar().getItemFromRecipe(getEarthdayEntityArrowRecipeKey(entity.getType()), player, null, null);

		// create arrow for entity
		ItemMeta meta = arrow.getItemMeta();

		String customName = entity.getCustomName();

		List<String> lore = new ArrayList<String>();
		if (customName != null) {
			lore.add(NAMED + customName);
		}

		String trueOrFalseMeta = "";
		if (entity instanceof Tameable) {
			if (((Tameable) entity).isTamed())
				trueOrFalseMeta = trueOrFalseMeta + TAMED;
		}

		boolean hasChest = entity instanceof ChestedHorse && ((ChestedHorse) entity).isCarryingChest();
		if (hasChest) {
			trueOrFalseMeta = trueOrFalseMeta + CHESTED;
		}
		if (!trueOrFalseMeta.isEmpty())
			lore.add(trueOrFalseMeta);

		meta.setLore(lore);
		arrow.setItemMeta(meta);
		
		NBTItem nbtArrow = new NBTItem(arrow);
		nbtArrow.setString(ENTITY_TYPE, entity.getType().toString());

		// drop the arrow
		entity.getWorld().dropItem(entity.getLocation(), nbtArrow.getItem());

		if (entity instanceof InventoryHolder) {
			hasChest = true;
			InventoryHolder inventoryHolder = (InventoryHolder) entity;
			if (inventoryHolder.getInventory() != null && inventoryHolder.getInventory().getContents() != null
					&& inventoryHolder.getInventory().getContents().length > 0) {
				final Location l = entity.getLocation();
				final World w = entity.getWorld();
				Arrays.asList(inventoryHolder.getInventory().getContents()).forEach(i -> drop(w, l, i));
			}
		}

		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity;
			if (livingEntity.getEquipment() != null && livingEntity.getEquipment().getArmorContents() != null
					&& livingEntity.getEquipment().getArmorContents().length > 0) {
				final Location l = entity.getLocation();
				final World w = entity.getWorld();
				Arrays.asList(livingEntity.getEquipment().getArmorContents())
						.forEach(i -> dropApprovedEquipment(w, l, i));
			}
		}

		// remove the entity
		entity.remove();

		debugInfo("replaceEntityWithEntityArrow - arrow dropped");
	}

	private void dropApprovedEquipment(World w, Location l, ItemStack i) {
		if (w != null && l != null && i != null && dropableEquipment.contains(i.getType())) {
			w.dropItemNaturally(l, i);
		}
	}

	private void drop(World w, Location l, ItemStack i) {
		if (w != null && l != null && i != null){
			w.dropItemNaturally(l, i);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onEntityArrowHit(ProjectileHitEvent event) {

		if (!(event.getEntity().getShooter() instanceof Player)){
			return;
		}
		
		List<MetadataValue> entityTypeMetaList = event.getEntity().getMetadata(ENTITY_TYPE);

		if (entityTypeMetaList == null || entityTypeMetaList.isEmpty() || entityTypeMetaList.get(0) == null) {
			debugInfo("onEntityArrowHit - entityTypeMetaList was null or empty");
			return;
		}

		String customName = event.getEntity().hasMetadata(NAMED)
				? event.getEntity().getMetadata(NAMED).get(0).asString() : null;

		EntityType entityType = EntityType.valueOf(entityTypeMetaList.get(0).asString());

		if (entityType == null) {
			debugInfo("onEntityArrowHit - not an entity arrow");
			return;
		}

		Entity spawnedEntity = event.getEntity().getWorld().spawnEntity(event.getEntity().getLocation(), entityType);
		if (customName != null)
			spawnedEntity.setCustomName(customName);

		if (spawnedEntity instanceof Tameable) {
			boolean isTamed = event.getEntity().hasMetadata(TAMED)
					? event.getEntity().getMetadata(TAMED).get(0).asBoolean() : false;
			if (isTamed) {
				if (spawnedEntity instanceof Ageable) {
					((Ageable) spawnedEntity).setAdult();
				}
				((Tameable) spawnedEntity).setTamed(true);
			}
		}

		if (spawnedEntity instanceof ChestedHorse) {
			boolean isChested = event.getEntity().hasMetadata(CHESTED)
					? event.getEntity().getMetadata(CHESTED).get(0).asBoolean() : false;

			if (isChested) {
				((ChestedHorse) spawnedEntity).setAdult();
				((ChestedHorse) spawnedEntity).setCarryingChest(true);
			}
		}

		debugInfo("onEntityArrowHit - animal spawned");

		event.getEntity().remove();
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerShootEntityArrow(EntityShootBowEvent event) {
		
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();

		ItemStack firstArrowStack = getPlayerHelper().getFirstArrowStack(player);

		if (firstArrowStack == null) {
			return;
		}
		
		NBTItem nbtArrowStack = new NBTItem(firstArrowStack);

		if (!nbtArrowStack.hasKey(ENTITY_TYPE)) {
			debugInfo("onPlayerShootEntityArrow - not an earthday arrow");
			return;
		}
		
		ItemStack bow = getPlayerHelper().getItemInHand(player);
		
		if (getPlayerHelper().itemHasEnchantment(bow, Enchantment.ARROW_INFINITE)) {
			debugInfo("Infinite enchantment not approved.");
		} else {
			MetadataValue entityTypeMetaDataValue = new FixedMetadataValue(plugin, nbtArrowStack.getString(ENTITY_TYPE));
			event.getProjectile().setMetadata(ENTITY_TYPE, entityTypeMetaDataValue);
			
			if (firstArrowStack.getItemMeta().getLore() != null) {
				Optional<String> nameOptional = firstArrowStack.getItemMeta().getLore().stream()
						.filter(l -> l.startsWith(NAMED)).findFirst();
				if (nameOptional != null && nameOptional.isPresent() && nameOptional.get() != null) {
					String name = nameOptional.get();
					debugInfo("Naming arrow: " + name);
					int startIndex = name.indexOf(NAMED) + NAMED.length();
					debugInfo("startIndex: " + startIndex);
					name = name.substring(startIndex);
					debugInfo("Named arrow: " + name);
					MetadataValue mdv = new FixedMetadataValue(plugin, name);
					event.getProjectile().setMetadata(NAMED, mdv);
				}

				if (firstArrowStack.getItemMeta().getLore().stream()
						.anyMatch(l -> !l.startsWith(NAMED) && l.contains(TAMED))) {
					MetadataValue mdv = new FixedMetadataValue(plugin, true);
					event.getProjectile().setMetadata(TAMED, mdv);
				}

				if (firstArrowStack.getItemMeta().getLore().stream()
						.anyMatch(l -> !l.startsWith(NAMED) && l.contains(CHESTED))) {
					MetadataValue mdv = new FixedMetadataValue(plugin, true);
					event.getProjectile().setMetadata(CHESTED, mdv);
				}
			}
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	public static String getEarthdayEntityArrowRecipeKey(EntityType entityType) {
		return "earthday-" + entityType.name();
	}
	
	public static void registerEarthDayArrowRecipes(YearmarkedPlugin plugin) {
		for (EntityType entityType : EntityType.values()) {
			Map<String, String> nbtStringData = new HashMap<String, String>();
			nbtStringData.put(EarthdayListener.ENTITY_TYPE, entityType.name());
			registerEarthdayArrowRecipe(entityType, nbtStringData, plugin);

		}
	}

	private static void registerEarthdayArrowRecipe(EntityType entityType, Map<String, String> nbtStringData,
			YearmarkedPlugin plugin) {

		List<Material> materialMatrix = new ArrayList<>();
		materialMatrix.add(0, Material.ARROW);
		materialMatrix.add(1, Material.ARROW);
		materialMatrix.add(2, Material.ARROW);
		materialMatrix.add(3, Material.ARROW);
		materialMatrix.add(4, Material.NAME_TAG);
		materialMatrix.add(5, Material.ARROW);
		materialMatrix.add(6, Material.ARROW);
		materialMatrix.add(7, Material.ARROW);
		materialMatrix.add(8, Material.ARROW);

		String recipeKey = getEarthdayEntityArrowRecipeKey(entityType);
		AbstractBlockTyperRecipe recipe = new BlockTyperRecipe(recipeKey, materialMatrix, null, Material.ARROW, plugin);

		recipe.setName("Earthday " + entityType.name());
		recipe.setNbtStringData(nbtStringData);

		List<String> entityLocalList = plugin.getConfig().getStringList("yearmarked.entity-locales");

		if (entityLocalList != null) {
			for (String locale : entityLocalList) {
				String entityNameKey = "yearmarked-entities." + entityType.name() + "." + locale;
				String entityName = plugin.getConfig().getString(entityNameKey, entityType.name());
				String earthDay = plugin.getLocalizedMessage(LocalizedMessage.EARTHDAY.getKey(), locale);

				String localeArrowName = earthDay + " " + entityName;
				recipe.getLocaleNameMap().put(locale, localeArrowName);
			}
		}

		Map<Integer, String> itemHasNametagKeyMatrix = new HashMap<>();
		itemHasNametagKeyMatrix.put(4, entityType.name());
		recipe.setItemHasNameTagKeyMatrix(itemHasNametagKeyMatrix);

		recipe.setNonStacking(false);

		plugin.recipeRegistrar().registerRecipe(recipe);

	}
}
