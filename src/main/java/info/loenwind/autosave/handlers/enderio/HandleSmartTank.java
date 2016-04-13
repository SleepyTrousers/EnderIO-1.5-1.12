package info.loenwind.autosave.handlers.enderio;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.tool.SmartTank;

public class HandleSmartTank implements IHandler<SmartTank> {

  public HandleSmartTank() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return SmartTank.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull SmartTank object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    object.writeCommon(name, nbt);
    return true;
  }

  @Override
  public SmartTank read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nullable SmartTank object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name) && object != null) {
      object.readCommon(name, nbt);
    }
    return object;
  }

}
