package info.loenwind.autosave.handlers.java;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class HandleEnum implements IHandler<Enum<?>> {

  public HandleEnum() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return Enum.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull Enum<?> object) throws IllegalArgumentException, IllegalAccessException {
    nbt.setInteger(name, object.ordinal());
    return true;
  }

  @Override
  public Enum<?> read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable Enum<?> object) {
    if (nbt.hasKey(name)) {
      Enum<?>[] enumConstants = null;
      if (object != null) {
        enumConstants = object.getClass().getEnumConstants();
      } else if (field != null) {
        enumConstants = (Enum<?>[]) field.getType().getEnumConstants();
      }
      if (enumConstants != null) {
        return enumConstants[MathHelper.clamp(nbt.getInteger(name), 0, enumConstants.length - 1)];
      }
    }
    return object;
  }

}
