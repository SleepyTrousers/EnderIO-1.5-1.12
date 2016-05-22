package crazypants.enderio.machine;

import static crazypants.enderio.EnderIO.itemBasicCapacitor;
import static crazypants.enderio.material.Alloy.DARK_STEEL;
import static crazypants.enderio.material.Alloy.ELECTRICAL_STEEL;
import static crazypants.enderio.material.Alloy.ENERGETIC_ALLOY;
import static crazypants.enderio.material.Alloy.PULSATING_IRON;
import static crazypants.enderio.material.Alloy.SOULARIUM;
import static crazypants.enderio.material.Alloy.VIBRANT_ALLOY;
import static crazypants.enderio.material.Material.CONDUIT_BINDER;
import static crazypants.enderio.material.Material.ENDER_CRYSTAL;
import static crazypants.enderio.material.Material.PULSATING_CYSTAL;
import static crazypants.enderio.material.Material.SILICON;
import static crazypants.enderio.material.Material.VIBRANT_CYSTAL;
import static crazypants.util.RecipeUtil.addShaped;
import static crazypants.util.RecipeUtil.addShapeless;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.item.FunctionUpgrade;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.skull.BlockEndermanSkull;
import crazypants.enderio.machine.buffer.BufferType;
import crazypants.enderio.machine.capbank.BlockItemCapBank;
import crazypants.enderio.machine.capbank.CapBankType;
import crazypants.enderio.machine.light.LightType;
import crazypants.enderio.material.FrankenSkull;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MachineRecipes {

  public static void addRecipes() {
    //Common ingredients
    ItemStack capacitor = new ItemStack(itemBasicCapacitor, 1, 0);
    ItemStack capacitor2 = new ItemStack(itemBasicCapacitor, 1, 1);
    ItemStack capacitor3 = new ItemStack(itemBasicCapacitor, 1, 2);
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor, 1, 2);
    String basicGear = MachinePart.BASIC_GEAR.oreDict;
    String machineChassi = MachinePart.MACHINE_CHASSI.oreDict;
    ItemStack fusedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 0);
    ItemStack enlightedQuartz = new ItemStack(EnderIO.blockFusedQuartz, 1, 2);
    ItemStack zombieController = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ZOMBIE_CONTROLLER.ordinal());
    ItemStack frankenZombie = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.FRANKEN_ZOMBIE.ordinal());
    ItemStack enderRes = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.ENDER_RESONATOR.ordinal());
    ItemStack sentientEnder = new ItemStack(EnderIO.itemFrankenSkull, 1, FrankenSkull.SENTIENT_ENDER.ordinal());
    ItemStack obsidian = new ItemStack(Blocks.OBSIDIAN);

    String electricalSteel = ELECTRICAL_STEEL.getOreIngot();
    String darkSteel = DARK_STEEL.getOreIngot();
    String energeticAlloy = ENERGETIC_ALLOY.getOreIngot();
    String phasedGold = VIBRANT_ALLOY.getOreIngot();
    String phasedIron = PULSATING_IRON.getOreIngot();
    String soularium = SOULARIUM.getOreIngot();

    String vibCry = VIBRANT_CYSTAL.oreDict;
    String pulCry = PULSATING_CYSTAL.oreDict;
    String endCry = ENDER_CRYSTAL.oreDict;
    String binder = CONDUIT_BINDER.oreDict;
    String silicon = SILICON.oreDict;


    //capacitor bank

    ItemStack capBank1 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.SIMPLE), 0);
    ItemStack capBank2 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.ACTIVATED), 0);
    ItemStack capBank3 = BlockItemCapBank.createItemStackWithPower(CapBankType.getMetaFromType(CapBankType.VIBRANT), 0);

    GameRegistry.addRecipe(new UpgradeCapBankRecipe(capBank2, "eee", "bcb", "eee", 'e', energeticAlloy, 'b', capBank1, 'c', capacitor2));
    GameRegistry.addRecipe(new UpgradeCapBankRecipe(capBank3, "vov", "NcN", "vov", 'v', phasedGold, 'o', capacitor3, 'N', capBank2, 'c', vibCry));


    ItemStack mJReader = new ItemStack(EnderIO.itemConduitProbe, 1, 0);

    // RF Gauge
    ItemStack rfGauge1 = new ItemStack(EnderIO.blockGauge, 1, 0);
    ItemStack rfGauge2 = new ItemStack(EnderIO.blockGauge, 2, 0);
    if (Config.recipeLevel < 1) {
      addShaped(rfGauge2, "i i", "imi", "i i", 'i', "ingotIron", 'e', electricalSteel, 'm', mJReader);
    } else if (Config.recipeLevel == 1) {
      addShaped(rfGauge1, "i i", "imi", "i i", 'i', "ingotIron", 'e', electricalSteel, 'm', mJReader);
    } else if (Config.recipeLevel == 2) {
      addShaped(rfGauge2, "i i", "eme", "i i", 'i', "ingotIron", 'e', electricalSteel, 'm', mJReader);
    } else {
      addShaped(rfGauge1, "i i", "eme", "iMi", 'i', "ingotIron", 'e', electricalSteel, 'm', mJReader, 'M', machineChassi);
    }

    //Enchanter
    ItemStack enchanter = new ItemStack(EnderIO.blockEnchanter);
    if (Config.recipeLevel < 3) {
      addShaped(enchanter, "dbd", "sss", " s ", 'd', "gemDiamond", 'b', Items.BOOK, 's', darkSteel);
    } else {
      addShaped(enchanter, "dbd", "sss", " s ", 'd', "blockDiamond", 'b', Blocks.BOOKSHELF, 's', DARK_STEEL.getOreBlock());
    }

    //Attractor
    ItemStack attractor = new ItemStack(EnderIO.blockAttractor);
    ItemStack attractorCrystal = new ItemStack(EnderIO.itemMaterial, 1, Material.ATTRACTOR_CRYSTAL.ordinal());
    addShaped(attractor, " c ", "ese", "sms", 's', soularium, 'm', machineChassi, 'c', attractorCrystal, 'e', energeticAlloy);

    //Aversion
    ItemStack aversion = new ItemStack(EnderIO.blockSpawnGuard);
    ItemStack tormentedEnderman = new ItemStack(EnderIO.blockEndermanSkull, 1, BlockEndermanSkull.SkullType.TORMENTED.ordinal());
    addShaped(aversion, " c ", "ese", "sms", 's', soularium, 'm', machineChassi, 'c', tormentedEnderman, 'e', energeticAlloy);

    // Relocator
    ItemStack relocator = new ItemStack(EnderIO.blockSpawnRelocator);
    ItemStack prismarine = new ItemStack(Blocks.PRISMARINE, 1, 0);
    if (Config.recipeLevel < 2) {
      addShaped(relocator, "p", "a", 'a', aversion, 'p', prismarine);
    } else if (Config.recipeLevel < 3) {
      addShaped(relocator, " p ", "pap", " p ", 'a', aversion, 'p', prismarine);
    } else {
      addShaped(relocator, "ppp", "pap", "ppp", 'a', aversion, 'p', prismarine);
    }

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
    ItemStack poweredLamp = new ItemStack(EnderIO.blockElectricLight, 1, LightType.ELECTRIC.ordinal());
    ItemStack poweredLampInv = new ItemStack(EnderIO.blockElectricLight, 1, LightType.ELECTRIC_INV.ordinal());
    ItemStack wirelessLamp = new ItemStack(EnderIO.blockElectricLight, 1, LightType.WIRELESS.ordinal());
    ItemStack wirelessLampInv = new ItemStack(EnderIO.blockElectricLight, 1, LightType.WIRELESS_INV.ordinal());
    addShapeless(wirelessLamp, poweredLamp, enderRes);
    addShapeless(wirelessLamp, wirelessLampInv, Blocks.REDSTONE_TORCH);
    addShapeless(wirelessLampInv, poweredLampInv, enderRes);
    addShapeless(wirelessLampInv, wirelessLamp, Blocks.REDSTONE_TORCH);

    //inventory panel
    ItemStack awareness = new ItemStack(EnderIO.itemFunctionUpgrade, 1, FunctionUpgrade.INVENTORY_PANEL.ordinal());
    addShaped(awareness, "bsb", "ses", "bib", 'b', binder, 's', silicon, 'e', Items.ENDER_EYE, 'i', electricalSteel);
    ItemStack invPanel = new ItemStack(EnderIO.blockInventoryPanel);
    ItemStack basicTank = new ItemStack(EnderIO.blockTank, 1, 0);
    addShaped(invPanel, "dad", "psp", "dtd", 'd', darkSteel, 'a', awareness, 'p', pulCry, 's', sentientEnder, 't', basicTank);

    ItemStack machineChassis = new ItemStack(EnderIO.itemMachinePart, 1, MachinePart.MACHINE_CHASSI.ordinal());

    //powered light
    if (Config.recipeLevel > 1) {
      addShaped(poweredLamp, "ggg", "sds", "scs", 'g', fusedQuartz, 'd', "dustGlowstone", 's', "itemSilicon", 'c', capacitor);
    } else {
      addShaped(poweredLamp, "ggg", "sds", "scs", 'g', "glass", 'd', "dustGlowstone", 's', "itemSilicon", 'c', capacitor);
    }
    ItemStack invPoweredLamp = new ItemStack(EnderIO.blockElectricLight, 1, 1);
    addShapeless(invPoweredLamp, poweredLamp, Blocks.REDSTONE_TORCH);
    addShapeless(poweredLamp, invPoweredLamp, Blocks.REDSTONE_TORCH);

    //light
    ItemStack lamp = new ItemStack(EnderIO.blockElectricLight, 1, 2);
    addShaped(lamp, "   ", "ggg", "isi", 'g', "glass", 's', "glowstone", 'i', "ingotIron");
    ItemStack invLamp = new ItemStack(EnderIO.blockElectricLight, 1, 3);
    addShapeless(invLamp, lamp, Blocks.REDSTONE_TORCH);
    addShapeless(lamp, invLamp, Blocks.REDSTONE_TORCH);

    //MJ Reader

    ItemStack powerConduit = new ItemStack(EnderIO.itemPowerConduit, 1, 0);
    ItemStack redstoneConduit = new ItemStack(EnderIO.itemRedstoneConduit, 1, 2);
    addShaped(mJReader, "epe", "gcg", "srs", 'p', powerConduit, 'r', redstoneConduit, 'c', Items.COMPARATOR, 'g', "paneGlass", 's', "itemSilicon", 'e',
        electricalSteel);

    //Slice'N'Splice
    ItemStack sns = new ItemStack(EnderIO.blockSliceAndSplice);
    addShaped(sns, "iki", "ams", "iii", 'i', soularium, 'm', machineChassis, 'k', "itemSkull", 'a', Items.IRON_AXE, 's', Items.SHEARS);

    //Buffer
    ItemStack itemBuffer = BufferType.getStack(BufferType.ITEM);
    ItemStack powerBuffer = BufferType.getStack(BufferType.POWER);
    ItemStack omniBuffer = BufferType.getStack(BufferType.OMNI);
    addShaped(itemBuffer, "isi", "scs", "isi", 'i', "ingotIron", 's', "ingotElectricalSteel", 'c', "chestWood");
    addShaped(powerBuffer, "isi", "sfs", "isi", 'i', "ingotIron", 's', "ingotElectricalSteel", 'f', machineChassis);
    addShapeless(omniBuffer, itemBuffer, powerBuffer);
  }
}
