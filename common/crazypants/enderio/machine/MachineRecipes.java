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
import static crazypants.enderio.ModObject.itemIndustrialBinder;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.ModObject;
import crazypants.enderio.material.MachinePart;
import crazypants.enderio.power.Capacitors;

public class MachineRecipes {

  public static void addRecipes() {
    //Common ingredients
    ItemStack industialBinder = new ItemStack(itemIndustrialBinder.actualId, 1, 0);
    ItemStack capacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    ItemStack activtedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, Capacitors.ACTIVATED_CAPACITOR.ordinal());
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, Capacitors.ENDER_CAPACITOR.ordinal());
    ItemStack basicGear = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.BASIC_GEAR.ordinal());
    ItemStack redstoneInductor = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.REDSTONE_INDUCTOR.ordinal());

    ItemStack stirlingGen = new ItemStack(blockStirlingGenerator.actualId, 1, 0);
    GameRegistry.addShapedRecipe(stirlingGen, " s ", "sfs", "gpg", 's', Block.stone, 'f', Block.furnaceIdle, 'p', Block.pistonBase, 'g', basicGear);

    ItemStack painter = new ItemStack(blockPainter.actualId, 1, 0);
    ItemStack red = new ItemStack(Item.dyePowder, 1, 1);
    ItemStack green = new ItemStack(Item.dyePowder, 1, 2);
    ItemStack blue = new ItemStack(Item.dyePowder, 1, 4);
    GameRegistry.addShapedRecipe(painter, "bbb", "RGB", "bcb", 'b', industialBinder, 'R', red, 'G', green, 'B', blue, 'c', capacitor);

    ItemStack reservoir = new ItemStack(blockReservoir.actualId, 1, 0);
    ItemStack glassPane = new ItemStack(Block.thinGlass, 1, 0);
    GameRegistry.addShapedRecipe(reservoir, "bgb", "gBg", "bgb", 'b', industialBinder, 'g', glassPane, 'B', Item.bucketEmpty);

    ItemStack alloySmelter = new ItemStack(blockAlloySmelter.actualId, 1, 0);
    GameRegistry.addShapedRecipe(alloySmelter, "bbb", "CfC", "bcb", 'b', industialBinder, 'f', Block.furnaceIdle, 'c', capacitor, 'C', Item.cauldron);

    ItemStack solarPanel = new ItemStack(blockSolarPanel.actualId, 1, 0);
    ItemStack fusedQuartz = new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0);
    GameRegistry.addShapedRecipe(solarPanel, "fff", "fdf", "ici", 'd', Block.daylightSensor, 'f', fusedQuartz, 'c', activtedCapacitor, 'i', redstoneInductor);

    ItemStack electricLight = new ItemStack(blockElectricLight.actualId, 1, 0);
    ItemStack glowstone = new ItemStack(Item.glowstone);
    GameRegistry.addShapedRecipe(electricLight, "bqb", "bgb", "bcb", 'q', fusedQuartz, 'g', glowstone, 'b', industialBinder, 'c', capacitor);

    ItemStack capacitorBank = new ItemStack(blockCapacitorBank.actualId, 1, 0);
    GameRegistry.addShapedRecipe(capacitorBank, "bab", "aca", "bab", 'a', activtedCapacitor, 'b', industialBinder, 'c', capacitor);

    ItemStack tesseract = new ItemStack(ModObject.blockHyperCube.actualId, 1, 0);
    ItemStack obsidian = new ItemStack(Block.obsidian);
    ItemStack enderPearl = new ItemStack(Item.enderPearl);
    GameRegistry.addShapedRecipe(tesseract, "oeo", "e e", "oco", 'o', obsidian, 'e', enderPearl, 'c', enderCapacitor);

  }

  public static void addOreDictionaryRecipes() {
    ItemStack capacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    ItemStack crusher = new ItemStack(blockCrusher.actualId, 1, 0);
    ItemStack machineChassi = new ItemStack(ModObject.itemMachinePart.actualId, 1, MachinePart.MACHINE_CHASSI.ordinal());

    ArrayList<ItemStack> copperIngots = OreDictionary.getOres("ingotCopper");
    if(copperIngots != null && !copperIngots.isEmpty()) {
      GameRegistry.addRecipe(new ShapedOreRecipe(crusher, "frf", "cmc", "cpc", 'c', "ingotCopper", 'm', machineChassi, 'f', Item.flint, 'p', Block.pistonBase,
          'r', capacitor));
    } else {
      GameRegistry
          .addShapedRecipe(crusher, "frf", "imi", "ipi", 'i', Item.ingotIron, 'm', machineChassi, 'f', Item.flint, 'p', Block.pistonBase, 'r', capacitor);

    }

  }
}