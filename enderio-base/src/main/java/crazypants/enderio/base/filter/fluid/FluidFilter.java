package crazypants.enderio.base.filter.fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;
import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.integration.jei.IHaveGhostTargets.IFluidGhostSlot;
import crazypants.enderio.util.NbtValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidFilter implements IFluidFilter {

  private final FluidStack[] fluids = new FluidStack[5];
  private boolean isBlacklist;

  @Override
  public boolean isEmpty() {
    for (FluidStack f : fluids) {
      if (f != null) {
        return false;
      }
    }
    return true;
  }

  @Override
  public int size() {
    return fluids.length;
  }

  @Override
  public FluidStack getFluidStackAt(int index) {
    return fluids[index];
  }

  @Deprecated
  public boolean setFluid(int index, Fluid fluid) {
    fluids[index] = new FluidStack(fluid, 0);
    return true;
  }

  @Override
  public boolean setFluid(int index, @Nullable FluidStack fluid) {
    if (fluid == null || fluid.getFluid() == null) {
      fluids[index] = null;
    } else {
      fluids[index] = fluid;
    }
    return true;
  }

  @Override
  public boolean setFluid(int index, @Nonnull ItemStack stack) {
    if (stack.isEmpty()) {
      return setFluid(index, (FluidStack) null);
    }
    FluidStack f = FluidUtil.getFluidTypeFromItem(stack);
    if (f == null || f.getFluid() == null) {
      return setFluid(index, (FluidStack) null);
    }
    return setFluid(index, f);
  }

  @Override
  public boolean removeFluid(int index) {
    if (index < 0 || index >= fluids.length) {
      return false;
    }
    fluids[index] = null;
    return true;
  }

  @Deprecated
  protected void setFluid(int index, String fluidName) {
    Fluid f = FluidRegistry.getFluid(fluidName);
    setFluid(index, f);
  }

  @Override
  public boolean isBlacklist() {
    return isBlacklist;
  }

  @Override
  public void setBlacklist(boolean isBlacklist) {
    this.isBlacklist = isBlacklist;
  }

  @Override
  public boolean isDefault() {
    return !isBlacklist && isEmpty();
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    NbtValue.FILTER_BLACKLIST.setBoolean(nbtRoot, isBlacklist);

    NBTTagList fluidList = new NBTTagList();

    int index = 0;
    for (FluidStack f : fluids) {
      NBTTagCompound fRoot = new NBTTagCompound();
      if (f != null) {
        fRoot.setInteger("index", index);
        f.writeToNBT(fRoot);
        fluidList.appendTag(fRoot);
      }
      index++;
    }
    nbtRoot.setTag("fluids", fluidList);

  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {

    isBlacklist = NbtValue.FILTER_BLACKLIST.getBoolean(nbtRoot);
    clear();

    NBTTagList tagList = nbtRoot.getTagList("fluids", nbtRoot.getId());
    for (int i = 0; i < tagList.tagCount(); i++) {
      fluids[i] = FluidStack.loadFluidStackFromNBT(tagList.getCompoundTagAt(i));
    }
  }

  private void clear() {
    for (int i = 0; i < fluids.length; i++) {
      fluids[i] = null;
    }
  }

  @Override
  public boolean matchesFilter(FluidStack drained) {
    if (drained == null || drained.getFluid() == null) {
      return false;
    }
    if (isEmpty()) {
      return true;
    }
    for (FluidStack f : fluids) {
      if (f != null && f.isFluidEqual(drained)) {
        return !isBlacklist;
      }
    }
    return isBlacklist;
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
    setFluid(slot, stack);
  }

  @Override
  public void writeToByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  @Override
  public void readFromByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    readFromNBT(tag);
  }

  public void createGhostSlots(@Nonnull NNList<GhostSlot> slots, int xOffset, int yOffset, @Nullable Runnable cb) {
    int topY = yOffset;
    int leftX = xOffset;
    int index = 0;
    int numRows = 1;
    int rowSpacing = 2;
    int numCols = 5;
    for (int row = 0; row < numRows; ++row) {
      for (int col = 0; col < numCols; ++col) {
        int x = leftX + col * 18;
        int y = topY + row * 18 + rowSpacing * row;
        slots.add(new FluidFilterGhostSlot(index, x, y, cb));
        index++;
      }
    }
  }

  @Override
  public int getSlotCount() {
    return fluids.length;
  }

  class FluidFilterGhostSlot extends GhostSlot implements IFluidGhostSlot {
    private final Runnable cb;

    FluidFilterGhostSlot(int slot, int x, int y, Runnable cb) {
      this.setX(x);
      this.setY(y);
      this.setSlot(slot);
      this.cb = cb;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack, int realsize) {
      setFluid(getSlot(), stack);
      cb.run();
    }

    @Override
    public @Nonnull ItemStack getStack() {
      return ItemStack.EMPTY;
    }

    @Override
    public void putFluidStack(@Nonnull FluidStack fluid) {
      setFluid(getSlot(), fluid);
      cb.run();
    }

    @Override
    public void putFluid(@Nonnull Fluid fluid) {
      setFluid(getSlot(), fluid);
      cb.run();
    }
  }

}
