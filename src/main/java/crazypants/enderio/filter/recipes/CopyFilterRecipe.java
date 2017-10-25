package crazypants.enderio.filter.recipes;

import javax.annotation.Nonnull;

import crazypants.enderio.filter.FilterRegistry;
import crazypants.enderio.filter.IItemFilterUpgrade;
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
  public boolean matches(InventoryCrafting inv, World world) {
    
    int blankCount = 0;
    ItemStack filterInput = null;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack checkStack = inv.getStackInSlot(i);
      if (checkStack.getItem() instanceof IItemFilterUpgrade) {
        if(FilterRegistry.isFilterSet(checkStack)) {
          if(filterInput != null) {
            return false;
          }
          filterInput = checkStack;
        } else {
          if(!isSameTypeOrNull(filterInput, checkStack)) {
            return false;
          }
          blankCount++;
        }
      }
    }
    
    if(blankCount == 0 || filterInput == null) {      
      return false;
    }
    output = filterInput.copy();
    output.setCount(blankCount + 1);
    return true;

  }

  private boolean isSameTypeOrNull(ItemStack matchOrNull, ItemStack checkStack) {
    return matchOrNull == null || (matchOrNull.getItem() == checkStack.getItem() && matchOrNull.getItemDamage() == checkStack.getItemDamage());
  }

  @Override
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    return output.copy();
  }

  @Override
  public int getRecipeSize() {
    return 1;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return output;
  }

  @Override
  public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
    return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
  }

}
