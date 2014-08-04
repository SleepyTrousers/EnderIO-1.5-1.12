package crazypants.enderio.machine;

import static crazypants.enderio.EnderIO.*;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.Material;
import crazypants.enderio.power.Capacitors;

public class MachineRecipes {

  public static void addRecipes() {
    //Common ingredients
    ItemStack conduitBinder = new ItemStack(EnderIO.itemMaterial, 4, Material.CONDUIT_BINDER.ordinal());
    ItemStack capacitor = new ItemStack(itemBasicCapacitor, 1, 0);
    ItemStack capacitor2 = new ItemStack(itemBasicCapacitor, 1, 1);
    ItemStack capacitor3 = new ItemStack(itemBasicCapacitor, 1, 2);
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor, 1, Capacitors.ENDER_CAPACITOR.ordinal());
    ItemStack basicGear = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack machineChassi = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.MACHINE_CHASSI.ordinal());
    ItemStack silicon = new ItemStack(EnderIO.itemMaterial, 1, Material.SILICON.ordinal());
    ItemStack pulCry = new ItemStack(EnderIO.itemMaterial, 1, Material.PULSATING_CYSTAL.ordinal());
    ItemStack vibCry = new ItemStack(EnderIO.itemMaterial, 1, Material.VIBRANT_CYSTAL.ordinal());
    ItemStack electricSteel = new ItemStack(EnderIO.itemAlloy,1,Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack darkSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());
    ItemStack phasedGold = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack phasedIron = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_IRON.ordinal());
    ItemStack energeticAlloy = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ENERGETIC_ALLOY.ordinal());
    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
    ItemStack enlightedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 2);
    ItemStack fusedGlass = new ItemStack(EnderIO.blockFusedQuartz, 1, 1);

    //stirling gen
    ItemStack stirlingGen = new ItemStack(EnderIO.blockStirlingGenerator, 1, 0);
    GameRegistry.addShapedRecipe(stirlingGen, "bbb", "bfb", "gpg", 'b', Blocks.stonebrick, 'f', Blocks.furnace, 'p', Blocks.piston, 'g', basicGear);

    //Combustion Gen
    ItemStack res = new ItemStack(EnderIO.blockReservoir, 1, 0);
    ItemStack comGen = new ItemStack(EnderIO.blockCombustionGenerator, 1, 0);
    GameRegistry.addShapedRecipe(comGen, "eee", "rmr", "gcg", 'e', electricSteel, 'r', EnderIO.blockReservoir, 'm', machineChassi, 'g', basicGear,'c', capacitor);

    //ZombieGen
    ItemStack zg = new ItemStack(EnderIO.blockZombieGenerator, 1, 0);
    GameRegistry.addShapedRecipe(zg, "eee", "qzq", "qcq", 'e', electricSteel, 'q', fusedQuartz, 'z', new ItemStack(Items.skull, 1, 2),'c', capacitor);
    
    ItemStack wirelessCharger = new ItemStack(EnderIO.blockWirelessCharger);
    GameRegistry.addShapedRecipe(wirelessCharger, "svs", "imi", "scs", 's', electricSteel, 'i', silicon, 'm', machineChassi, 'c', capacitor3, 'v', vibCry);
    
    
    //Crafter
    ItemStack crafter = new ItemStack(EnderIO.blockCrafter, 1, 0);
    GameRegistry.addShapedRecipe(crafter, "iti", "imi", "ici", 'i', Items.iron_ingot, 't', Blocks.crafting_table, 'm', machineChassi, 'c', capacitor);
    
    //Powered Spawner
    ItemStack poweredSpawner = new ItemStack(EnderIO.blockPoweredSpawner);
    GameRegistry.addRecipe(new ShapedOreRecipe(poweredSpawner, "ese","eme","vcv",'e', electricSteel, 's', "itemSkull", 'v', vibCry,'m',machineChassi, 'c', capacitor));
    
    //reservoir    
    ItemStack reservoir = new ItemStack(EnderIO.blockReservoir, 2, 0);
    Object glassSides;
    if(Config.useHardRecipes) {
      glassSides = "glassHardened";
    } else {
      glassSides = "glass";
    }
    GameRegistry.addRecipe(new ShapedOreRecipe(reservoir, "gfg", "gcg", "gfg", 'g', glassSides, 'c', Items.cauldron, 'f', fusedQuartz));

    //Tanks
    ItemStack basicTank = new ItemStack(EnderIO.blockTank,1,0);
    GameRegistry.addRecipe(new ShapedOreRecipe(basicTank, "ibi", "bgb", "ibi", 'g', "glass", 'i', Items.iron_ingot, 'b', Blocks.iron_bars));
    
    ItemStack advTank = new ItemStack(EnderIO.blockTank,1,1);
    GameRegistry.addRecipe(new ShapedOreRecipe(advTank, "ibi", "bgb", "ibi", 'g', "glassHardened", 'i', darkSteel, 'b', EnderIO.blockDarkIronBars));
    
    //mill
    ItemStack crusher = new ItemStack(EnderIO.blockCrusher, 1, 0);
    if(Config.useHardRecipes) {
      GameRegistry.addShapedRecipe(crusher, "ooo", "fpf", "cmc", 'f', Items.flint, 'm', machineChassi, 'i', Items.iron_ingot, 'c', capacitor, 'p',
          Blocks.piston,
          'o', Blocks.obsidian);
    } else {
      GameRegistry.addShapedRecipe(crusher, "fff", "imi", "ici", 'f', Items.flint, 'm', machineChassi, 'i', Items.iron_ingot, 'c', capacitor);
    }

    //Still
    ItemStack still = new ItemStack(EnderIO.blockVat, 1, 0);
    GameRegistry.addShapedRecipe(still, "eve", "eme", "ece", 'v', Items.cauldron, 'm', machineChassi, 'e', electricSteel, 'c', capacitor);

    //Farm
    ItemStack farm = new ItemStack(EnderIO.blockFarmStation, 1, 0);
    GameRegistry.addShapedRecipe(farm, "ehe", "eme", "pcp", 'h', Items.diamond_hoe, 'm', machineChassi, 'e', electricSteel, 'c', capacitor, 'p', pulCry);

    //transceiver
    ItemStack transceiver = new ItemStack(EnderIO.blockHyperCube, 1, 0);
    ItemStack obsidian = new ItemStack(Blocks.obsidian);
    
    GameRegistry
    .addShapedRecipe(transceiver, "oeo", "pdp", "oco", 'o', obsidian, 'e', Items.ender_eye, 'c', enderCapacitor, 'p', phasedGold, 'd', Items.diamond);

    //solar panel
    if(Config.photovoltaicCellEnabled) {
      
      ItemStack solarPanel = new ItemStack(EnderIO.blockSolarPanel, 1, 0);
      ItemStack advSolarPanel = new ItemStack(EnderIO.blockSolarPanel, 1, 1);
      if(Config.useHardRecipes) {
        GameRegistry.addRecipe(new ShapedOreRecipe(solarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', "glassHardened", 'c', capacitor, 'e',
            energeticAlloy, 'p', phasedGold));
        GameRegistry.addRecipe(new ShapedOreRecipe(advSolarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', enlightedQuartz, 'c', capacitor2, 'e',
            phasedIron, 'p', phasedGold));
      } else {
        GameRegistry.addRecipe(new ShapedOreRecipe(solarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', "glassHardened", 'p', silicon, 'e',
            energeticAlloy, 'c', electricSteel));
        GameRegistry.addRecipe(new ShapedOreRecipe(advSolarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', enlightedQuartz, 'p', vibCry, 'e',
            phasedGold, 'c', phasedIron));
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
    
    //Enchanter
    ItemStack enchanter = new ItemStack(EnderIO.blockEnchanter);
    GameRegistry.addShapedRecipe(enchanter, "dbd", "sss", " s ", 'd', Items.diamond, 'b', Items.book, 's', darkSteel);
    
    //Vacuum Chest
    ItemStack vacuumChest = new ItemStack(EnderIO.blockVacuumChest);
    GameRegistry.addShapedRecipe(vacuumChest, "iii", "ici","ipi", 'i', Items.iron_ingot, 'c', Blocks.chest, 'p', pulCry);
    

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
      if(copperIngots != null && !copperIngots.isEmpty() && Config.useModMetals) {
        GameRegistry.addRecipe(new ShapedOreRecipe(alloySmelter, "bfb", "cmc", "cCc", 'c', "ingotCopper", 'm', machineChassi, 'b', Blocks.stonebrick, 'f',
            Blocks.furnace, 'C', capacitor));
      } else {
        GameRegistry.addShapedRecipe(alloySmelter, "bfb", "imi", "iCi", 'i', Items.iron_ingot, 'm', machineChassi, 'b', Blocks.stonebrick, 'f',
            Blocks.furnace, 'C', capacitor);
      }
    }

    ArrayList<ItemStack> tinIngots = OreDictionary.getOres("ingotTin");
    Object metal;
    if(tinIngots != null && !tinIngots.isEmpty() && Config.useModMetals) {
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

    //powered light
    ItemStack poweredLamp = new ItemStack(EnderIO.blockElectricLight, 1, 0);
    ItemStack glowstone = new ItemStack(Items.glowstone_dust);
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(poweredLamp, "ggg", "sds", "scs", 'g', fusedQuartz, 'd', glowstone, 's', "itemSilicon", 'c', capacitor));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(poweredLamp, "ggg", "sds", "scs", 'g', "glass", 'd', glowstone, 's', "itemSilicon", 'c', capacitor));
    }    
    ItemStack invPoweredLamp = new ItemStack(EnderIO.blockElectricLight, 1, 1);
    GameRegistry.addShapelessRecipe(invPoweredLamp, poweredLamp, Blocks.redstone_torch);
    GameRegistry.addShapelessRecipe(poweredLamp, invPoweredLamp, Blocks.redstone_torch);
    
    //light
    ItemStack lamp = new ItemStack(EnderIO.blockElectricLight, 1, 2);
    GameRegistry.addRecipe(new ShapedOreRecipe(lamp, "   ", "ggg", "isi", 'g', "glass", 's', Blocks.glowstone, 'i', Items.iron_ingot));
    ItemStack invLamp = new ItemStack(EnderIO.blockElectricLight, 1, 3);
    GameRegistry.addShapelessRecipe(invLamp, lamp, Blocks.redstone_torch);
    GameRegistry.addShapelessRecipe(lamp, invLamp, Blocks.redstone_torch); 
    

    //MJ Reader
    ItemStack mJReader = new ItemStack(EnderIO.itemConduitProbe, 1, 0);
    ItemStack electricalSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack powerConduit = new ItemStack(EnderIO.itemPowerConduit, 1, 0);
    ItemStack redstoneConduit = new ItemStack(EnderIO.itemRedstoneConduit, 1, 2);

    GameRegistry.addRecipe(new ShapedOreRecipe(mJReader, "epe", "gcg", "srs", 'p', powerConduit, 'r', redstoneConduit, 'c', Items.comparator, 'g',
        Blocks.glass_pane, 's', "itemSilicon", 'e',
        electricalSteel));
  }
}