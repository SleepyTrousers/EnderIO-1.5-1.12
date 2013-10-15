package crazypants.enderio.machine.alloy;

import net.minecraft.item.ItemStack;

public interface IAlloyRecipe {

  boolean isValidRecipeComponents(ItemStack... items);

}
