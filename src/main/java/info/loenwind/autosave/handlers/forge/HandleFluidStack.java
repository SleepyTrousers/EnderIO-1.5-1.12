package info.loenwind.autosave.handlers.forge;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public class HandleFluidStack implements IHandler<FluidStack> {

  public HandleFluidStack() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return FluidStack.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull FluidStack object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    object.writeToNBT(tag);
    nbt.setTag(name, tag);
    return false;
  }

  @Override
  public FluidStack read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nullable FluidStack object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      return FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag(name));
    }
    return null;
  }

}
