package crazypants.enderio.material;

import static crazypants.enderio.ModObject.itemBasicCapacitor;
import static crazypants.enderio.ModObject.itemIndustrialBinder;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;
import crazypants.enderio.machine.alloy.BasicAlloyRecipe;

public class MaterialRecipes {

  public static void addRecipes() {

    ItemStack industialBinder = new ItemStack(itemIndustrialBinder.actualId, 4, 0);
    GameRegistry.addSmelting(Block.gravel.blockID, industialBinder, 0);

    industialBinder = new ItemStack(itemIndustrialBinder.actualId, 1, 0);
    
    ItemStack basicCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 0);
    GameRegistry.addShapedRecipe(basicCapacitor, "   ", "gwi", "RbR", 'b', industialBinder, 'g', new ItemStack(Item.ingotGold), 'i', new ItemStack(
        Item.ingotIron), 'R', new ItemStack(Item.redstoneRepeater), 'w', Block.cloth);
    
    
    ItemStack blueSteel = new ItemStack(ModObject.itemAlloy.actualId, 1, Alloy.BLUE_STEEL.ordinal());
    
    ItemStack activatedCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 1);
    GameRegistry.addShapedRecipe(activatedCapacitor, "   ", "gwi", "RbR", 'b', blueSteel, 'g', new ItemStack(ModObject.itemAlloy.actualId,1,Alloy.ACTIVATED_GOLD.ordinal()), 'i', 
        new ItemStack(ModObject.itemAlloy.actualId,1,Alloy.ACTIVATED_IRON.ordinal()), 'R', new ItemStack(Item.redstoneRepeater), 'w', Block.cloth);
    
    ItemStack enderCapacitor = new ItemStack(itemBasicCapacitor.actualId, 1, 2);
    GameRegistry.addShapedRecipe(enderCapacitor, "   ", "gwi", "RbR", 'b', blueSteel, 'g', new ItemStack(ModObject.itemAlloy.actualId,1,Alloy.ENDER_GOLD.ordinal()), 'i', 
        new ItemStack(ModObject.itemAlloy.actualId,1,Alloy.ENDER_IRON.ordinal()), 'R', new ItemStack(Item.redstoneRepeater), 'w', Block.cloth);
    
    
    int meta = 0;
    for(Alloy alloy : Alloy.values()) {
      ItemStack ingot = new ItemStack(ModObject.itemAlloy.actualId, 1, meta);
      IMachineRecipe recipe = new BasicAlloyRecipe(ingot, alloy.unlocalisedName, alloy.ingrediants);
      
      ItemStack nugget = new ItemStack(ModObject.itemAlloy.actualId, 9, meta + Alloy.values().length);           
      GameRegistry.addShapelessRecipe(nugget, ingot);
      nugget = nugget.copy();
      nugget.stackSize = 1;
      GameRegistry.addShapedRecipe(ingot, "nnn","nnn","nnn",'n',nugget);
      
      
      MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, recipe);
      meta++;
    }

    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, new FusedQuartzRecipe());
    
    ItemStack fusedQuartzFrame = new ItemStack(ModObject.itemFusedQuartzFrame.actualId,1,0);    
    GameRegistry.addShapedRecipe(fusedQuartzFrame, "bsb", "s s", "bsb", 'b', industialBinder, 's', new ItemStack(Item.stick));
    
  }

}
