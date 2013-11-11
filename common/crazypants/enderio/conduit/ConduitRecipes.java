package crazypants.enderio.conduit;

import static crazypants.enderio.ModObject.blockPainter;
import static crazypants.enderio.ModObject.itemConduitFacade;
import static crazypants.enderio.ModObject.itemItemConduit;
import static crazypants.enderio.ModObject.itemLiquidConduit;
import static crazypants.enderio.ModObject.itemPowerConduit;
import static crazypants.enderio.ModObject.itemRedstoneConduit;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.Config;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.facade.ItemConduitFacade.FacadePainterRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.material.Alloy;
import crazypants.enderio.material.Material;

public class ConduitRecipes {

  public static void addRecipes() {

    //Crafting Components
    ItemStack redstoneConduit = new ItemStack(itemRedstoneConduit.actualId, 1, 0);
    ItemStack conduitBinder = new ItemStack(ModObject.itemMaterial.actualId, 1, Material.CONDUIT_BINDER.ordinal());
    ItemStack fusedQuartz = new ItemStack(ModObject.blockFusedQuartz.actualId, 1, 0);
    ItemStack conductiveIron = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.CONDUCTIVE_IRON.ordinal());
    ItemStack energeticGold = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ENERGETIC_ALLOY.ordinal());
    ItemStack phasedGold = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.PHASED_GOLD.ordinal());
    ItemStack phasedIron = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.PHASED_IRON.ordinal());
    ItemStack redstoneAlloy = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.REDSTONE_ALLOY.ordinal());
    ItemStack electricalSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.ELECTRICAL_STEEL.ordinal());

    //Recipes
    GameRegistry.addShapedRecipe(new ItemStack(itemConduitFacade.actualId, 1, 0), "bbb", "b b", "bbb", 'b', conduitBinder);

    int numConduits = Config.numConduitsPerRecipe;
    GameRegistry.addShapedRecipe(new ItemStack(itemLiquidConduit.actualId, numConduits, 0), "bbb", "###", "bbb", 'b', conduitBinder, '#', fusedQuartz);
    GameRegistry.addShapedRecipe(new ItemStack(itemPowerConduit.actualId, numConduits, 0), "bbb", "###", "bbb", 'b', conduitBinder, '#', conductiveIron);
    GameRegistry.addShapedRecipe(new ItemStack(itemPowerConduit.actualId, numConduits, 1), "bbb", "###", "bbb", 'b', conduitBinder, '#', energeticGold);
    GameRegistry.addShapedRecipe(new ItemStack(itemPowerConduit.actualId, numConduits, 2), "bbb", "###", "bbb", 'b', conduitBinder, '#', phasedGold);
    GameRegistry.addShapedRecipe(new ItemStack(itemRedstoneConduit.actualId, numConduits, 0), "   ", "###", "   ", 'b', conduitBinder, '#', redstoneAlloy);
    GameRegistry.addShapedRecipe(new ItemStack(itemRedstoneConduit.actualId, 1, 1), "lbl", "bcb", "lbl", 'b', conduitBinder, 'c', redstoneConduit, 'l',
        Block.lever);
    GameRegistry.addShapedRecipe(new ItemStack(itemRedstoneConduit.actualId, numConduits, 2), "bbb", "###", "bbb", 'b', conduitBinder, '#', redstoneAlloy);

    ItemStack itemConduit = new ItemStack(itemItemConduit.actualId, numConduits, 0);
    GameRegistry.addShapedRecipe(itemConduit, "bbb", "###", "bbb", 'b', conduitBinder, '#', phasedIron);

    GameRegistry.addShapedRecipe(new ItemStack(itemItemConduit.actualId, 2, 1), "sis", "ehe", "sis", 's', electricalSteel, 'i', itemConduit, 'e',
        new ItemStack(Item.emerald), 'h', new ItemStack(Block.hopperBlock));

    MachineRecipeRegistry.instance.registerRecipe(blockPainter.unlocalisedName, new FacadePainterRecipe());

  }

}
