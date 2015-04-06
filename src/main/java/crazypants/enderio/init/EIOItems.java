package crazypants.enderio.init;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.ChestGenHooks;
import crazypants.enderio.conduit.facade.FacadeRenderer;
import crazypants.enderio.conduit.facade.ItemConduitFacade;
import crazypants.enderio.conduit.gas.ItemGasConduit;
import crazypants.enderio.conduit.item.ItemExtractSpeedUpgrade;
import crazypants.enderio.conduit.item.ItemItemConduit;
import crazypants.enderio.conduit.item.filter.ItemBasicItemFilter;
import crazypants.enderio.conduit.item.filter.ItemExistingItemFilter;
import crazypants.enderio.conduit.item.filter.ItemModItemFilter;
import crazypants.enderio.conduit.item.filter.ItemPowerItemFilter;
import crazypants.enderio.conduit.liquid.ItemLiquidConduit;
import crazypants.enderio.conduit.me.ItemMEConduit;
import crazypants.enderio.conduit.power.ItemPowerConduit;
import crazypants.enderio.conduit.redstone.ItemRedstoneConduit;
import crazypants.enderio.conduit.render.ItemConduitRenderer;
import crazypants.enderio.config.Config;
import crazypants.enderio.enderface.ItemEnderface;
import crazypants.enderio.item.ItemConduitProbe;
import crazypants.enderio.item.ItemMagnet;
import crazypants.enderio.item.ItemSoulVessel;
import crazypants.enderio.item.ItemYetaWrench;
import crazypants.enderio.machine.spawner.BrokenSpawnerRenderer;
import crazypants.enderio.machine.spawner.ItemBrokenSpawner;
import crazypants.enderio.machine.xp.ItemXpTransfer;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.FusedQuartzFrameRenderer;
import crazypants.enderio.material.ItemAlloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.material.ItemFrankenSkull;
import crazypants.enderio.material.ItemFusedQuartzFrame;
import crazypants.enderio.material.ItemMachinePart;
import crazypants.enderio.material.ItemMaterial;
import crazypants.enderio.material.ItemPowderIngot;
import crazypants.enderio.material.MachinePartRenderer;
import crazypants.enderio.teleport.ItemTravelStaff;
import crazypants.enderio.teleport.telepad.ItemCoordSelector;

public class EIOItems {

  public static ItemCapacitor itemBasicCapacitor;
  public static ItemAlloy itemAlloy;
  public static ItemFusedQuartzFrame itemFusedQuartzFrame;
  public static ItemMachinePart itemMachinePart;
  public static ItemPowderIngot itemPowderIngot;
  public static ItemMaterial itemMaterial;
  public static ItemEnderface itemEnderface;
  public static ItemCoordSelector itemCoordsCard;
  public static ItemTravelStaff itemTravelStaff;
  public static ItemConduitFacade itemConduitFacade;
  public static ItemRedstoneConduit itemRedstoneConduit;
  public static ItemPowerConduit itemPowerConduit;
  public static ItemLiquidConduit itemLiquidConduit;
  public static ItemItemConduit itemItemConduit;
  public static ItemGasConduit itemGasConduit;
  public static ItemMEConduit itemMEConduit;
  public static ItemBasicItemFilter itemBasicFilterUpgrade;
  public static ItemExistingItemFilter itemExistingItemFilter;
  public static ItemModItemFilter itemModItemFilter;
  public static ItemPowerItemFilter itemPowerItemFilter;
  public static ItemExtractSpeedUpgrade itemExtractSpeedUpgrade;
  public static ItemBrokenSpawner itemBrokenSpawner;
  public static ItemYetaWrench itemYetaWench;
  public static ItemConduitProbe itemConduitProbe;
  public static ItemMagnet itemMagnet;
  public static ItemXpTransfer itemXpTransfer;
  public static ItemSoulVessel itemSoulVessel;
  public static ItemFrankenSkull itemFrankenSkull;

  public static void registerItems() {
    itemCoordsCard = ItemCoordSelector.create();
    itemFusedQuartzFrame = ItemFusedQuartzFrame.create();
    itemConduitFacade = ItemConduitFacade.create();
    itemBrokenSpawner = ItemBrokenSpawner.create();
    itemFrankenSkull = ItemFrankenSkull.create();
    itemRedstoneConduit = ItemRedstoneConduit.create();
    itemPowerConduit = ItemPowerConduit.create();
    itemLiquidConduit = ItemLiquidConduit.create();
    itemItemConduit = ItemItemConduit.create();
    itemGasConduit = ItemGasConduit.create();
    itemMEConduit = ItemMEConduit.create();
    itemBasicFilterUpgrade = ItemBasicItemFilter.create();
    itemExistingItemFilter = ItemExistingItemFilter.create();
    itemModItemFilter = ItemModItemFilter.create();
    itemPowerItemFilter = ItemPowerItemFilter.create();
    itemExtractSpeedUpgrade = ItemExtractSpeedUpgrade.create();
    itemBasicCapacitor = ItemCapacitor.create();
    itemMachinePart = ItemMachinePart.create();
    itemMaterial = ItemMaterial.create();
    itemAlloy = ItemAlloy.create();
    itemPowderIngot = ItemPowderIngot.create();
    itemYetaWench = ItemYetaWrench.create();
    itemEnderface = ItemEnderface.create();
    itemTravelStaff = ItemTravelStaff.create();
    itemConduitProbe = ItemConduitProbe.create();
    itemMagnet = ItemMagnet.create();
    itemXpTransfer = ItemXpTransfer.create();
    itemSoulVessel = ItemSoulVessel.create();
  }

  public static void registerDungeonLoot() {
    // Register Custom Dungeon Loot here
    if (Config.lootDarkSteel) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
          new WeightedRandomChestContent(new ItemStack(itemAlloy, 1, Alloy.DARK_STEEL.ordinal()), 1, 3, 15));
    }

    if (Config.lootItemConduitProbe) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
          new WeightedRandomChestContent(new ItemStack(itemConduitProbe, 1, 0), 1, 1, 10));
    }

    if (Config.lootQuartz) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
          new WeightedRandomChestContent(new ItemStack(Items.quartz), 3, 16, 20));
    }

    if (Config.lootNetherWart) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
          new WeightedRandomChestContent(new ItemStack(Items.nether_wart), 1, 4, 10));
    }

    if (Config.lootEnderPearl) {
      ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST).addItem(
          new WeightedRandomChestContent(new ItemStack(Items.ender_pearl), 1, 2, 30));
    }

    if (Config.lootElectricSteel) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal()), 2, 6, 20));
    }

    if (Config.lootRedstoneAlloy) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(itemAlloy, 1, Alloy.REDSTONE_ALLOY.ordinal()), 3, 6, 35));
    }

    if (Config.lootDarkSteel) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(itemAlloy, 1, Alloy.DARK_STEEL.ordinal()), 3, 6, 35));
    }

    if (Config.lootPhasedIron) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(itemAlloy, 1, Alloy.PHASED_IRON.ordinal()), 1, 2, 10));
    }

    if (Config.lootPhasedGold) {
      ChestGenHooks.getInfo(ChestGenHooks.VILLAGE_BLACKSMITH).addItem(
          new WeightedRandomChestContent(new ItemStack(itemAlloy, 1, Alloy.PHASED_GOLD.ordinal()), 1, 2, 5));
    }

    if (Config.lootTravelStaff) {
      ItemStack staff = new ItemStack(itemTravelStaff, 1, 0);
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_DESERT_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 3));
      ChestGenHooks.getInfo(ChestGenHooks.PYRAMID_JUNGLE_CHEST).addItem(new WeightedRandomChestContent(staff, 1, 1, 3));
    }
  }

  @SideOnly(Side.CLIENT)
  public static void registerItemRenderers() {
    MinecraftForgeClient.registerItemRenderer(itemBrokenSpawner, new BrokenSpawnerRenderer());
    MinecraftForgeClient.registerItemRenderer(itemFusedQuartzFrame, new FusedQuartzFrameRenderer());
  
    ItemConduitRenderer itemConRenderer = new ItemConduitRenderer();
    MinecraftForgeClient.registerItemRenderer(itemLiquidConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(itemPowerConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(itemRedstoneConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(itemItemConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(itemGasConduit, itemConRenderer);
    MinecraftForgeClient.registerItemRenderer(itemMEConduit, itemConRenderer);
  
    MinecraftForgeClient.registerItemRenderer(itemMachinePart, new MachinePartRenderer());
    MinecraftForgeClient.registerItemRenderer(itemConduitFacade, new FacadeRenderer());
  }

}
