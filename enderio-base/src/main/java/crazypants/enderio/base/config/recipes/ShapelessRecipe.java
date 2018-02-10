package crazypants.enderio.base.config.recipes;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.init.ModObjectRegistry;
import crazypants.enderio.util.Prep;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ShapelessRecipe extends ShapelessRecipes {

  private final @Nonnull Things result;

  public ShapelessRecipe(@Nonnull ResourceLocation name, @Nonnull NonNullList<Ingredient> ingredients, @Nonnull Things result) {
    super("", new ItemStack(Items.SPAWN_EGG), ingredients);
    this.result = result;
    setRegistryName(name);
  }

  @Override
  public @Nonnull ItemStack getRecipeOutput() {
    if (result.isPotentiallyValid()) {
      ItemStack itemStack = result.getItemStack();
      if (Prep.isValid(itemStack)) {
        return itemStack;
      }
    }
    // JEI doesn't like recipes with empty results
    return super.getRecipeOutput();
  }

  @Override
  public @Nonnull NonNullList<Ingredient> getIngredients() {
    return result.isPotentiallyValid() ? super.getIngredients() : ShapedRecipe.NONE;
  }

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
    return result.isPotentiallyValid() ? super.matches(inv, worldIn) : false;
  }

  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    if (result.isPotentiallyValid()) {
      return result.getItemStack().copy();
    } else {
      return Prep.getEmpty();
    }
  }

  public static class Upgrade extends ShapelessRecipe {

    public Upgrade(@Nonnull ResourceLocation name, @Nonnull NonNullList<Ingredient> ingredients, @Nonnull Things result) {
      super(name, ingredients, result);
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
      ItemStack craftingResult = super.getCraftingResult(inv);
      // Pass 1: Same item, different meta
      for (int x = 0; x < inv.getSizeInventory(); x++) {
        ItemStack slot = inv.getStackInSlot(x);
        if (Prep.isValid(slot) && craftingResult.getItem() == slot.getItem() && slot.hasTagCompound()) {
          craftingResult.setTagCompound(slot.getTagCompound().copy());
          craftingResult.clearCustomName();
          return craftingResult;
        }
      }
      // Pass 2: Different item, both ours (better not define upgrade recipes that take 2 of our items that have nbt...)
      if (ModObjectRegistry.getModObject(craftingResult.getItem()) != null) {
        for (int x = 0; x < inv.getSizeInventory(); x++) {
          ItemStack slot = inv.getStackInSlot(x);
          if (ModObjectRegistry.getModObject(slot.getItem()) != null && slot.hasTagCompound()) {
            craftingResult.setTagCompound(slot.getTagCompound().copy());
            craftingResult.clearCustomName();
            return craftingResult;
          }
        }
      }
      return craftingResult;
    }

  }

}
