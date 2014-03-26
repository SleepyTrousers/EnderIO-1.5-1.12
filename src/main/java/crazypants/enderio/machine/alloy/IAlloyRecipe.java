package crazypants.enderio.machine.alloy;

import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.recipe.IRecipe;

public interface IAlloyRecipe extends IRecipe {

  boolean isValidRecipeComponents(ItemStack... items);

  ItemStack getOutput();

}
