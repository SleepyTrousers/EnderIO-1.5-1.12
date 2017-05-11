package info.loenwind.autosave.handlers.forge;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class HandleFluid implements IHandler<Fluid> {

  public HandleFluid() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return Fluid.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull Fluid object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    nbt.setString(name, NullHelper.notnullF(FluidRegistry.getFluidName(object), "Fluid " + object + " is missing from fluid registry"));
    return true;
  }

  @Override
  public Fluid read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable Fluid object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    return nbt.hasKey(name) ? FluidRegistry.getFluid(nbt.getString(name)) : object;
  }

}
