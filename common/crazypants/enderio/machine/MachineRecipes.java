package crazypants.enderio.machine;

import static crazypants.enderio.ModObject.blockAlloySmelter;
import static crazypants.enderio.ModObject.blockCapacitorBank;
import static crazypants.enderio.ModObject.blockCrusher;
import static crazypants.enderio.ModObject.blockElectricLight;
import static crazypants.enderio.ModObject.blockPainter;
import static crazypants.enderio.ModObject.blockReservoir;
import static crazypants.enderio.ModObject.blockSolarPanel;
import static crazypants.enderio.ModObject.blockStirlingGenerator;
import static crazypants.enderio.ModObject.itemBasicCapacitor;
import static crazypants.enderio.ModObject.itemPowerConduit;
import static crazypants.enderio.ModObject.itemRedstoneConduit;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.ModObject;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.Material;
import crazypants.enderio.power.Capacitors;

public class MachineRecipes {

  public static void addRecipes() {
    //Common ingredients
    ItemStack conduitBinder = new ItemStack(ModObject.itemMaterial.actualId, 4, Material.CONDUIT_BINDER.ordinal());
    ItemStack silicon = new ItemStack(ModObject.itemMaterial.actualId, 4, Material.SILICON.ordinal());
    ItemStack capacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, Capacitors.ENDER_CAPACITOR.ordinal());
    ItemStack basicGear = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack machineChassi = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.MACHINE_CHASSI.ordinal());

    //stirling gen
    ItemStack stirlingGen = new ItemStack(blockStirlingGenerator.actualId, 1, 0);
    GameRegistry.addShapedRecipe(stirlingGen, "bbb", "bfb", "gpg", 'b', Block.stoneBrick, 'f', Block.furnaceIdle, 'p', Block.pistonBase, 'g', basicGear);

    //reservoir
    ItemStack fusedQuartz = new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0);
    ItemStack reservoir = new ItemStack(blockReservoir.actualId, 2, 0);
    ItemStack glassSides;
    if(Config.useHardRecipes) {
      glassSides = fusedQuartz;
    } else {
      glassSides = new ItemStack(Block.thinGlass, 1, 0);
    }
    GameRegistry.addShapedRecipe(reservoir, "gfg", "gcg", "gfg", 'g', glassSides, 'c', Item.cauldron, 'f', fusedQuartz);

    //mill
    ItemStack crusher = new ItemStack(blockCrusher.actualId, 1, 0);
    if(Config.useHardRecipes) {
      GameRegistry.addShapedRecipe(crusher, "ooo", "fpf", "cmc", 'f', Item.flint, 'm', machineChassi, 'i', Item.ingotIron, 'c', capacitor, 'p',
          Block.pistonBase,
          'o', Block.obsidian);
    } else {
      GameRegistry.addShapedRecipe(crusher, "fff", "imi", "ici", 'f', Item.flint, 'm', machineChassi, 'i', Item.ingotIron, 'c', capacitor);
    }

    //transceiver
    ItemStack transceiver = new ItemStack(ModObject.blockHyperCube.actualId, 1, 0);
    ItemStack obsidian = new ItemStack(Block.obsidian);
    ItemStack phasedGold = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.PHASED_GOLD.ordinal());
    GameRegistry
        .addShapedRecipe(transceiver, "oeo", "pdp", "oco", 'o', obsidian, 'e', Item.eyeOfEnder, 'c', enderCapacitor, 'p', phasedGold, 'd', Item.diamond);

    //solar panel
    if(Config.photovoltaicCellEnabled) {
      ItemStack energeticAlloy = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ENERGETIC_ALLOY.ordinal());
      ItemStack solarPanel = new ItemStack(blockSolarPanel.actualId, 1, 0);
      if(Config.useHardRecipes) {
        GameRegistry.addRecipe(new ShapedOreRecipe(solarPanel, "efe", "pfp", "cdc", 'd', Block.daylightSensor, 'f', "glassHardened", 'c', capacitor, 'e',
            energeticAlloy, 'p', phasedGold));
      } else {
        GameRegistry.addRecipe(new ShapedOreRecipe(solarPanel, "efe", "efe", "cdc", 'd', Block.daylightSensor, 'f', "glassHardened", 'c', "dustCoal", 'e',
            energeticAlloy));
      }
    }

    //MJ Monitor
    ItemStack mJReader = new ItemStack(ModObject.itemMJReader.actualId, 1, 0);
    ItemStack powerConduit = new ItemStack(itemPowerConduit.actualId, 1, 0);
    ItemStack redstoneConduit = new ItemStack(itemRedstoneConduit.actualId, 1, 2);
    ItemStack mJMonitor = new ItemStack(ModObject.blockPowerMonitor.actualId, 1, 0);
    GameRegistry
        .addShapedRecipe(mJMonitor, "bmb", "bMb", "bcb", 'b', Block.stoneBrick, 'e', Item.eyeOfEnder, 'M', machineChassi, 'm', mJReader, 'p', powerConduit,
            'r', redstoneConduit, 'c', capacitor);

  }

  public static void addOreDictionaryRecipes() {
    ItemStack capacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    ItemStack alloySmelter = new ItemStack(blockAlloySmelter.actualId, 1, 0);
    ItemStack machineChassi = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.MACHINE_CHASSI.ordinal());
    ItemStack fusedQuartz = new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0);

    //alloy smelter
    if(Config.useHardRecipes) {
      GameRegistry.addShapedRecipe(alloySmelter, "nnn", "nfn", "CmC", 'o', Block.netherBrick, 'm', machineChassi, 'f', Block.furnaceIdle, 'C', capacitor, 'n',
          Block.netherBrick);
    } else {
      ArrayList<ItemStack> copperIngots = OreDictionary.getOres("ingotCopper");
      if(copperIngots != null && !copperIngots.isEmpty()) {
        GameRegistry.addRecipe(new ShapedOreRecipe(alloySmelter, "bfb", "cmc", "cCc", 'c', "ingotCopper", 'm', machineChassi, 'b', Block.stoneBrick, 'f',
            Block.furnaceIdle, 'C', capacitor));
      } else {
        GameRegistry.addShapedRecipe(alloySmelter, "bfb", "imi", "iCi", 'i', Item.ingotIron, 'm', machineChassi, 'b', Block.stoneBrick, 'f',
            Block.furnaceIdle, 'C', capacitor);
      }
    }

    ArrayList<ItemStack> tinIngots = OreDictionary.getOres("ingotTin");
    Object metal;
    if(tinIngots != null && !tinIngots.isEmpty()) {
      metal = "ingotTin";
    } else {
      metal = Item.ingotIron;
    }

    //painter
    ItemStack painter = new ItemStack(blockPainter.actualId, 1, 0);
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(painter, "qqq", "mdm", "CMC", 'm', metal, 'M', machineChassi, 'q', Item.netherQuartz, 'd', Item.diamond,
          'C', capacitor, 'q', Item.netherQuartz, 'd', Item.diamond));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(painter, "qdq", "mMm", "mCm", 'm', metal, 'M', machineChassi, 'q', Item.netherQuartz, 'd', Item.diamond,
          'C', capacitor, 'q', Item.netherQuartz, 'd', Item.diamond));
    }

    //capacitor bank
    ItemStack capacitorBank = new ItemStack(blockCapacitorBank.actualId, 1, 0);
    ItemStack activatedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 1);
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(capacitorBank, "rcr", "ccc", "rMr", 'm', metal, 'c', activatedCapacitor, 'r', Block.blockRedstone, 'M',
          machineChassi));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(capacitorBank, "mcm", "crc", "mcm", 'm', metal, 'c', activatedCapacitor, 'r', Block.blockRedstone));
    }

    //light
    ItemStack poweredLamp = new ItemStack(blockElectricLight.actualId, 1, 0);
    ItemStack glowstone = new ItemStack(Item.glowstone);
    ArrayList<ItemStack> siliconEntries = OreDictionary.getOres("itemSilicon");
    Object silicon;
    if(siliconEntries == null || siliconEntries.isEmpty()) {
      silicon = new ItemStack(ModObject.itemMaterial.actualId, 1, Material.SILICON.ordinal());
    } else {
      silicon = "itemSilicon";
    }
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(poweredLamp, "ggg", "sds", "scs", 'g', fusedQuartz, 'd', glowstone, 's', silicon, 'c', capacitor));

    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(poweredLamp, "ggg", "sds", "scs", 'g', Block.glass, 'd', glowstone, 's', silicon, 'c', capacitor));

    }

    //MJ Reader    
    ItemStack mJReader = new ItemStack(ModObject.itemMJReader.actualId, 1, 0);
    ItemStack electricalSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack powerConduit = new ItemStack(itemPowerConduit.actualId, 1, 0);
    ItemStack redstoneConduit = new ItemStack(itemRedstoneConduit.actualId, 1, 2);

    GameRegistry.addRecipe(new ShapedOreRecipe(mJReader, "epe", "gcg", "srs", 'p', powerConduit, 'r', redstoneConduit, 'c', Item.comparator, 'g',
        Block.thinGlass, 's', silicon, 'e',
        electricalSteel));
  }
}