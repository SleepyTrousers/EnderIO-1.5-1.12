package crazypants.enderio.config.recipes;

import crazypants.util.Prep;
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

  public GenericUpgradeRecipe(ItemStack result, Object... recipe) {
    super(result, recipe);
  }

  @SuppressWarnings("null")
  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    ItemStack result = super.getCraftingResult(inv);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        ItemStack slot = inv.getStackInRowAndColumn(x, y);
        if (Prep.isValid(slot) && result.getItem() == slot.getItem() && slot.hasTagCompound()) {
          result.setTagCompound(slot.getTagCompound().copy());
          return result;
        }
      }
    }
    return result;
  }

}
