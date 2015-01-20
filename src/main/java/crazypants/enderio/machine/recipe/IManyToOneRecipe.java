package crazypants.enderio.machine.recipe;

import net.minecraft.item.ItemStack;

public interface IManyToOneRecipe extends IRecipe {

  boolean isValidRecipeComponents(ItemStack... items);

  ItemStack getOutput();

}
