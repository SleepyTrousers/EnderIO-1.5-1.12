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
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CopyFilterRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

  private @Nonnull ItemStack output = ItemStack.EMPTY;

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {

    // find the "source filter"
    ItemStack filterInput = Prep.getEmpty();
    int sourceSlot = -1;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack checkStack = inv.getStackInSlot(i);
      if (Prep.isValid(checkStack)) {
        if (checkStack.getItem() instanceof IItemFilterUpgrade) {
          if (FilterRegistry.isFilterSet(checkStack)) {
            if (sourceSlot >= 0) {
              // 2 configured filter found
              return false;
            }
            filterInput = checkStack;
            sourceSlot = i;
          }
          // else unconfigured filter, ignore for now
        } else {
          // a non-filter item was found
          return false;
        }
      }
    }

    // no source filter found
    if (sourceSlot < 0) {
      return false;
    }

    // check that all other filters (targets) are of the same type
    int blankCount = 0;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack checkStack = inv.getStackInSlot(i);
      if (i != sourceSlot && Prep.isValid(checkStack)) {
        if (!isSameType(filterInput, checkStack)) {
          return false;
        }
        blankCount++;
      }
    }

    if (blankCount == 0) {
      return false;
    }
    output = filterInput.copy();
    output.setCount(blankCount + 1);
    return true;

  }

  private boolean isSameType(@Nonnull ItemStack template, @Nonnull ItemStack candidate) {
    return template.getItem() == candidate.getItem() && template.getItemDamage() == candidate.getItemDamage();
  }

  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    return output.copy();
  }

  @Override
  public @Nonnull ItemStack getRecipeOutput() {
    return output;
  }

  @Override
  public @Nonnull NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
  }

  @Override
  public boolean canFit(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public boolean isDynamic() {
    return true;
  }

}
