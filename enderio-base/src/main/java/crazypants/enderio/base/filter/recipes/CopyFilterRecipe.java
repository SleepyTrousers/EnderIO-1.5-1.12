package crazypants.enderio.base.filter.recipes;

import javax.annotation.Nonnull;

import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class CopyFilterRecipe implements IRecipe {

  static {
    RecipeSorter.register("EnderIO:copyFilter", CopyFilterRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
  }

  private @Nonnull ItemStack output = ItemStack.EMPTY;

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {

    int blankCount = 0;
    @Nonnull
    ItemStack filterInput = Prep.getEmpty();
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      @Nonnull
      ItemStack checkStack = inv.getStackInSlot(i);
      if (checkStack.getItem() instanceof IItemFilterUpgrade) {
        if (FilterRegistry.isFilterSet(checkStack)) {
          if (Prep.isValid(filterInput)) {
            return false;
          }
          filterInput = checkStack;
        } else {
          if (!isSameTypeOrNull(filterInput, checkStack)) {
            return false;
          }
          blankCount++;
        }
      }
    }

    if (blankCount == 0 || Prep.isInvalid(filterInput)) {
      return false;
    }
    output = filterInput.copy();
    output.setCount(blankCount + 1);
    return true;

  }

  private boolean isSameTypeOrNull(@Nonnull ItemStack matchOrNull, @Nonnull ItemStack checkStack) {
    return Prep.isInvalid(matchOrNull) || (matchOrNull.getItem() == checkStack.getItem() && matchOrNull.getItemDamage() == checkStack.getItemDamage());
  }

  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    return output.copy();
  }

  @Override
  public int getRecipeSize() {
    return 1;
  }

  @Override
  public @Nonnull ItemStack getRecipeOutput() {
    return output;
  }

  @Override
  public @Nonnull NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
  }

}
