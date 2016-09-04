package info.loenwind.autosave.handlers.forge;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class HandleFluidTank implements IHandler<FluidTank> {

  public HandleFluidTank() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return clazz != null && clazz.isAssignableFrom(FluidTank.class);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull FluidTank tank)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if(tank != null) {
      NBTTagCompound root = new NBTTagCompound();
      root.setInteger("capacity", tank.getCapacity());
      FluidStack fluid = tank.getFluid();
      if(fluid != null) {
        fluid.writeToNBT(root);
      }
      nbt.setTag(name, root);
    }
    return true;
  }

  @Override
  public FluidTank read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable FluidTank object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if(!nbt.hasKey(name)) {
      return object;
    }
    
    FluidTank res;
    NBTTagCompound root = nbt.getCompoundTag(name);
    int cap = root.getInteger("capacity");
    FluidStack stuff = FluidStack.loadFluidStackFromNBT(root);
    if(stuff == null) {
      res = new FluidTank(cap);
    } else {
      res = new FluidTank(stuff, cap);
    }
    return res;
  }
}
