package crazypants.enderio.integration.forestry;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.api.farm.IFertilizer;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.Log;
import crazypants.enderio.integration.forestry.farmers.ForestryFarmer;
import crazypants.enderio.integration.forestry.fertilizer.ForestryFertilizer;
import crazypants.enderio.integration.forestry.upgrades.ApiaristArmorUpgrade;
import crazypants.enderio.integration.forestry.upgrades.NaturalistEyeUpgrade;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class ForestryControl {

  public static void registerEventBus() {
    MinecraftForge.EVENT_BUS.register(ForestryControl.class);
  }

  @SubscribeEvent
  public static void registerFarmers(@Nonnull RegistryEvent.Register<IFarmerJoe> event) {
    ForestryFarmer farmer = new ForestryFarmer();
    event.getRegistry().register(farmer);
    if (farmer.isValid()) {
      Log.info("Farming Station: Forestry integration for farming loaded");
    } else if (ForestryItemStacks.FORESTRY_SAPLING == null) {
      Log.warn("Farming Station: Forestry integration for farming loaded but could not find Forestry sapling.");
    } else {
      Log.warn("Farming Station: Forestry integration for farming loaded but could not get species root for 'rootTrees'.");
    }
  }

  @SubscribeEvent
  public static void registerFertilizer(@Nonnull RegistryEvent.Register<IFertilizer> event) {
    final ForestryFertilizer fertilizer = new ForestryFertilizer();
    event.getRegistry().register(fertilizer);
    if (fertilizer.isValid()) {
      Log.info("Farming Station: Forestry integration for fertilizing loaded");
    } else {
      Log.warn("Farming Station: Forestry integration for fertilizing loaded but could not find Forestry fertilizer.");
    }
  }

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    final IForgeRegistry<IDarkSteelUpgrade> registry = event.getRegistry();
    registry.register(new NaturalistEyeUpgrade());
    registry.register(new ApiaristArmorUpgrade(EntityEquipmentSlot.FEET));
    registry.register(new ApiaristArmorUpgrade(EntityEquipmentSlot.LEGS));
    registry.register(new ApiaristArmorUpgrade(EntityEquipmentSlot.CHEST));
    registry.register(new ApiaristArmorUpgrade(EntityEquipmentSlot.HEAD));
    Log.info("Dark Steel Upgrades: Forestry integration loaded");
  }

  public static void init(FMLPreInitializationEvent event) {
  }

  public static void init(FMLInitializationEvent event) {
  }

  public static void init(FMLPostInitializationEvent event) {
  }

}
