package crazypants.enderio.conduit.liquid;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import crazypants.util.FluidUtil;

public class FluidFilter {

  private final Fluid[] fluids = new Fluid[5];

  public boolean isEmpty() {
    for (Fluid f : fluids) {
      if(f != null) {
        return false;
      }
    }
    return true;
  }
  
  public int size() {
    return fluids.length;
  }
  
  public Fluid getFluidAt(int index) {
    return fluids[index];
  }

  public boolean setFluid(int index, Fluid fluid) {
    fluids[index] = fluid;
    return true;
  }

  public boolean setFluid(int index, FluidStack fluid) {
    return setFluid(index, fluid.getFluid());
  }

  public boolean setFluid(int index, ItemStack stack) {
    if(stack == null) {
      return setFluid(index, (Fluid) null);
    }
    FluidStack f = FluidUtil.getFluidFromItem(stack);
    if(f == null || f.getFluid() == null) {
      return false;
    }
    return setFluid(index, f);
  }

  public boolean removeFluid(int index) {
    if(index < 0 || index >= fluids.length) {
      return false;
    }
    fluids[index] = null;
    return true;
  }

  protected void setFluid(int index, String fluidName) {
    Fluid f = FluidRegistry.getFluid(fluidName);
    fluids[index] = f;
  }

  public void writeToNBT(NBTTagCompound root) {
    if(isEmpty()) {
      root.removeTag("fluidFilter");
      return;
    }

    NBTTagList fluidList = new NBTTagList();
    int index = 0;
    for (Fluid f : fluids) {
      if(f != null) {
        NBTTagCompound fRoot = new NBTTagCompound();
        fRoot.setInteger("index", index);
        fRoot.setString("fluidName", f.getName());
        fluidList.appendTag(fRoot);
      }
      index++;
    }
    root.setTag("fluidFilter", fluidList);

  }

  public void readFromNBT(NBTTagCompound root) {
    if(!root.hasKey("fluidFilter")) {
      clear();
      return;
    }
    NBTTagList fluidList = (NBTTagList) root.getTag("fluidFilter");
    for (int i = 0; i < fluidList.tagCount(); i++) {
      NBTTagCompound fRoot = fluidList.getCompoundTagAt(i);
      setFluid(fRoot.getInteger("index"), fRoot.getString("fluidName"));
    }
  }

  private void clear() {
    for(int i=0;i<fluids.length;i++) {
      fluids[i] = null;
    }    
  }

  public boolean matchesFilter(FluidStack drained) {
    if(drained == null || drained.getFluid() == null) {
      return false;
    }
    if(isEmpty()) {
      return true;
    }
    for (Fluid f : fluids) {
      if(f != null && f.getID() == drained.getFluid().getID()) {
        return true;
      }
    }
    return false;
  }

}
