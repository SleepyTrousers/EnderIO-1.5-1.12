package crazypants.enderio.base.loot;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.material.alloy.Alloy;
import crazypants.enderio.base.material.material.Material;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static crazypants.enderio.base.init.ModObject.itemBasicCapacitor;
import static crazypants.enderio.base.init.ModObject.itemConduitProbe;
import static crazypants.enderio.base.init.ModObject.itemTravelStaff;

public class LootManager {

  // Note: Testing code is on the capacitor item. Right-click a chest in creative mode to fill it with loot. Edit the code to select which loot table to use.

  private static final @Nonnull LootCondition[] NO_CONDITIONS = new LootCondition[0];
  private static LootManager INSTANCE = new LootManager();

  public static void init(@Nonnull FMLPostInitializationEvent event) {
    MinecraftForge.EVENT_BUS.register(INSTANCE);
  }

  private LootManager() {
  }

  @SubscribeEvent
  public void onLootTableLoad(@Nonnull LootTableLoadEvent evt) {

    LootTable table = evt.getTable();

    LootPool lp = new LootPool(new LootEntry[0], NO_CONDITIONS, new RandomValueRange(1, 3), new RandomValueRange(0, 0), EnderIO.MOD_NAME);

    if (evt.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {

      lp.addEntry(createLootEntry(Alloy.DARK_STEEL.getStackIngot(), 1, 3, 0.25F));
      lp.addEntry(createLootEntry(itemConduitProbe.getItemNN(), 0.10F));
      lp.addEntry(createLootEntry(Items.QUARTZ, 3, 16, 0.25F));
      lp.addEntry(createLootEntry(Items.NETHER_WART, 1, 4, 0.20F));
      lp.addEntry(createLootEntry(Items.ENDER_PEARL, 1, 2, 0.30F));
      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelSword.getItemNN(), 0.1F));
      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelBoots.getItemNN(), 0.1F));
      lp.addEntry(createLootEntry(Material.GEAR_WOOD.getStack(), 1, 2, 0.5F));
      lp.addEntry(createLootCapacitor(0.15F));
      lp.addEntry(createLootCapacitor(0.15F));
      lp.addEntry(createLootCapacitor(0.15F));

    } else if (evt.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) {

      lp.addEntry(createLootEntry(Alloy.DARK_STEEL.getStackIngot(), 1, 3, 0.05F));
      lp.addEntry(createLootEntry(Items.ENDER_PEARL, 1, 2, 0.10F));
      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelSword.getItemNN(), 0.2F));
      lp.addEntry(createLootEntry(Material.GEAR_WOOD.getStack(), 1, 2, 0.5F));
      lp.addEntry(createLootCapacitor(0.15F));
      lp.addEntry(createLootCapacitor(0.05F));
      lp.addEntry(createLootEntry(ModObject.blockExitRail.getItemNN(), 1, 2, 0.15F));

    } else if (evt.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)) {

      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelBoots.getItemNN(), 0.1F));
      lp.addEntry(createLootEntry(Material.GEAR_IRON.getStack(), 1, 2, 0.5F));
      lp.addEntry(createLootCapacitor(0.15F));

    } else if (evt.getName().equals(LootTableList.CHESTS_IGLOO_CHEST)) {

      final CapturedMob polarBear = CapturedMob.create(new ResourceLocation("minecraft", "polar_bear"));
      if (polarBear != null) {
        lp.addEntry(
            new LootEntryItem(ModObject.itemSoulVial.getItemNN(), 1, 1, new LootFunction[] { setCount(1, 1), new SetNBT(NO_CONDITIONS, polarBear.toNbt(null)) },
                new LootCondition[] { new RandomChance(.2F) }, "PolarBearSoulVial"));
      }
      lp.addEntry(createLootEntry(ModObject.itemSoulVial.getItemNN(), 1, 3, 0.5F));
      lp.addEntry(createLootCapacitor(0.05F));

    } else if (evt.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER)) {

      ItemStack bucket = Fluids.FIRE_WATER.getBucket();
      lp.addEntry(new LootEntryItem(bucket.getItem(), 1, 1, new LootFunction[] { setCount(1, 1), setMetadata(bucket.getMetadata()), setNBT(bucket) },
          new LootCondition[] { new RandomChance(.05F) }, bucket.getItem().getUnlocalizedName() + ":" + bucket.getMetadata()));

    } else if (evt.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {

      lp.addEntry(createLootEntry(Alloy.ELECTRICAL_STEEL.getStackIngot(), 2, 6, 0.20F));
      lp.addEntry(createLootEntry(Alloy.REDSTONE_ALLOY.getStackIngot(), 3, 6, 0.35F));
      lp.addEntry(createLootEntry(Alloy.DARK_STEEL.getStackIngot(), 3, 6, 0.35F));
      lp.addEntry(createLootEntry(Alloy.PULSATING_IRON.getStackIngot(), 1, 2, 0.3F));
      lp.addEntry(createLootEntry(Alloy.VIBRANT_ALLOY.getStackIngot(), 1, 2, 0.2F));
      lp.addEntry(createLootEntry(Material.GEAR_WOOD.getStack(), 1, 2, 0.5F));
      lp.addEntry(createLootEntry(Material.GEAR_STONE.getStack(), 1, 2, 0.4F));
      lp.addEntry(createLootEntry(Material.GEAR_IRON.getStack(), 1, 2, 0.25F));
      lp.addEntry(createLootEntry(Material.GEAR_ENERGIZED.getStack(), 1, 2, 0.125F));
      lp.addEntry(createLootEntry(Material.GEAR_VIBRANT.getStack(), 1, 2, 0.0625F));
      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelSword.getItemNN(), 1, 1, 0.25F));
      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelBoots.getItemNN(), 1, 1, 0.25F));
      lp.addEntry(createLootCapacitor(0.1F));

    } else if (evt.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)) {

      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelSword.getItemNN(), 0.2F));
      lp.addEntry(createLootEntry(Material.GEAR_VIBRANT.getStack(), 1, 2, 0.0625F));
      lp.addEntry(createLootEntry(itemTravelStaff.getItemNN(), 0.1F));
      lp.addEntry(createLootCapacitor(.5f));

    } else if (evt.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {

      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelSword.getItemNN(), 1, 1, 0.25F));
      lp.addEntry(createLootEntry(itemTravelStaff.getItemNN(), 1, 1, 0.1F));
      lp.addEntry(createLootCapacitor(0.25F));
      lp.addEntry(createLootCapacitor(0.25F));

    } else if (evt.getName().equals(LootTableList.CHESTS_WOODLAND_MANSION)) {

      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelBow.getItemNN(), 1, 1, 0.25F));
      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelAxe.getItemNN(), 1, 1, 0.25F));
      lp.addEntry(createLootEntry(Material.GEAR_STONE.getStack(), 1, 2, 0.4F));
      lp.addEntry(createLootCapacitor(0.25F));
      lp.addEntry(createLootEntry(itemTravelStaff.getItemNN(), 1, 1, 0.1F));

    } else if (evt.getName().equals(LootTableList.CHESTS_END_CITY_TREASURE)) {

      final CapturedMob shulker = CapturedMob.create(new ResourceLocation("minecraft", "shulker"));
      if (shulker != null) {
        lp.addEntry(
            new LootEntryItem(ModObject.itemSoulVial.getItemNN(), 1, 1, new LootFunction[] { setCount(1, 1), new SetNBT(NO_CONDITIONS, shulker.toNbt(null)) },
                new LootCondition[] { new RandomChance(.2F) }, "ShulkerSoulVial"));
      }
      lp.addEntry(createLootEntry(ModObject.itemSoulVial.getItemNN(), 1, 3, 0.5F));
      lp.addEntry(createLootEntry(Material.GEAR_ENERGIZED.getStack(), 1, 2, 0.125F));
      lp.addEntry(createLootEntry(Material.GEAR_VIBRANT.getStack(), 1, 2, 0.125F));
      lp.addEntry(createLootCapacitor(0.05F));
      lp.addEntry(createDarkSteelLootEntry(ModObject.itemDarkSteelBow.getItemNN(), 1, 1, 0.25F));

    } else {
      return;
    }

    if (table.isFrozen()) {
      throw new RuntimeException("Some other mod (a list of suspects is printed in the log file) put a frozen loot table into the load event for loot table '"
          + evt.getName() + "'. This is a bug in that other mod. Ender IO is the victim here. Don't blame the victim!");
    }

    table.addPool(lp);
  }

  private @Nonnull LootEntry createLootEntry(@Nonnull Item item, float chance) {
    return createLootEntry(item, 1, 1, chance);
  }

  private @Nonnull LootEntry createLootEntry(@Nonnull Item item, int minSize, int maxSize, float chance) {
    return createLootEntry(item, 0, minSize, maxSize, chance);
  }

  /*
   * All loot entries are given the same weight, the generation probabilities depend on the RandomChance condition.
   */
  private @Nonnull LootEntry createLootEntry(@Nonnull Item item, int meta, int minStackSize, int maxStackSize, float chance) {
    LootCondition[] chanceCond = new LootCondition[] { new RandomChance(chance) };
    final ResourceLocation registryName = NullHelper.notnull(item.getRegistryName(), "found unregistered item");
    if (item.isDamageable()) {
      return new LootEntryItem(item, 1, 1, new LootFunction[] { setCount(minStackSize, maxStackSize), setDamage(item, meta), setEnergy() }, chanceCond,
          registryName.toString() + ":" + meta);
    } else {
      return new LootEntryItem(item, 1, 1, new LootFunction[] { setCount(minStackSize, maxStackSize), setMetadata(meta) }, chanceCond,
          registryName.toString() + ":" + meta);
    }
  }

  private @Nonnull LootEntry createLootEntry(@Nonnull ItemStack stack, int minStackSize, int maxStackSize, float chance) {
    LootCondition[] chanceCond = new LootCondition[] { new RandomChance(chance) };
    final ResourceLocation registryName = NullHelper.notnull(stack.getItem().getRegistryName(), "found unregistered item");
    return new LootEntryItem(stack.getItem(), 1, 1, new LootFunction[] { setCount(minStackSize, maxStackSize), setMetadata(stack.getMetadata()) }, chanceCond,
        registryName.toString() + ":" + stack.getMetadata());
  }

  private @Nonnull LootEntry createDarkSteelLootEntry(@Nonnull Item item, float chance) {
    return createDarkSteelLootEntry(item, 1, 1, chance);
  }

  private @Nonnull LootEntry createDarkSteelLootEntry(@Nonnull Item item, int minSize, int maxSize, float chance) {
    return createDarkSteelLootEntry(item, 0, minSize, maxSize, chance);
  }

  private @Nonnull LootEntry createDarkSteelLootEntry(@Nonnull Item item, int meta, int minStackSize, int maxStackSize, float chance) {
    LootCondition[] chanceCond = new LootCondition[] { new RandomChance(chance) };
    final ResourceLocation registryName = NullHelper.notnull(item.getRegistryName(), "found unregistered item");
    return new LootEntryItem(item, 1, 1, new LootFunction[] { setCount(minStackSize, maxStackSize), setDamage(item, meta), setUpgrades(), setEnergy() },
        chanceCond, registryName.toString() + ":" + meta);
  }

  int capCount = 0; // Each loot entry in a pool must have a unique name

  private @Nonnull LootEntry createLootCapacitor(float chance) {
    capCount++;
    return new LootEntryItem(itemBasicCapacitor.getItemNN(), 1, 1, new LootFunction[] { ls, setMetadata(3, 4) },
        new LootCondition[] { new RandomChance(chance) }, itemBasicCapacitor.getUnlocalisedName() + capCount);
  }

  private @Nonnull SetCount setCount(int min, int max) {
    return new SetCount(NO_CONDITIONS, new RandomValueRange(min, min));
  }

  private @Nonnull SetDamage setDamage(Item item, int damage) {
    return new SetDamage(NO_CONDITIONS, new RandomValueRange(damage > 0 ? damage : 1, damage > 0 ? damage : item.getMaxDamage()));
  }

  private @Nonnull SetMetadata setMetadata(int metaMin, int metaMax) {
    return new SetMetadata(NO_CONDITIONS, new RandomValueRange(metaMin, metaMax));
  }

  private @Nonnull SetMetadata setMetadata(int meta) {
    return new SetMetadata(NO_CONDITIONS, new RandomValueRange(meta));
  }

  private @Nonnull SetRandomEnergy setEnergy() {
    return new SetRandomEnergy(NO_CONDITIONS);
  }

  private @Nonnull SetRandomDarkUpgrade setUpgrades() {
    return new SetRandomDarkUpgrade(NO_CONDITIONS);
  }

  private @Nonnull SetNBT setNBT(ItemStack stack) {
    return new SetNBT(NO_CONDITIONS, NullHelper.first(stack.getTagCompound(), new NBTTagCompound()));
  }

  private static final @Nonnull LootSelector ls = new LootSelector(NO_CONDITIONS);
}
