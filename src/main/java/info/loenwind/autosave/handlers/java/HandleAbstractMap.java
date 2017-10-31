package info.loenwind.autosave.handlers.java;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.engine.StorableEngine;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public abstract class HandleAbstractMap<K, V> implements IHandler<Map<K, V>> {

  private final IHandler<K> keyHandler;
  private final IHandler<V> valueHandler;

  protected HandleAbstractMap(IHandler<K> keyHandler, IHandler<V> valueHandler) {
    this.keyHandler = keyHandler;
    this.valueHandler = valueHandler;
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    // This handler needs to be sub-classed and annotated to be used because the Generics on the List<E> will have been deleted when canHandle() would need them
    return false;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull Map<K, V> object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    NBTTagList tag = new NBTTagList();
    for (Entry<K, V> e : object.entrySet()) {
      NBTTagCompound etag = new NBTTagCompound();
      K key = e.getKey();
      if (key != null) {
        keyHandler.store(registry, phase, etag, "key", key);
      } else {
        etag.setBoolean("key" + StorableEngine.NULL_POSTFIX, true);
      }
      V val = e.getValue();
      if (val != null) {
        valueHandler.store(registry, phase, etag, "val", val);
      } else {
        etag.setBoolean("val" + StorableEngine.NULL_POSTFIX, true);
      }
      tag.appendTag(etag);
    }
    nbt.setTag(name, tag);
    return true;
  }

  @Override
  public Map<K, V> read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable Map<K, V> object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      if (object == null) {
        object = createMap();
      } else {
        object.clear();
      }

      NBTTagList tag = nbt.getTagList(name, Constants.NBT.TAG_COMPOUND);
      for (int i = 0; i < tag.tagCount(); i++) {
        NBTTagCompound etag = tag.getCompoundTagAt(i);
        K key = etag.getBoolean("key" + StorableEngine.NULL_POSTFIX) ? null : keyHandler.read(registry, phase, etag, field, "key", null);
        V val = etag.getBoolean("val" + StorableEngine.NULL_POSTFIX) ? null : valueHandler.read(registry, phase, etag, field, "val", null);
        object.put(key, val);
      }
    }
    return object;
  }

  abstract protected @Nonnull Map<K, V> createMap();

}
