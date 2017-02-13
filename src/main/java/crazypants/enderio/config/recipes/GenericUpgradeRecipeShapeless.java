package crazypants.enderio.config.recipes;

import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class GenericUpgradeRecipeShapeless extends ShapelessOreRecipe {

  static {
    RecipeSorter.register("EnderIO:GenericUpgradeRecipeShapeless", GenericUpgradeRecipeShapeless.class, Category.SHAPED, "after:minecraft:shapeless");
  }

  public GenericUpgradeRecipeShapeless(Block result, Object... recipe) {
    super(result, recipe);
  }

  public GenericUpgradeRecipeShapeless(Item result, Object... recipe) {
    super(result, recipe);
  }

  public GenericUpgradeRecipeShapeless(ItemStack result, Object... recipe) {
    super(result, recipe);
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
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
