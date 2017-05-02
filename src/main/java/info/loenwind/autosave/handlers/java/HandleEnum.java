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
  public boolean store(@Nonnull Registry registry, @Nonnull Set<Store.StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull Enum<?> object)
      throws IllegalArgumentException, IllegalAccessException {
//    System.out.println("HandleEnum.store: " + object);
    nbt.setInteger(name, object.ordinal());
    return true;
  }

  @Override
  public Enum<?> read(@Nonnull Registry registry, @Nonnull Set<Store.StoreFor> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable Enum<?> object) {
    if (nbt.hasKey(name) && (object != null || field != null)) {
//      System.out.println("HandleEnum.read: ");
      Enum<?>[] enumConstants = (Enum<?>[]) (object != null ? object.getClass().getEnumConstants() : field.getType().getEnumConstants());
      Enum<?> res = enumConstants[MathHelper.clamp(nbt.getInteger(name), 0, enumConstants.length - 1)];
//      System.out.println("HandleEnum.read: " + res);
      return  res;
    } else {
      return object;
    }
  }

}
