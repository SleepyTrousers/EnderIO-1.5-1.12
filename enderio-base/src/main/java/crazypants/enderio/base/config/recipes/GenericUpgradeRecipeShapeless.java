package crazypants.enderio.base.config.recipes;

import javax.annotation.Nonnull;

import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class GenericUpgradeRecipeShapeless extends ShapelessOreRecipe {

  public GenericUpgradeRecipeShapeless(Block result, Object... recipe) {
    super(null, result, recipe);
  }

  public GenericUpgradeRecipeShapeless(Item result, Object... recipe) {
    super(null, result, recipe);
  }

  public GenericUpgradeRecipeShapeless(@Nonnull ItemStack result, Object... recipe) {
    super(null, result, recipe);
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    ItemStack result = super.getCraftingResult(inv);
    for (int x = 0; x < inv.getSizeInventory(); x++) {
      ItemStack slot = inv.getStackInSlot(x);
      if (Prep.isValid(slot) && result.getItem() == slot.getItem() && slot.hasTagCompound()) {
        result.setTagCompound(slot.getTagCompound().copy());
        result.clearCustomName();
        return result;
      }
    }
    return result;
  }

}
