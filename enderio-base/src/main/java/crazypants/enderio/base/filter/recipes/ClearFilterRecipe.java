package crazypants.enderio.base.filter.recipes;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ClearFilterRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

  private @Nonnull ItemStack output = ItemStack.EMPTY;

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
    int count = 0;
    @Nonnull
    ItemStack input = ItemStack.EMPTY;

    for (int i = 0; i < inv.getSizeInventory(); i++) {
      @Nonnull
      ItemStack checkStack = inv.getStackInSlot(i);
      if (checkStack.getItem() instanceof IItemFilterUpgrade) {
        count++;
      }
      if (count == 1 && !checkStack.isEmpty()) {
        input = checkStack;
      }
    }

    if (count == 1 && FilterRegistry.isFilterSet(input)) {
      ItemStack out = input.copy();
      out.setCount(1);
      out.setTagCompound(null);
      this.output = out;
    } else {
      this.output = ItemStack.EMPTY;
    }

    return count == 1 && Prep.isValid(output);
  }

  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    return output.copy();
  }

  @Override
  public @Nonnull ItemStack getRecipeOutput() {
    return output;
  }

  @SubscribeEvent
  public void onTooltip(ItemTooltipEvent event) {
    if (ItemStack.areItemStacksEqual(output, event.getItemStack())) {
      event.getToolTip().add(TextFormatting.RED.toString() + TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.clearConfigWarning"));
    }
  }

  @Override
  public @Nonnull NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
  }

  @Override
  public boolean canFit(int width, int height) {
    return width >= 1 && height >= 1;
  }

  @Override
  public boolean isDynamic() {
    return true;
  }
}
