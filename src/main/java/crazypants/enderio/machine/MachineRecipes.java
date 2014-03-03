package crazypants.enderio.machine;

import static crazypants.enderio.EnderIO.itemBasicCapacitor;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.Material;
import crazypants.enderio.power.Capacitors;

public class MachineRecipes {

  public static void addRecipes() {
    //Common ingredients
    ItemStack conduitBinder = new ItemStack(EnderIO.itemMaterial, 4, Material.CONDUIT_BINDER.ordinal());
    ItemStack silicon = new ItemStack(EnderIO.itemMaterial, 4, Material.SILICON.ordinal());
    ItemStack capacitor = new ItemStack(itemBasicCapacitor, 1, 0);
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor, 1, Capacitors.ENDER_CAPACITOR.ordinal());
    ItemStack basicGear = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack machineChassi = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.MACHINE_CHASSI.ordinal());

    //stirling gen
    ItemStack stirlingGen = new ItemStack(EnderIO.blockStirlingGenerator, 1, 0);
    GameRegistry.addShapedRecipe(stirlingGen, "bbb", "bfb", "gpg", 'b', Blocks.stonebrick, 'f', Blocks.furnace, 'p', Blocks.piston, 'g', basicGear);

    //reservoir
    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
    ItemStack reservoir = new ItemStack(EnderIO.blockReservoir, 2, 0);
    ItemStack glassSides;
    if(Config.useHardRecipes) {
      glassSides = fusedQuartz;
    } else {
      glassSides = new ItemStack(Blocks.glass_pane, 1, 0);
    }
    GameRegistry.addShapedRecipe(reservoir, "gfg", "gcg", "gfg", 'g', glassSides, 'c', Items.cauldron, 'f', fusedQuartz);

    //mill
    ItemStack crusher = new ItemStack(EnderIO.blockCrusher, 1, 0);
    if(Config.useHardRecipes) {
      GameRegistry.addShapedRecipe(crusher, "ooo", "fpf", "cmc", 'f', Items.flint, 'm', machineChassi, 'i', Items.iron_ingot, 'c', capacitor, 'p',
          Blocks.piston,
          'o', Blocks.obsidian);
    } else {
      GameRegistry.addShapedRecipe(crusher, "fff", "imi", "ici", 'f', Items.flint, 'm', machineChassi, 'i', Items.iron_ingot, 'c', capacitor);
    }

    //transceiver
    ItemStack transceiver = new ItemStack(EnderIO.blockHyperCube, 1, 0);
    ItemStack obsidian = new ItemStack(Blocks.obsidian);
    ItemStack phasedGold = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal());
    GameRegistry
        .addShapedRecipe(transceiver, "oeo", "pdp", "oco", 'o', obsidian, 'e', Items.ender_eye, 'c', enderCapacitor, 'p', phasedGold, 'd', Items.diamond);

    //solar panel
    if(Config.photovoltaicCellEnabled) {
      ItemStack energeticAlloy = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ENERGETIC_ALLOY.ordinal());
      ItemStack solarPanel = new ItemStack(EnderIO.blockSolarPanel, 1, 0);
      if(Config.useHardRecipes) {
        GameRegistry.addRecipe(new ShapedOreRecipe(solarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', fusedQuartz, 'c', capacitor, 'e',
            energeticAlloy, 'p', phasedGold));
      } else {
        GameRegistry.addRecipe(new ShapedOreRecipe(solarPanel, "efe", "efe", "cdc", 'd', Blocks.daylight_detector, 'f', fusedQuartz, 'c', "dustCoal", 'e',
            energeticAlloy));
      }
    }

    //MJ Monitor
    ItemStack mJReader = new ItemStack(EnderIO.itemConduitProbe, 1, 0);
    ItemStack powerConduit = new ItemStack(EnderIO.itemPowerConduit, 1, 0);
    ItemStack redstoneConduit = new ItemStack(EnderIO.itemRedstoneConduit, 1, 2);
    ItemStack mJMonitor = new ItemStack(EnderIO.blockPowerMonitor, 1, 0);
    GameRegistry
        .addShapedRecipe(mJMonitor, "bmb", "bMb", "bcb", 'b', Blocks.stonebrick, 'e', Items.ender_eye, 'M', machineChassi, 'm', mJReader, 'p', powerConduit,
            'r', redstoneConduit, 'c', capacitor);

  }

  public static void addOreDictionaryRecipes() {
    ItemStack capacitor = new ItemStack(itemBasicCapacitor, 1, 0);
    ItemStack alloySmelter = new ItemStack(EnderIO.blockAlloySmelter, 1, 0);
    ItemStack machineChassi = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.MACHINE_CHASSI.ordinal());
    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);

    //alloy smelter
    if(Config.useHardRecipes) {
      GameRegistry.addShapedRecipe(alloySmelter, "nnn", "nfn", "CmC", 'o', Blocks.nether_brick, 'm', machineChassi, 'f', Blocks.furnace, 'C', capacitor, 'n',
          Blocks.nether_brick);
    } else {
      ArrayList<ItemStack> copperIngots = OreDictionary.getOres("ingotCopper");
      if(copperIngots != null && !copperIngots.isEmpty()) {
        GameRegistry.addRecipe(new ShapedOreRecipe(alloySmelter, "bfb", "cmc", "cCc", 'c', "ingotCopper", 'm', machineChassi, 'b', Blocks.stonebrick, 'f',
            Blocks.furnace, 'C', capacitor));
      } else {
        GameRegistry.addShapedRecipe(alloySmelter, "bfb", "imi", "iCi", 'i', Items.iron_ingot, 'm', machineChassi, 'b', Blocks.stonebrick, 'f',
            Blocks.furnace, 'C', capacitor);
      }
    }

    ArrayList<ItemStack> tinIngots = OreDictionary.getOres("ingotTin");
    Object metal;
    if(tinIngots != null && !tinIngots.isEmpty()) {
      metal = "ingotTin";
    } else {
      metal = Items.iron_ingot;
    }

    //painter
    ItemStack painter = new ItemStack(EnderIO.blockPainter, 1, 0);
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(painter, "qqq", "mdm", "CMC", 'm', metal, 'M', machineChassi, 'q', Items.quartz, 'd', Items.diamond,
          'C', capacitor, 'q', Items.quartz, 'd', Items.diamond));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(painter, "qdq", "mMm", "mCm", 'm', metal, 'M', machineChassi, 'q', Items.quartz, 'd', Items.diamond,
          'C', capacitor, 'q', Items.quartz, 'd', Items.diamond));
    }

    //capacitor bank
    ItemStack capacitorBank = new ItemStack(EnderIO.blockCapacitorBank, 1, 0);
    ItemStack activatedCapacitor = new ItemStack(itemBasicCapacitor, 1, 1);
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(capacitorBank, "rcr", "ccc", "rMr", 'm', metal, 'c', activatedCapacitor, 'r', Blocks.redstone_block, 'M',
          machineChassi));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(capacitorBank, "mcm", "crc", "mcm", 'm', metal, 'c', activatedCapacitor, 'r', Blocks.redstone_block));
    }

    //light
    ItemStack poweredLamp = new ItemStack(EnderIO.blockElectricLight, 1, 0);
    ItemStack glowstone = new ItemStack(Items.glowstone_dust);
    ArrayList<ItemStack> siliconEntries = OreDictionary.getOres("itemSilicon");
    Object silicon;
    if(siliconEntries == null || siliconEntries.isEmpty()) {
      silicon = new ItemStack(EnderIO.itemMaterial, 1, Material.SILICON.ordinal());
    } else {
      silicon = "itemSilicon";
    }
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(poweredLamp, "ggg", "sds", "scs", 'g', fusedQuartz, 'd', glowstone, 's', silicon, 'c', capacitor));

    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(poweredLamp, "ggg", "sds", "scs", 'g', Blocks.glass, 'd', glowstone, 's', silicon, 'c', capacitor));

    }

    //MJ Reader    
    ItemStack mJReader = new ItemStack(EnderIO.itemConduitProbe, 1, 0);
    ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack powerConduit = new ItemStack(EnderIO.itemPowerConduit, 1, 0);
    ItemStack redstoneConduit = new ItemStack(EnderIO.itemRedstoneConduit, 1, 2);

    GameRegistry.addRecipe(new ShapedOreRecipe(mJReader, "epe", "gcg", "srs", 'p', powerConduit, 'r', redstoneConduit, 'c', Items.comparator, 'g',
        Blocks.glass_pane, 's', silicon, 'e',
        electricalSteel));
  }
}