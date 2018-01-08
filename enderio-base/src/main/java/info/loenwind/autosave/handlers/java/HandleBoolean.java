package info.loenwind.autosave.handlers.java;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;

public class HandleBoolean implements IHandler<Boolean> {

  public HandleBoolean() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return Boolean.class.isAssignableFrom(clazz) || boolean.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull Boolean object) throws IllegalArgumentException, IllegalAccessException {
    nbt.setBoolean(name, object);
    return true;
  }

  @Override
  public Boolean read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable Boolean object) {
    return nbt.hasKey(name) ? nbt.getBoolean(name) : object != null ? object : false;
  }

}
