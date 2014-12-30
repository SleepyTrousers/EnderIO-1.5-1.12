package crazypants.enderio.conduit.item.filter;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import crazypants.enderio.conduit.item.FilterRegister;

public class CopyFilterRecipe implements IRecipe{
  
  static {
    RecipeSorter.register("EnderIO:copyFilter", CopyFilterRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
  }

  private ItemStack output;
  
  @Override
  public boolean matches(InventoryCrafting inv, World world) {
    
    ItemStack blankInput = null;
    ItemStack filterInput = null;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack checkStack = inv.getStackInSlot(i);
      if (checkStack != null && checkStack.getItem() instanceof IItemFilterUpgrade) {
        if(FilterRegister.isFilterSet(checkStack)) {
          if(filterInput != null || !isSameTypeOrNull(blankInput, checkStack)) {
            return false;
          }          
          filterInput = checkStack;
        } else {
          if(blankInput != null || !isSameTypeOrNull(filterInput, checkStack)) {
            return false;
          }
          blankInput = checkStack;
        }
      }
    }
    
    if(blankInput == null || filterInput == null) {      
      return false;
    }
    output = filterInput.copy();
    output.stackSize = 2;
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

}
