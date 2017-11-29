package crazypants.enderio.base.config.recipes;

import javax.annotation.Nonnull;

import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class GenericUpgradeRecipe extends ShapedOreRecipe {

  static {
    RecipeSorter.register("EnderIO:genericUpgradeRecipe", GenericUpgradeRecipe.class, Category.SHAPED, "after:minecraft:shaped");
  }

  public GenericUpgradeRecipe(Block result, Object... recipe) {
    super(result, recipe);
  }

  public GenericUpgradeRecipe(Item result, Object... recipe) {
    super(result, recipe);
  }

  public GenericUpgradeRecipe(@Nonnull ItemStack result, Object... recipe) {
    super(result, recipe);
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    ItemStack result = super.getCraftingResult(inv);
    for (int x = 0; x < inv.getSizeInventory(); x++) {
      ItemStack slot = inv.getStackInSlot(x);
      if (Prep.isValid(slot) && result.getItem() == slot.getItem() && slot.hasTagCompound()) {
        result.setTagCompound(slot.getTagCompound().copy());
        return result;
      }
    }
    return result;
  }

}
