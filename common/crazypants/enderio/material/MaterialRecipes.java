package crazypants.enderio.material;

import static crazypants.enderio.ModObject.*;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.*;
import crazypants.enderio.machine.alloy.BasicAlloyRecipe;

public class MaterialRecipes {

  public static void addRecipes() {

    ItemStack industialBinder = new ItemStack(itemIndustrialBinder.actualId, 8, 0);
    GameRegistry.addSmelting(Block.gravel.blockID, industialBinder, 0);

    industialBinder = new ItemStack(itemIndustrialBinder.actualId, 1, 0);
    ItemStack basicCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    GameRegistry.addShapedRecipe(basicCapacitor, "   ", "gwi", "RbR", 'b', industialBinder, 'g', new ItemStack(Item.ingotGold), 'i', new ItemStack(
        Item.ingotIron), 'R', new ItemStack(Item.redstoneRepeater), 'w', Block.cloth);
    
    int meta = 0;
    for(Alloy alloy : Alloy.values()) {
      ItemStack ingot = new ItemStack(ModObject.itemAlloy.actualId, 1, meta);
      ItemStack nugget = new ItemStack(ModObject.itemAlloy.actualId, 9, meta + Alloy.values().length);
      IMachineRecipe recipe = new BasicAlloyRecipe(ingot, alloy.unlocalisedName, alloy.ingrediants);
      GameRegistry.addShapelessRecipe(nugget, ingot);
      MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, recipe);
      meta++;
    }

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, new FusedQuartzRecipe());
    
    ItemStack fusedQuartzFrame = new ItemStack(ModObject.itemFusedQuartzFrame.actualId,1,0);    
    GameRegistry.addShapedRecipe(fusedQuartzFrame, "bsb", "s s", "bsb", 'b', industialBinder, 's', new ItemStack(Item.stick));
    
  }

}
