package crazypants.enderio.machine;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.item.FunctionUpgrade;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.buffer.BlockItemBuffer.Type;
import crazypants.enderio.machine.capbank.BlockItemCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.capbank.ConvertOldRecipe;
import crazypants.enderio.machine.light.BlockItemElectricLight;
import crazypants.enderio.material.FrankenSkull;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.Material;
import crazypants.enderio.power.Capacitors;

import static crazypants.enderio.EnderIO.itemBasicCapacitor;
import static crazypants.enderio.material.Alloy.*;
import static crazypants.enderio.material.Material.*;
import static crazypants.util.RecipeUtil.*;

public class MachineRecipes {

  public static void addRecipes() {
    //Common ingredients
    ItemStack capacitor = new ItemStack(itemBasicCapacitor, 1, 0);
    ItemStack capacitor2 = new ItemStack(itemBasicCapacitor, 1, 1);
    ItemStack capacitor3 = new ItemStack(itemBasicCapacitor, 1, 2);
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor, 1, Capacitors.ENDER_CAPACITOR.ordinal());
    String basicGear = MachinePart.BASIC_GEAR.oreDict;
    String machineChassi = MachinePart.MACHINE_CHASSI.oreDict;
    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
    ItemStack enlightedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 2);
    ItemStack zombieController = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal());
    ItemStack frankenZombie = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.FRANKEN_ZOMBIE.ordinal());
    ItemStack enderRes = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ENDER_RESONATOR.ordinal());
    ItemStack sentientEnder = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.SENTIENT_ENDER.ordinal());
    ItemStack obsidian = new ItemStack(Blocks.obsidian);

    String redstoneAlloy = REDSTONE_ALLOY.getOreIngot();
    String electricalSteel = ELECTRICAL_STEEL.getOreIngot();
    String darkSteel = DARK_STEEL.getOreIngot();
    String energeticAlloy = ENERGETIC_ALLOY.getOreIngot();
    String phasedGold = PHASED_GOLD.getOreIngot();
    String phasedIron = PHASED_IRON.getOreIngot();
    String soularium = SOULARIUM.getOreIngot();
    String endSteel = END_STEEL.getOreIngot();

    String vibCry = VIBRANT_CYSTAL.oreDict;
    String pulCry = PULSATING_CYSTAL.oreDict;
    String endCry = ENDER_CRYSTAL.oreDict;
    String binder = CONDUIT_BINDER.oreDict;
    String silicon = SILICON.oreDict;

    if (Config.addPeacefulRecipes) {
      addShaped(frankenZombie, "gdg", "dzd", "gdg", 'g', "ingotGold", 'd', "gemDiamond", 'z', zombieController);
      addShaped(sentientEnder, "ddd", "ded", "dgd", 'g', "blockGold", 'd', "gemDiamond", 'e', enderRes);
    }

    //stirling gen
    ItemStack stirlingGen = new ItemStack(EnderIO.blockStirlingGenerator, 1, 0);
    addShaped(stirlingGen, "bbb", "bfb", "gpg", 'b', Blocks.stonebrick, 'f', Blocks.furnace, 'p', Blocks.piston, 'g', basicGear);

    //Combustion Gen
    ItemStack res = new ItemStack(EnderIO.blockTank, 1, 0);
    ItemStack comGen = new ItemStack(EnderIO.blockCombustionGenerator, 1, 0);
    addShaped(comGen, "eee", "rmr", "gpg", 'e', electricalSteel, 'r', res, 'm', machineChassi, 'g', basicGear, 'p', Blocks.piston);

    //ZombieGen
    ItemStack zg = new ItemStack(EnderIO.blockZombieGenerator, 1, 0);
    addShaped(zg, "eee", "qzq", "qqq", 'e', electricalSteel, 'q', fusedQuartz, 'z', new ItemStack(EnderIO.itemFrankenSkull, 1, 0));

    //FrankenzombieGen
    ItemStack fg = new ItemStack(EnderIO.blockFrankenZombieGenerator, 1, 0);
    addShaped(fg, "eee", "qzq", "qqq", 'e', soularium, 'q', fusedQuartz, 'z', new ItemStack(EnderIO.itemFrankenSkull, 1, 2));

    //EnderGen
    ItemStack eg = new ItemStack(EnderIO.blockEnderGenerator, 1, 0);
    addShaped(eg, "eee", "qzq", "qqq", 'e', endSteel, 'q', fusedQuartz, 'z', new ItemStack(EnderIO.itemFrankenSkull, 1, 3));

    //KillerJoe
    ItemStack kj = new ItemStack(EnderIO.blockKillerJoe, 1, 0);
    addShaped(kj, "sss", "qzq", "qqq", 's', darkSteel, 'q', fusedQuartz, 'z', frankenZombie);

    //Wireless charger
    ItemStack wirelessCharger = new ItemStack(EnderIO.blockWirelessCharger);
    //addShaped(wirelessCharger, "svs", "imi", "scs", 's', electricSteel, 'i', silicon, 'm', machineChassi, 'c', capacitor3, 'v', vibCry);
    addShaped(wirelessCharger, "sss", "ses", "scs", 's', electricalSteel, 'c', capacitor3, 'e', enderRes);

    //Crafter
    ItemStack crafter = new ItemStack(EnderIO.blockCrafter, 1, 0);
    addShaped(crafter, "iti", "imi", "izi", 'i', "ingotIron", 't', "craftingTableWood", 'm', machineChassi, 'z', zombieController);

    //Powered Spawner
    ItemStack poweredSpawner = new ItemStack(EnderIO.blockPoweredSpawner);
    ItemStack zombieBit;
    if (Config.useHardRecipes) {
      zombieBit = frankenZombie;
    } else {
      zombieBit = zombieController;
    }
    addShaped(poweredSpawner, "ese", "eme", "vzv", 'e', electricalSteel, 's', "itemSkull", 'v', vibCry, 'm', machineChassi, 'z', zombieBit);

    if (Config.reservoirEnabled) {
      //reservoir
      ItemStack reservoir = new ItemStack(EnderIO.blockReservoir, 2, 0);
      Object glassSides;
      if (Config.useHardRecipes) {
        glassSides = "blockGlassHardened";
      } else {
        glassSides = "glass";
      }
      addShaped(reservoir, "gfg", "gcg", "gfg", 'g', glassSides, 'c', Items.cauldron, 'f', fusedQuartz);
    }

    //Tanks
    ItemStack basicTank = new ItemStack(EnderIO.blockTank, 1, 0);
    addShaped(basicTank, "ibi", "bgb", "ibi", 'g', "glass", 'i', "ingotIron", 'b', Blocks.iron_bars);

    ItemStack advTank = new ItemStack(EnderIO.blockTank, 1, 1);
    addShaped(advTank, "ibi", "bgb", "ibi", 'g', "blockGlassHardened", 'i', darkSteel, 'b', EnderIO.blockDarkIronBars);

    //mill
    ItemStack crusher = new ItemStack(EnderIO.blockCrusher, 1, 0);
    if (Config.useHardRecipes) {
      addShaped(crusher, "ooo", "fmf", "pip", 'f', Items.flint, 'm', machineChassi, 'i', "ingotIron", 'p', Blocks.piston, 'o', Blocks.obsidian);
    } else {
      addShaped(crusher, "fff", "imi", "ipi", 'f', Items.flint, 'm', machineChassi, 'i', "ingotIron", 'p', Blocks.piston);
    }

    //alloy smelter
    ItemStack alloySmelter = new ItemStack(EnderIO.blockAlloySmelter, 1, 0);
    if (Config.useHardRecipes) {
      addShaped(alloySmelter, "bfb", "fmf", "bcb", 'c', Items.cauldron, 'm', machineChassi, 'b', "blockIron", 'f', Blocks.furnace);
    } else {
      addShaped(alloySmelter, "bfb", "fmf", "bcb", 'c', Items.cauldron, 'm', machineChassi, 'b', "ingotIron", 'f', Blocks.furnace);

    }

    //Vat
    ItemStack still = new ItemStack(EnderIO.blockVat, 1, 0);
    addShaped(still, "eve", "tmt", "efe", 'v', Items.cauldron, 'm', machineChassi, 'e', electricalSteel, 'f', Blocks.furnace, 't', basicTank);

    //capacitor bank

    ItemStack capBank1 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.SIMPLE), 0);
    addShaped(capBank1, "bcb", "cmc", "bcb", 'b', "ingotIron", 'c', capacitor, 'm', "blockRedstone");
    ItemStack capBank2 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.ACTIVATED), 0);
    addShaped(capBank2, "bcb", "cmc", "bcb", 'b', electricalSteel, 'c', capacitor2, 'm', "blockRedstone");
    ItemStack capBank3 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.VIBRANT), 0);
    addShaped(capBank3, "bcb", "cmc", "bcb", 'b', electricalSteel, 'c', capacitor3, 'm', vibCry);

    ConvertOldRecipe convertRecipe = new ConvertOldRecipe();
    GameRegistry.addRecipe(convertRecipe);

    GameRegistry.addRecipe(new UpgradeCapBankRecipe(capBank2, "eee", "bcb", "eee", 'e', energeticAlloy, 'b', capBank1, 'c', capacitor2));
    GameRegistry.addRecipe(new UpgradeCapBankRecipe(capBank3, "vov", "NcN", "vov", 'v', phasedGold, 'o', capacitor3, 'N', capBank2, 'c', vibCry));

    //painter
    ItemStack painter = new ItemStack(EnderIO.blockPainter, 1, 0);
    if (Config.useHardRecipes) {
      addShaped(painter, "qqq", "mdm", "mMm", 'm', electricalSteel, 'M', machineChassi, 'q', "gemQuartz", 'd', "gemDiamond", 'd', "gemDiamond");
    } else {
      addShaped(painter, "qdq", "mMm", "mmm", 'm', electricalSteel, 'M', machineChassi, 'q', "gemQuartz", 'd', "gemDiamond", 'd', "gemDiamond");
    }

    //Farm
    ItemStack farm = new ItemStack(EnderIO.blockFarmStation, 1, 0);
    if (Config.useHardRecipes) {
      zombieBit = frankenZombie;
    } else {
      zombieBit = zombieController;
    }
    addShaped(farm, "ehe", "eme", "pzp", 'h', Items.diamond_hoe, 'm', machineChassi, 'e', electricalSteel, 'z', zombieController, 'p', pulCry);

    if (Config.transceiverEnabled) {
      //transceiver
      ItemStack transceiver = new ItemStack(EnderIO.blockTransceiver, 1, 0);
      if (Config.transceiverUseEasyRecipe) {
        addShaped(transceiver, "oeo", "pdp", "oco", 'o', obsidian, 'e', Items.ender_eye, 'c', enderCapacitor, 'p', phasedGold, 'd',
            Items.diamond);
      } else {
        addShaped(transceiver, "oeo", "pdp", "oco", 'o', electricalSteel, 'e', enderRes, 'c', enderCapacitor, 'p', fusedQuartz, 'd', endCry);
        addShapeless(transceiver, new ItemStack(EnderIO.blockHyperCube, 1, 0));
      }
    }

    //solar panel
    if (Config.photovoltaicCellEnabled) {

      ItemStack solarPanel = new ItemStack(EnderIO.blockSolarPanel, 1, 0);
      ItemStack advSolarPanel = new ItemStack(EnderIO.blockSolarPanel, 1, 1);
      ItemStack vibSolarPanel = new ItemStack(EnderIO.blockSolarPanel, 1, 2);

      if (Config.useHardRecipes) {
        addShaped(solarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', "blockGlassHardened", 'c', capacitor, 'e', energeticAlloy, 'p', phasedGold);
        addShaped(advSolarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', enlightedQuartz, 'c', capacitor2, 'e', phasedIron, 'p', phasedGold);
        addShaped(vibSolarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', enlightedQuartz, 'c', capacitor3, 'e', endSteel, 'p', phasedGold);
      } else {
        addShaped(solarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', "blockGlassHardened", 'p', silicon, 'e', energeticAlloy, 'c', redstoneAlloy);
        addShaped(advSolarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', enlightedQuartz, 'p', pulCry, 'e', phasedIron, 'c', electricalSteel);
        addShaped(vibSolarPanel, "efe", "pfp", "cdc", 'd', Blocks.daylight_detector, 'f', enlightedQuartz, 'p', vibCry, 'e', phasedGold, 'c', endSteel);

      }
    }

    //MJ Monitor
    ItemStack mJReader = new ItemStack(EnderIO.itemConduitProbe, 1, 0);
    ItemStack powerConduit = new ItemStack(EnderIO.itemPowerConduit, 1, 0);
    ItemStack redstoneConduit = new ItemStack(EnderIO.itemRedstoneConduit, 1, 2);
    ItemStack mJMonitor = new ItemStack(EnderIO.blockPowerMonitor, 1, 0);
    addShaped(mJMonitor, "sms", "sMs", "sps", 's', electricalSteel, 'M', machineChassi, 'm', mJReader, 'p', powerConduit, 'r', redstoneConduit);

    //Enchanter
    ItemStack enchanter = new ItemStack(EnderIO.blockEnchanter);
    addShaped(enchanter, "dbd", "sss", " s ", 'd', "gemDiamond", 'b', Items.book, 's', darkSteel);

    //Vacuum Chest
    ItemStack vacuumChest = new ItemStack(EnderIO.blockVacuumChest);
    addShaped(vacuumChest, "iii", "ici", "ipi", 'i', "ingotIron", 'c', "chestWood", 'p', pulCry);

    //Soul Binder
    Object enderBit;
    if (Config.soulBinderRequiresEndermanSkull) {
      enderBit = new ItemStack(EnderIO.blockEndermanSkull);
    } else {
      enderBit = pulCry;
    }
    ItemStack creeperSkull = new ItemStack(Items.skull, 1, 2);
    ItemStack zombieSkull = new ItemStack(Items.skull, 1, 4);
    ItemStack skeletonSkull = new ItemStack(Items.skull, 1, 0);
    ItemStack soulBinder = new ItemStack(EnderIO.blockSoulFuser);
    addShaped(soulBinder, "ses", "zmc", "sks", 's', soularium, 'm', machineChassi, 'e', enderBit, 'z', zombieSkull, 'c', creeperSkull, 'k', skeletonSkull);

    //Attractor
    ItemStack attractor = new ItemStack(EnderIO.blockAttractor);
    ItemStack attractorCrystal = new ItemStack(EnderIO.itemMaterial, 1, Material.ATTRACTOR_CRYSTAL.ordinal());
    addShaped(attractor, " c ", "ese", "sms", 's', soularium, 'm', machineChassi, 'c', attractorCrystal, 'e', energeticAlloy);

    //Aversion
    ItemStack aversion = new ItemStack(EnderIO.blockSpawnGuard);
    ItemStack tormentedEnderman = new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.TORMENTED.ordinal());
    addShaped(aversion, " c ", "ese", "sms", 's', soularium, 'm', machineChassi, 'c', tormentedEnderman, 'e', energeticAlloy);

    //Experience
    ItemStack xp = new ItemStack(EnderIO.blockExperianceOblisk);
    ItemStack xpItem = new ItemStack(EnderIO.itemXpTransfer);
    addShaped(xp, " x ", " s ", "sms", 's', soularium, 'm', machineChassi, 'x', xpItem);

    //Weather
    ItemStack weather = new ItemStack(EnderIO.blockWeatherObelisk);
    ItemStack weatherItem = new ItemStack(EnderIO.itemMaterial, 1, Material.WEATHER_CRYSTAL.ordinal());
    addShaped(weather, " x ", "ese", "sbs", 'x', weatherItem, 'e', energeticAlloy, 's', soularium, 'b',
        new ItemStack(EnderIO.blockCapBank, 1, CapBankType.getMetaFromType(CapBankType.SIMPLE)));

    ClearConfigRecipe inst = new ClearConfigRecipe();
    MinecraftForge.EVENT_BUS.register(inst);
    GameRegistry.addRecipe(inst);

    //wireless light
    ItemStack poweredLamp = new ItemStack(EnderIO.blockElectricLight, 1, BlockItemElectricLight.Type.ELECTRIC.ordinal());
    ItemStack poweredLampInv = new ItemStack(EnderIO.blockElectricLight, 1, BlockItemElectricLight.Type.ELECTRIC_INV.ordinal());
    ItemStack wirelessLamp = new ItemStack(EnderIO.blockElectricLight, 1, BlockItemElectricLight.Type.WIRELESS.ordinal());
    ItemStack wirelessLampInv = new ItemStack(EnderIO.blockElectricLight, 1, BlockItemElectricLight.Type.WIRELESS_INV.ordinal());
    addShapeless(wirelessLamp, poweredLamp, enderRes);
    addShapeless(wirelessLamp, wirelessLampInv, Blocks.redstone_torch);
    addShapeless(wirelessLampInv, poweredLampInv, enderRes);
    addShapeless(wirelessLampInv, wirelessLamp, Blocks.redstone_torch);

    //inventory panel
    ItemStack awareness = new ItemStack(EnderIO.itemFunctionUpgrade, 1, FunctionUpgrade.INVENTORY_PANEL.ordinal());
    addShaped(awareness, "bsb", "ses", "bib", 'b', binder, 's', silicon, 'e', Items.ender_eye, 'i', electricalSteel);
    ItemStack invPanel = new ItemStack(EnderIO.blockInventoryPanel);
    addShaped(invPanel, "dad", "psp", "dtd", 'd', darkSteel, 'a', awareness, 'p', pulCry, 's', sentientEnder, 't', basicTank);

    ItemStack machineChassis = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.MACHINE_CHASSI.ordinal());

    //powered light
    if (Config.useHardRecipes) {
      addShaped(poweredLamp, "ggg", "sds", "scs", 'g', fusedQuartz, 'd', "dustGlowstone", 's', "itemSilicon", 'c', capacitor);
    } else {
      addShaped(poweredLamp, "ggg", "sds", "scs", 'g', "glass", 'd', "dustGlowstone", 's', "itemSilicon", 'c', capacitor);
    }
    ItemStack invPoweredLamp = new ItemStack(EnderIO.blockElectricLight, 1, 1);
    addShapeless(invPoweredLamp, poweredLamp, Blocks.redstone_torch);
    addShapeless(poweredLamp, invPoweredLamp, Blocks.redstone_torch);

    //light
    ItemStack lamp = new ItemStack(EnderIO.blockElectricLight, 1, 2);
    addShaped(lamp, "   ", "ggg", "isi", 'g', "glass", 's', "glowstone", 'i', "ingotIron");
    ItemStack invLamp = new ItemStack(EnderIO.blockElectricLight, 1, 3);
    addShapeless(invLamp, lamp, Blocks.redstone_torch);
    addShapeless(lamp, invLamp, Blocks.redstone_torch);

    //MJ Reader

    addShaped(mJReader, "epe", "gcg", "srs", 'p', powerConduit, 'r', redstoneConduit, 'c', Items.comparator, 'g', "paneGlass", 's', "itemSilicon", 'e',
        electricalSteel);

    //Slice'N'Splice
    ItemStack sns = new ItemStack(EnderIO.blockSliceAndSplice);
    addShaped(sns, "iki", "ams", "iii", 'i', soularium, 'm', machineChassis, 'k', "itemSkull", 'a', Items.iron_axe, 's', Items.shears);

    //Buffer
    ItemStack itemBuffer = Type.getStack(Type.ITEM);
    ItemStack powerBuffer = Type.getStack(Type.POWER);
    ItemStack omniBuffer = Type.getStack(Type.OMNI);
    addShaped(itemBuffer, "isi", "scs", "isi", 'i', "ingotIron", 's', "ingotElectricalSteel", 'c', "chestWood");
    addShaped(powerBuffer, "isi", "sfs", "isi", 'i', "ingotIron", 's', "ingotElectricalSteel", 'f', machineChassis);
    addShapeless(omniBuffer, itemBuffer, powerBuffer);
  }
}
