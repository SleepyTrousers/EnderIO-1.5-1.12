package crazypants.enderio.machine;

import static crazypants.enderio.ModObject.blockAlloySmelter;
import static crazypants.enderio.ModObject.blockElectricLight;
import static crazypants.enderio.ModObject.blockPainter;
import static crazypants.enderio.ModObject.blockReservoir;
import static crazypants.enderio.ModObject.blockSolarPanel;
import static crazypants.enderio.ModObject.blockStirlingGenerator;
import static crazypants.enderio.ModObject.itemBasicCapacitor;
import static crazypants.enderio.ModObject.itemIndustrialBinder;
import static crazypants.enderio.ModObject.blockCapacitorBank;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.ModObject;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.ItemCapacitor;
import crazypants.enderio.power.Capacitors;

public class MachineRecipes {

  public static void addRecipes() {
    ItemStack industialBinder = new ItemStack(itemIndustrialBinder.actualId, 1, 0);
    ItemStack capacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);

    ItemStack stirlingGen = new ItemStack(blockStirlingGenerator.actualId, 1, 0);
    GameRegistry.addShapedRecipe(stirlingGen, "bbb", "bfb", "bcb", 'b', industialBinder, 'f', Block.furnaceIdle, 'c', capacitor);

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
    ItemStack activatedIron = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ACTIVATED_IRON.ordinal());
    ItemStack activatedGold = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ACTIVATED_GOLD.ordinal());
    ItemStack fusedQuartz = new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0);

    GameRegistry.addShapedRecipe(solarPanel, "qqq", "gig", "bcb", 'q', fusedQuartz, 'g', activatedGold, 'i', activatedIron, 'b', industialBinder, 'c',
        capacitor);

    ItemStack electricLight = new ItemStack(blockElectricLight.actualId, 1, 0);
    ItemStack glowstone = new ItemStack(Item.lightStoneDust);
    GameRegistry.addShapedRecipe(electricLight, "bqb", "bgb", "bcb", 'q', fusedQuartz, 'g', glowstone, 'b', industialBinder, 'c', capacitor);
    
    ItemStack activtedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, Capacitors.ACTIVATED_CAPACITOR.ordinal());
    ItemStack capacitorBank = new ItemStack(blockCapacitorBank.actualId,1,0);
    GameRegistry.addShapedRecipe(capacitorBank , "bab", "aca", "bab", 'a', activtedCapacitor,'b', industialBinder, 'c', capacitor);
    

  }

}
