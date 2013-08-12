package crazypants.enderio.conduit;

import static crazypants.enderio.ModObject.*;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadePainterRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.material.Alloy;

public class ConduitRecipes {

  public static void addRecipes() {
    ItemStack industialBinder = new ItemStack(itemIndustrialBinder.actualId, 1, 0);
    //ItemStack glassPane = new ItemStack(Block.thinGlass, 1, 0);
    ItemStack fusedQuartz = new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0);
    
    //Facade    
    GameRegistry.addShapedRecipe(new ItemStack(itemConduitFacade.actualId,1,0), "bbb", "b b", "bbb", 'b', industialBinder);
    MachineRecipeRegistry.instance.registerRecipe(blockPainter.unlocalisedName, new FacadePainterRecipe());
    
    //Liquids
    GameRegistry.addShapedRecipe(new ItemStack(itemLiquidConduit.actualId,4,0), "bgb", "g g", "bgb", 'b', industialBinder, 'g', fusedQuartz);
    
    //Power
    GameRegistry.addShapedRecipe(new ItemStack(itemPowerConduit.actualId,4,0), " b ", "bib", " b ", 'b', industialBinder, 'i', new ItemStack(Item.ingotIron));
    ItemStack activatedIron = new ItemStack(ModObject.itemAlloy.actualId,1,Alloy.ACTIVATED_IRON.ordinal());
    GameRegistry.addShapedRecipe(new ItemStack(itemPowerConduit.actualId,4,1), " b ", "bib", " b ", 'b', industialBinder, 'i', activatedIron);
    GameRegistry.addShapedRecipe(new ItemStack(itemPowerConduit.actualId,4,2), " b ", "bib", " b ", 'b', industialBinder, 'i', new ItemStack(ModObject.itemAlloy.actualId,1,Alloy.ENDER_IRON.ordinal()));
    
    
    //Redstone
    ItemStack redstoneConduit = new ItemStack(itemRedstoneConduit.actualId,1,0);
    GameRegistry.addShapedRecipe(redstoneConduit , " b ", "brb", " b ", 'b', industialBinder, 'r', new ItemStack(Item.redstone));
    GameRegistry.addShapedRecipe(new ItemStack(itemRedstoneConduit.actualId,1,1), " l ", "lcl", " l ", 'c', redstoneConduit, 'l', Block.lever);
    
    
  }
  
}
