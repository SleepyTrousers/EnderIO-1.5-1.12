package info.loenwind.autosave.handlers.java;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.util.NullHelper;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.engine.StorableEngine;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;

public class HandleAbstractEnumMap<K extends Enum<K>, V> implements IHandler<EnumMap<K, V>> {

  private final Class<K> enumClass;
  private final K[] enumValues;
  private final IHandler<V> valueHandler;
  
  protected HandleAbstractEnumMap(Class<K> enumClass, IHandler<V> valueHandler) {
    this.enumClass = enumClass;
    this.enumValues = enumClass.getEnumConstants();
    this.valueHandler = valueHandler;
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    // This handler needs to be sub-classed and annotated to be used because the Generics on the List<E> will have been deleted when canHandle() would need them
    return false;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull EnumMap<K, V> object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagCompound tag = new NBTTagCompound();
    for (K key : enumValues) {
      V val = object.get(key);
      String keystr = NullHelper.notnullJ(Integer.toString(key.ordinal()), "Integer.toString is null");
      if (val != null) {
        valueHandler.store(registry, phase, tag, keystr, val);
      } else {
        tag.setBoolean(keystr + StorableEngine.NULL_POSTFIX, true);
      }
    }
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public EnumMap<K, V> read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field,
      @Nonnull String name, @Nullable EnumMap<K, V> object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      if (object == null) {
        object = makeMap();
      }
      NBTTagCompound tag = nbt.getCompoundTag(name);
      for (K key : enumValues) {
        String keystr = NullHelper.notnullJ(Integer.toString(key.ordinal()), "Integer.toString is null");
        if (!tag.getBoolean(keystr + StorableEngine.NULL_POSTFIX)) {
          object.put(key, valueHandler.read(registry, phase, tag, null, keystr, null));
        }
      }
    }
    return object;
  }
  
  @Nonnull
  protected EnumMap<K, V> makeMap() {
    return new EnumMap<>(enumClass);
  }

}
