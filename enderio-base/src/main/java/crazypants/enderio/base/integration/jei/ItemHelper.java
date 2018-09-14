package crazypants.enderio.base.integration.jei;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.paint.PaintUtil;
import crazypants.enderio.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

public class ItemHelper {

  protected static final class Walker implements Callback<ItemStack> {
    private Item item;

    protected boolean set(Item item) {
      this.item = item;
      if (item instanceof ItemBlock) {
        final Block block = ((ItemBlock) item).getBlock();
        if (NullHelper.untrust(block) == null) {
          Log.error("ItemBlock " + item + " returned null from getBlock(). This is a major bug in the mod '" + PaintUtil.block2Modname(item) + "'.");
          return false;
        }
        if (NullHelper.untrust(Block.REGISTRY.getNameForObject(block)) == null) {
          Log.error(
              "ItemBlock " + item + " returned an unregistered block from getBlock(). This is a major bug in the mod '" + PaintUtil.block2Modname(item) + "'.");
          return false;
        }
        if (block instanceof IFluidBlock) {
          Fluid fluid = ((IFluidBlock) block).getFluid();
          if (fluid == null) {
            Log.error("Block " + block + " returned null from getFluid(). This is a major bug in the mod '" + PaintUtil.block2Modname(block) + "'.");
            return false;
          }
          final Block fblock = fluid.getBlock();
          if (fblock != null && NullHelper.untrust(Block.REGISTRY.getNameForObject(fblock)) == null) {
            Log.error("Fluid " + fluid + " (" + fluid.getName() + ", " + fluid.getClass() + ") from block " + block
                + " returned an unregistered block from getBlock(). This is a major bug in the mod '" + PaintUtil.block2Modname(block) + "'.");
            return false;
          }
        }
      }
      return true;
    }

    protected Walker() {
    }

    @Override
    public void apply(@Nonnull ItemStack stack) {
      if (Prep.isInvalid(stack)) {
        Log.error("The item " + item + " (" + item.getUnlocalizedName() + ") produces empty itemstacks in getSubItems(). This is a major bug in the mod '"
            + PaintUtil.block2Modname(item) + "'.");
        return;
      }
      if (stack.getItem() == Items.AIR) {
        Log.error("The item " + item + " (" + item.getUnlocalizedName()
            + ") produces itemstacks without item in getSubItems(). This is a major bug in the mod '" + PaintUtil.block2Modname(item) + "'.");
        return;
      }
      FluidStack fluidStack = FluidUtil.getFluidTypeFromItem(stack);
      if (fluidStack != null && fluidStack.getFluid() != null) {
        final Block block = fluidStack.getFluid().getBlock();
        if (block != null && NullHelper.untrust(Block.REGISTRY.getNameForObject(block)) == null) {
          Log.error("Fluid " + fluidStack.getFluid() + " (" + fluidStack.getFluid().getName() + ", " + fluidStack.getFluid().getClass() + ") from item " + stack
              + " returned an unregistered block from getBlock(). This is a major bug in the mod that fluid belongs to.");
          return;
        }
      }
      list.add(stack);
    }
  }

  private ItemHelper() {
  }

  private static final @Nonnull NNList<ItemStack> list = new NNList<ItemStack>();

  public static @Nonnull NNList<ItemStack> getValidItems() {
    if (list.isEmpty()) {
      final NNList<ItemStack> sublist = new NNList<ItemStack>();
      final Walker callback = new Walker();
      for (final Item item : Item.REGISTRY) {
        if (callback.set(item)) {
          item.getSubItems(CreativeTabs.SEARCH, sublist);
          sublist.apply(callback);
          sublist.clear();
        }
      }
    }
    return list;
  }

}
