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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.ModObject;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.material.Material;
import crazypants.enderio.material.PowderIngot;
import crazypants.enderio.power.Capacitors;

public class MachineRecipes {

  public static void addRecipes() {
    //Common ingredients
    ItemStack conduitBinder = new ItemStack(ModObject.itemMaterial.actualId, 4, Material.CONDUIT_BINDER.ordinal());
    ItemStack silicon = new ItemStack(ModObject.itemMaterial.actualId, 4, Material.SILICON.ordinal());
    ItemStack capacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    ItemStack activtedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, Capacitors.ACTIVATED_CAPACITOR.ordinal());
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, Capacitors.ENDER_CAPACITOR.ordinal());
    ItemStack basicGear = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack machineChassi = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.MACHINE_CHASSI.ordinal());

    ItemStack stirlingGen = new ItemStack(blockStirlingGenerator.actualId, 1, 0);
    GameRegistry.addShapedRecipe(stirlingGen, "bcb", "bfb", "gpg", 'b', Block.stoneBrick, 'f', Block.furnaceIdle, 'p', Block.pistonBase, 'g', basicGear, 'c',
        capacitor);

    ItemStack fusedQuartz = new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0);
    ItemStack reservoir = new ItemStack(blockReservoir.actualId, 2, 0);
    ItemStack glassPane = new ItemStack(Block.thinGlass, 1, 0);
    GameRegistry.addShapedRecipe(reservoir, "gfg", "gcg", "gfg", 'g', glassPane, 'c', Item.cauldron, 'f', fusedQuartz);

    ItemStack coalPowder = new ItemStack(ModObject.itemPowderIngot.actualId, 1, PowderIngot.POWDER_COAL.ordinal());

    ItemStack poweredLamp = new ItemStack(blockElectricLight.actualId, 1, 0);
    ItemStack glowstone = new ItemStack(Item.glowstone);
    GameRegistry.addShapedRecipe(poweredLamp, "ggg", "sds", "scs", 'g', Block.glass, 'd', glowstone, 's', silicon, 'c', capacitor);

    ItemStack crusher = new ItemStack(blockCrusher.actualId, 1, 0);
    GameRegistry.addShapedRecipe(crusher, "fff", "imi", "ici", 'f', Item.flint, 'm', machineChassi, 'i', Item.ingotIron, 'c', capacitor);

    ItemStack tesseract = new ItemStack(ModObject.blockHyperCube.actualId, 1, 0);
    ItemStack obsidian = new ItemStack(Block.obsidian);
    ItemStack phasedIron = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.PHASED_IRON.ordinal());
    GameRegistry.addShapedRecipe(tesseract, "oeo", "pdp", "oco", 'o', obsidian, 'e', Item.eyeOfEnder, 'c', enderCapacitor, 'p', phasedIron, 'd', Item.diamond);

  }

  public static void addOreDictionaryRecipes() {
    ItemStack capacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    ItemStack alloySmelter = new ItemStack(blockAlloySmelter.actualId, 1, 0);
    ItemStack machineChassi = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.MACHINE_CHASSI.ordinal());

    ArrayList<ItemStack> copperIngots = OreDictionary.getOres("ingotCopper");
    if(copperIngots != null && !copperIngots.isEmpty()) {
      GameRegistry.addRecipe(new ShapedOreRecipe(alloySmelter, "bfb", "cmc", "cCc", 'c', "ingotCopper", 'm', machineChassi, 'b', Block.stoneBrick, 'f',
          Block.furnaceIdle,
          'C', capacitor));
    } else {
      GameRegistry
          .addShapedRecipe(alloySmelter, "bfb", "imi", "iCi", 'i', Item.ingotIron, 'm', machineChassi, 'b', Block.stoneBrick, 'C', capacitor);
    }

    ItemStack painter = new ItemStack(blockPainter.actualId, 1, 0);
    ItemStack capacitorBank = new ItemStack(blockCapacitorBank.actualId, 1, 0);
    ItemStack activatedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 1);

    ArrayList<ItemStack> tinIngots = OreDictionary.getOres("ingotTin");
    if(tinIngots != null && !tinIngots.isEmpty()) {
      GameRegistry.addRecipe(new ShapedOreRecipe(painter, "qdq", "tmt", "tCt", 't', "ingotTin", 'm', machineChassi, 'q', Item.netherQuartz, 'd', Item.diamond,
          'C', capacitor));
      GameRegistry.addRecipe(new ShapedOreRecipe(capacitorBank, "tct", "crc", "tct", 't', "ingotTin", 'c', activatedCapacitor, 'r', Block.blockRedstone));
    }
    else {
      GameRegistry.addShapedRecipe(painter, "qdq", "imi", "iCi", 'i', Item.ingotIron, 'm', machineChassi, 'b', Block.netherBrick, 'C', activatedCapacitor);
      GameRegistry.addShapedRecipe(capacitorBank, "ici", "crc", "ici", 'i', Item.ingotIron, 'c', activatedCapacitor, 'r', Block.blockRedstone);
    }
    int dustCoal = OreDictionary.getOreID("dustCoal");
    ItemStack energeticAlloy = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ENERGETIC_ALLOY.ordinal());
    ItemStack solarPanel = new ItemStack(blockSolarPanel.actualId, 1, 0);
    ItemStack fusedQuartz = new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0);
    GameRegistry.addRecipe(new ShapedOreRecipe(solarPanel, "efe", "efe", "cdc", 'd', Block.daylightSensor, 'f', fusedQuartz, 'c', "dustCoal", 'e',
        energeticAlloy));
  }
}