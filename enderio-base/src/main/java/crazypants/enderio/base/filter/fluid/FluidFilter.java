package crazypants.enderio.base.filter.fluid;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.FluidUtil;

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
  public boolean setFluid(int index, FluidStack fluid) {
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
  public void writeToNBT(@Nonnull NBTTagCompound root) {
    root.setBoolean("isBlacklist", isBlacklist);
    if (isEmpty()) {
      root.removeTag("fluidFilter");
      return;
    }

    NBTTagList fluidList = new NBTTagList();
    int index = 0;
    for (FluidStack f : fluids) {
      if (f != null) {
        NBTTagCompound fRoot = new NBTTagCompound();
        fRoot.setInteger("index", index);
        f.writeToNBT(fRoot);
        fluidList.appendTag(fRoot);
      }
      index++;
    }
    root.setTag("fluidStackFilter", fluidList);

  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound root) {
    isBlacklist = root.getBoolean("isBlacklist");
    if (root.hasKey("fluidFilter")) {
      NBTTagList fluidList = (NBTTagList) root.getTag("fluidFilter");
      for (int i = 0; i < fluidList.tagCount(); i++) {
        NBTTagCompound fRoot = fluidList.getCompoundTagAt(i);
        setFluid(fRoot.getInteger("index"), fRoot.getString("fluidName"));
      }
    } else if (root.hasKey("fluidStackFilter")) {
      NBTTagList fluidList = (NBTTagList) root.getTag("fluidStackFilter");
      for (int i = 0; i < fluidList.tagCount(); i++) {
        NBTTagCompound fRoot = fluidList.getCompoundTagAt(i);
        setFluid(fRoot.getInteger("index"), FluidStack.loadFluidStackFromNBT(fRoot));
      }
    } else {
      clear();
      return;
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
  public void writeToByteBuf(@Nonnull ByteBuf buf) {
    // TODO Auto-generated method stub

  }

  @Override
  public void readFromByteBuf(@Nonnull ByteBuf buf) {
    // TODO Auto-generated method stub

  }

}
