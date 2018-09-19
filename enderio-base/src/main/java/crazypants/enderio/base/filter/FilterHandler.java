package crazypants.enderio.base.filter;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.nbt.NBTTagCompound;

public class FilterHandler implements IHandler<IFilter> {

  static {
    Registry.GLOBAL_REGISTRY.register(new FilterHandler());
  }

  @Override
  public Class<?> getRootType() {
    return IFilter.class;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull IFilter object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound root = new NBTTagCompound();
    FilterRegistry.writeFilterToNbt(object, root);
    nbt.setTag(name, root);
    return true;
  }

  @Override
  public IFilter read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable IFilter object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (object == null && !nbt.hasKey(name)) {
      // Note: This will be called with no nbt when a fresh itemstack is placed---output should be null!
      return object;
    }
    object = FilterRegistry.loadFilterFromNbt(nbt.getCompoundTag(name));
    return object;
  }

}
