package info.loenwind.autosave.handlers.java;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;

public abstract class HandleAbstractCollection<E, C extends Collection<E>> implements IHandler<C> {

  private final IHandler<E> elemHandler;

  protected HandleAbstractCollection(IHandler<E> elemHandler) {
    this.elemHandler = elemHandler;
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    // This handler needs to be sub-classed and annotated to be used because the Generics on the List<E> will have been deleted when canHandle() would need them
    return false;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull C object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setInteger("size", object.size());
    int i = 0;
    for (E elem : object) {
      if (elem != null) {
        elemHandler.store(registry, phase, tag, i + "", elem);
      }
      i++;
    }
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public C read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable C object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      if (object == null) {
        object = makeCollection();
      } else {
        object.clear();
      }

      NBTTagCompound tag = nbt.getCompoundTag(name);
      int size = tag.getInteger("size");
      for (int i = 0; i < size; i++) {
        if (tag.hasKey(i + "")) {
          object.add(elemHandler.read(registry, phase, tag, null, i + "", makeEmptyValueObject()));
        } else {
          object.add(null);
        }
      }
    }
    return object;
  }

  abstract protected @Nonnull C makeCollection();

  protected @Nullable E makeEmptyValueObject() {
    return null;
  }

}
