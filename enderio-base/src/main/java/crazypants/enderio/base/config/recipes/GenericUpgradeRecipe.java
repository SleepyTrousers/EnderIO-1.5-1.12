package crazypants.enderio.base.config.recipes;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class GenericUpgradeRecipe extends ShapedOreRecipe {

  public GenericUpgradeRecipe(Block result, Object... recipe) {
    super(null, result, recipe);
  }

  public GenericUpgradeRecipe(Item result, Object... recipe) {
    super(null, result, recipe);
  }

  public GenericUpgradeRecipe(@Nonnull ItemStack result, Object... recipe) {
    super(null, result, recipe);
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    ItemStack result = super.getCraftingResult(inv);
    // Pass 1: Same item, different meta
    for (int x = 0; x < inv.getSizeInventory(); x++) {
      ItemStack slot = inv.getStackInSlot(x);
      if (Prep.isValid(slot) && result.getItem() == slot.getItem() && slot.hasTagCompound()) {
        result.setTagCompound(slot.getTagCompound().copy());
        return result;
      }
    }
    // Pass 2: Different item, both ours (better not define upgrade recipes that take 2 of our items that have nbt...)
    if (ModObjectRegistry.getModObject(result.getItem()) != null) {
      for (int x = 0; x < inv.getSizeInventory(); x++) {
        ItemStack slot = inv.getStackInSlot(x);
        if (ModObjectRegistry.getModObject(slot.getItem()) != null && slot.hasTagCompound()) {
          result.setTagCompound(slot.getTagCompound().copy());
          return result;
        }
      }
    }
    return result;
  }

}
