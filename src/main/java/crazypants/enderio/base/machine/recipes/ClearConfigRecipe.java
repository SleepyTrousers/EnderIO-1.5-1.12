package crazypants.enderio.base.machine.recipes;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.machine.interfaces.IClearableConfiguration;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class ClearConfigRecipe implements IRecipe {

  static {
    RecipeSorter.register(EnderIO.DOMAIN + ":clear_config", ClearConfigRecipe.class, Category.SHAPELESS, "after:minecraft:shapeless");
  }

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

    final NBTTagCompound tagCompound = input.getTagCompound();
    if (Prep.isValid(input) && tagCompound != null && !tagCompound.hasNoTags()) {
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
      stack.setTagCompound(new NBTTagCompound());
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
  public int getRecipeSize() {
    return 1;
  }

  @Override
  public @Nonnull ItemStack getRecipeOutput() {
    return Prep.getEmpty();
  }

  @SubscribeEvent
  public void onTooltip(ItemTooltipEvent event) {
    if (ItemStack.areItemStacksEqual(lastOutput, event.getItemStack())) {
      if ((event.getEntityPlayer().openContainer instanceof ContainerWorkbench
          && ((ContainerWorkbench) event.getEntityPlayer().openContainer).craftResult.getStackInSlot(0) == event.getItemStack())
          || (event.getEntityPlayer().openContainer instanceof ContainerPlayer
              && ((ContainerPlayer) event.getEntityPlayer().openContainer).craftResult.getStackInSlot(0) == event.getItemStack())) {
        event.getToolTip().add(TextFormatting.RED.toString() + TextFormatting.ITALIC + EnderIO.lang.localize("machine.tooltip.clearConfig"));
      }
    }
  }

  @Override
  public @Nonnull NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    return NNList.withSize(inv.getSizeInventory(), Prep.getEmpty());
  }

}
