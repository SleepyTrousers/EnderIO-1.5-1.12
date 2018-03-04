package crazypants.enderio.base.filter;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;

public class FilterHandler implements IHandler<IItemFilter> {

  static {
    Registry.GLOBAL_REGISTRY.register(new FilterHandler());
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return IItemFilter.class.isAssignableFrom(clazz);
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull IItemFilter object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound root = new NBTTagCompound();
    FilterRegistry.writeFilterToNbt(object, root);
    nbt.setTag(name, root);
    return true;
  }

  @Override
  public IItemFilter read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable IItemFilter object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (object == null) {
      // Note: This will be called with no nbt when a fresh itemstack is placed---output should be null!
      if (nbt.hasKey(name)) {
        object = FilterRegistry.loadFilterFromNbt(nbt.getCompoundTag(name));
      } else {
        return object;
      }
    }
    object.readFromNBT(nbt.getCompoundTag(name));
    return object;
  }

}
