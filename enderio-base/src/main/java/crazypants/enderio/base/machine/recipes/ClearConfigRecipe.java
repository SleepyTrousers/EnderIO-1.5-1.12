package crazypants.enderio.base.machine.recipes;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.machine.interfaces.IClearableConfiguration;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ClearConfigRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world) {
    return Prep.isValid(match(inv));
  }

  private @Nonnull ItemStack match(@Nonnull InventoryCrafting inv) {
    ItemStack input = Prep.getEmpty();

    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack checkStack = inv.getStackInSlot(i);
      if (Prep.isValid(checkStack)) {
        if (Prep.isValid(input)) {
          return Prep.getEmpty();
        }
        input = checkStack;
      }
    }

    if (Prep.isValid(input) && input.hasTagCompound()) {
      final Item item = input.getItem();
      if (item instanceof IClearableConfiguration) {
        return clear((IClearableConfiguration) item, input);
      }
      Block block = Block.getBlockFromItem(item);
      if (block instanceof IClearableConfiguration) {
        return clear((IClearableConfiguration) block, input);
      }
    }

    return Prep.getEmpty();
  }

  private @Nonnull ItemStack clear(@Nonnull IClearableConfiguration owner, @Nonnull ItemStack stack) {
    stack = stack.copy();
    if (owner instanceof IClearableConfiguration.Handler) {
      ((IClearableConfiguration.Handler) owner).clearConfiguration(stack);
      return stack;
    } else {
      stack.setTagCompound(null);
      stack.setCount(1);
      return stack;
    }
  }

  private @Nonnull ItemStack lastOutput = Prep.getEmpty();

  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    return lastOutput = match(inv);
  }

  @Override
  public @Nonnull ItemStack getRecipeOutput() {
    return Prep.getEmpty();
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  public void onTooltip(ItemTooltipEvent event) {
    final EntityPlayer player = event.getEntityPlayer();
    if (player != null
        && ((player.openContainer instanceof ContainerWorkbench
            && ((ContainerWorkbench) player.openContainer).craftResult.getStackInSlot(0) == event.getItemStack())
            || (player.openContainer instanceof ContainerPlayer
                && ((ContainerPlayer) player.openContainer).craftResult.getStackInSlot(0) == event.getItemStack()))
        && ItemStack.areItemStacksEqual(lastOutput, event.getItemStack())) {
      event.getToolTip().add(Lang.RECIPE_CLEAR.get());
    }

    if (Log.inDev && event.getItemStack().hasTagCompound()) {
      event.getToolTip().add("NBT: " + event.getItemStack().getTagCompound() + "(INDEV)");
    }
  }

  @Override
  public @Nonnull NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    return NNList.withSize(inv.getSizeInventory(), Prep.getEmpty());
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
