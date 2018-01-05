package info.loenwind.autosave.handlers.internal;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * A dummy {@link IHandler} that is used as default value for fields. It will be
 * ignored and the fields' handlers will be looked up in the {@link Registry}
 * instead.
 * <p>
 * This is needed because Java annotations do not allow "null" as a default
 * value for class parameters.
 * <p>
 * Do not add this handler to an annotation.
 */
public class NullHandler implements IHandler<NullHandler> {

  private NullHandler() {
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return false;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull NullHandler object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    return false;
  }

  @Override
  public NullHandler read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field,
      @Nonnull String name, @Nullable NullHandler object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    return null;
  }

}
