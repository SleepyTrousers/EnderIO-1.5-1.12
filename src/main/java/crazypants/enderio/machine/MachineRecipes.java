package crazypants.enderio.machine;

import static crazypants.enderio.EnderIO.*;

import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.FrankenSkull;
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
    ItemStack electricSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ELECTRICAL_STEEL.ordinal());
    ItemStack darkSteel = new ItemStack(EnderIO.itemAlloy, 1, Alloy.DARK_STEEL.ordinal());
    ItemStack phasedGold = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack phasedIron = new ItemStack(EnderIO.itemAlloy, 1, Alloy.PHASED_IRON.ordinal());
    ItemStack energeticAlloy = new ItemStack(EnderIO.itemAlloy, 1, Alloy.ENERGETIC_ALLOY.ordinal());
    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
    ItemStack enlightedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 2);
    ItemStack fusedGlass = new ItemStack(EnderIO.blockFusedQuartz, 1, 1);
    ItemStack soularium = new ItemStack(EnderIO.itemAlloy, 1, Alloy.SOULARIUM.ordinal());
    ItemStack zombieController = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal());
    ItemStack frankenZombie = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.FRANKEN_ZOMBIE.ordinal());

    //stirling gen
    ItemStack stirlingGen = new ItemStack(EnderIO.blockStirlingGenerator, 1, 0);
    GameRegistry.addShapedRecipe(stirlingGen, "bbb", "bfb", "gpg", 'b', Blocks.stonebrick, 'f', Blocks.furnace, 'p', Blocks.piston, 'g', basicGear);

    //Combustion Gen
    ItemStack res = new ItemStack(EnderIO.blockTank, 1, 0);
    ItemStack comGen = new ItemStack(EnderIO.blockCombustionGenerator, 1, 0);
    GameRegistry.addShapedRecipe(comGen, "eee", "rmr", "gpg", 'e', electricSteel, 'r', res, 'm', machineChassi, 'g', basicGear, 'p', Blocks.piston);

    //ZombieGen
    ItemStack zg = new ItemStack(EnderIO.blockZombieGenerator, 1, 0);
    GameRegistry.addShapedRecipe(zg, "eee", "qzq", "qqq", 'e', electricSteel, 'q', fusedQuartz, 'z', new ItemStack(EnderIO.itemFrankenSkull, 1, 0));

    //KillerJoe
    ItemStack kj = new ItemStack(EnderIO.blockKillerJoe, 1, 0);
    GameRegistry.addShapedRecipe(kj, "sss", "qzq", "qqq", 's', darkSteel, 'q', fusedQuartz, 'z', frankenZombie);

    //Wireless charger
    ItemStack wirelessCharger = new ItemStack(EnderIO.blockWirelessCharger);
    GameRegistry.addShapedRecipe(wirelessCharger, "svs", "imi", "scs", 's', electricSteel, 'i', silicon, 'm', machineChassi, 'c', capacitor3, 'v', vibCry);

    //Crafter
    ItemStack crafter = new ItemStack(EnderIO.blockCrafter, 1, 0);
    GameRegistry.addShapedRecipe(crafter, "iti", "imi", "izi", 'i', Items.iron_ingot, 't', Blocks.crafting_table, 'm', machineChassi, 'z', zombieController);

    //Powered Spawner
    ItemStack poweredSpawner = new ItemStack(EnderIO.blockPoweredSpawner);
    ItemStack zombieBit;
    if(Config.useHardRecipes) {
      zombieBit = frankenZombie;
    } else {
      zombieBit = zombieController;
    }
    GameRegistry.addRecipe(new ShapedOreRecipe(poweredSpawner, "ese", "eme", "vzv", 'e', electricSteel, 's', "itemSkull", 'v', vibCry, 'm', machineChassi, 'z',
        zombieBit));

    if(Config.reservoirEnabled) {
      //reservoir    
      ItemStack reservoir = new ItemStack(EnderIO.blockReservoir, 2, 0);
      Object glassSides;
      if(Config.useHardRecipes) {
        glassSides = "blockGlassHardened";
      } else {
        glassSides = "glass";
      }
      GameRegistry.addRecipe(new ShapedOreRecipe(reservoir, "gfg", "gcg", "gfg", 'g', glassSides, 'c', Items.cauldron, 'f', fusedQuartz));
    }

    //Tanks
    ItemStack basicTank = new ItemStack(EnderIO.blockTank, 1, 0);
    GameRegistry.addRecipe(new ShapedOreRecipe(basicTank, "ibi", "bgb", "ibi", 'g', "glass", 'i', Items.iron_ingot, 'b', Blocks.iron_bars));

    ItemStack advTank = new ItemStack(EnderIO.blockTank, 1, 1);
    GameRegistry.addRecipe(new ShapedOreRecipe(advTank, "ibi", "bgb", "ibi", 'g', "blockGlassHardened", 'i', darkSteel, 'b', EnderIO.blockDarkIronBars));

    //mill
    ItemStack crusher = new ItemStack(EnderIO.blockCrusher, 1, 0);
    if(Config.useHardRecipes) {
      GameRegistry.addShapedRecipe(crusher, "ooo", "fmf", "pip", 'f', Items.flint, 'm', machineChassi, 'i', Items.iron_ingot, 'p',
          Blocks.piston,
          'o', Blocks.obsidian);
    } else {
      GameRegistry.addShapedRecipe(crusher, "fff", "imi", "ipi", 'f', Items.flint, 'm', machineChassi, 'i', Items.iron_ingot, 'p', Blocks.piston);
    }

    //alloy smelter
    ItemStack alloySmelter = new ItemStack(EnderIO.blockAlloySmelter, 1, 0);
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(alloySmelter, "bfb", "fmf", "bcb", 'c', Items.cauldron, 'm', machineChassi, 'b', Blocks.iron_block, 'f',
          Blocks.furnace));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(alloySmelter, "bfb", "fmf", "bcb", 'c', Items.cauldron, 'm', machineChassi, 'b', Items.iron_ingot, 'f',
          Blocks.furnace));

    }

    //Vat
    ItemStack still = new ItemStack(EnderIO.blockVat, 1, 0);
    GameRegistry.addShapedRecipe(still, "eve", "tmt", "efe", 'v', Items.cauldron, 'm', machineChassi, 'e', electricSteel, 'f', Blocks.furnace, 't', basicTank);

    //capacitor bank
    ItemStack capacitorBank = new ItemStack(EnderIO.blockCapacitorBank, 1, 0);
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(capacitorBank, "rcr", "ccc", "rMr", 'm', electricSteel, 'c', capacitor2, 'r', Blocks.redstone_block, 'M',
          machineChassi));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(capacitorBank, "mcm", "crc", "mcm", 'm', electricSteel, 'c', capacitor2, 'r', Blocks.redstone_block));
    }

    //painter
    ItemStack painter = new ItemStack(EnderIO.blockPainter, 1, 0);
    if(Config.useHardRecipes) {
      GameRegistry.addRecipe(new ShapedOreRecipe(painter, "qqq", "mdm", "mMm", 'm', electricSteel, 'M', machineChassi, 'q', Items.quartz, 'd', Items.diamond,
          'd', Items.diamond));
    } else {
      GameRegistry.addRecipe(new ShapedOreRecipe(painter, "qdq", "mMm", "mmm", 'm', electricSteel, 'M', machineChassi, 'q', Items.quartz, 'd', Items.diamond,
          'd', Items.diamond));
    }

    //Farm
    ItemStack farm = new ItemStack(EnderIO.blockFarmStation, 1, 0);
    if(Config.useHardRecipes) {
      zombieBit = frankenZombie;
    } else {
      zombieBit = zombieController;
    }
    GameRegistry.addShapedRecipe(farm, "ehe", "eme", "pzp", 'h', Items.diamond_hoe, 'm', machineChassi, 'e', electricSteel, 'z', zombieController, 'p', pulCry);

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
        GameRegistry.addRecipe(new ShapedOreRecipe(solarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', "blockGlassHardened", 'c', capacitor,
            'e',
            energeticAlloy, 'p', phasedGold));
        GameRegistry.addRecipe(new ShapedOreRecipe(advSolarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', enlightedQuartz, 'c', capacitor2,
            'e',
            phasedIron, 'p', phasedGold));
      } else {
        GameRegistry.addRecipe(new ShapedOreRecipe(solarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', "blockGlassHardened", 'p', silicon,
            'e',
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
        .addShapedRecipe(mJMonitor, "sms", "sMs", "sps", 's', electricSteel, 'M', machineChassi, 'm', mJReader, 'p', powerConduit,
            'r', redstoneConduit);

    //Enchanter
    ItemStack enchanter = new ItemStack(EnderIO.blockEnchanter);
    GameRegistry.addShapedRecipe(enchanter, "dbd", "sss", " s ", 'd', Items.diamond, 'b', Items.book, 's', darkSteel);

    //Vacuum Chest
    ItemStack vacuumChest = new ItemStack(EnderIO.blockVacuumChest);
    GameRegistry.addShapedRecipe(vacuumChest, "iii", "ici", "ipi", 'i', Items.iron_ingot, 'c', Blocks.chest, 'p', pulCry);

    //Soul Binder
    ItemStack enderBit;
    if(Config.soulBinderRequiresEndermanSkull) {
      enderBit = new ItemStack(EnderIO.blockEndermanSkull);
    } else {
      enderBit = pulCry;
    }
    ItemStack creeperSkull = new ItemStack(Items.skull, 1, 2);
    ItemStack zombieSkull = new ItemStack(Items.skull, 1, 4);
    ItemStack skeletonSkull = new ItemStack(Items.skull, 1, 0);
    ItemStack soulBinder = new ItemStack(EnderIO.blockSoulFuser);
    GameRegistry.addShapedRecipe(soulBinder, "ses", "zmc", "sks", 's', soularium, 'm', machineChassi, 'e', enderBit, 'z', zombieSkull, 'c', creeperSkull, 'k',
        skeletonSkull);

    //Attractor
    ItemStack attractor = new ItemStack(EnderIO.blockAttractor);
    ItemStack attractorCrystal = new ItemStack(EnderIO.itemMaterial, 1, Material.ATTRACTOR_CRYSTAL.ordinal());
    GameRegistry
        .addShapedRecipe(attractor, " c ", "ese", "sms", 's', soularium, 'm', machineChassi, 'c', attractorCrystal, 'e', energeticAlloy);

    //Aversion
    ItemStack aversion = new ItemStack(EnderIO.blockSpawnGuard);
    ItemStack tormentedEnderman = new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.TORMENTED.ordinal());
    GameRegistry
        .addShapedRecipe(aversion, " c ", "ese", "sms", 's', soularium, 'm', machineChassi, 'c', tormentedEnderman, 'e', energeticAlloy);

    //Experience
    ItemStack xp = new ItemStack(EnderIO.blockExperianceOblisk);
    ItemStack xpItem = new ItemStack(EnderIO.itemXpTransfer);
    GameRegistry
        .addShapedRecipe(xp, " x ", " s ", "sms", 's', soularium, 'm', machineChassi, 'x', xpItem);

    ClearConfigRecipe inst = new ClearConfigRecipe();
    MinecraftForge.EVENT_BUS.register(inst);
    GameRegistry.addRecipe(inst);
  }

  public static void addOreDictionaryRecipes() {
    ItemStack capacitor = new ItemStack(itemBasicCapacitor, 1, 0);
    ItemStack machineChassi = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.MACHINE_CHASSI.ordinal());
    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);

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

    //Slice'N'Splice
    ItemStack soularium = new ItemStack(EnderIO.itemAlloy, 1, Alloy.SOULARIUM.ordinal());
    ItemStack sns = new ItemStack(EnderIO.blockSliceAndSplice);
    GameRegistry.addRecipe(new ShapedOreRecipe(sns, "iki", "ams", "iii", 'i', soularium, 'm', machineChassi, 'k', "itemSkull", 'a', Items.iron_axe, 's',
        Items.shears));

  }
}