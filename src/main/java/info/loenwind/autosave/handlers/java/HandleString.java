package info.loenwind.autosave.handlers.java;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.IHandler;

import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;

public class HandleString implements IHandler<String> {

  public HandleString() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return String.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<Store.StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull String object)
      throws IllegalArgumentException, IllegalAccessException {
    nbt.setString(name, object);
    return true;
  }

  @Override
  public String read(@Nonnull Registry registry, @Nonnull Set<Store.StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nullable String object) {
    return nbt.hasKey(name) ? nbt.getString(name) : object;
  }

}
