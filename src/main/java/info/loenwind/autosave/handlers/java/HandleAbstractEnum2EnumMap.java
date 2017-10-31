package info.loenwind.autosave.handlers.java;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import crazypants.enderio.Log;
import crazypants.enderio.machine.modes.IoMode;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * This is a specialized version of {@link HandleAbstractEnumMap}, for maps with enum values as well.
 * <p>
 * It will compress the stored data into a single long, throwing an error on construction if there are too many values to do so.
 * 
 * @author tterrag
 *
 */
public class HandleAbstractEnum2EnumMap<K extends Enum<K>, V extends Enum<V>> implements IHandler<EnumMap<K, V>> {
  
  private final Class<K> keyClass;
  private final K[] keys;
  
  private final V[] vals;
  private final int valspace;

  public HandleAbstractEnum2EnumMap(Class<K> keyClass, Class<V> valClass) {
    this.keyClass = keyClass;
    this.keys = keyClass.getEnumConstants();
    this.vals = valClass.getEnumConstants();
    // Add one to vals.length for null
    this.valspace= Integer.numberOfTrailingZeros(Integer.highestOneBit(vals.length + 1)) + 1;
    
    if (keys.length * valspace > 64) {
      throw new IllegalArgumentException("Enums " + keyClass + " and " + valClass + " cannot be used, as they have too many combinations.");
    }
  }

  @Override
  public boolean canHandle(Class<?> clazz) {
    return false;
  }

  @Override
  public boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name,
      @Nonnull EnumMap<K, V> object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    long value = 0;
    for (K key : keys) {
      // 0 is null, all ordinal values are shifted up by 1
      long subvalue = 0;
      if (object.containsKey(key)) {
        subvalue = object.get(key).ordinal() + 1;
      }
      value = value | (subvalue << (key.ordinal() * valspace));
    }
    nbt.setIntArray(name, new int[] {valspace, (int) (value >>> 32), (int) (value & 0xFFFFFFFF)});
    return true;
  }

  @Override
  public EnumMap<K, V> read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field,
      @Nonnull String name, @Nullable EnumMap<K, V> object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException {
    if (nbt.hasKey(name)) {
      if (object == null) {
        object = new EnumMap<K, V>(keyClass);
      }
      int[] raw = nbt.getIntArray(name);
      if (raw.length == 0) {
        // Convert old data
        long value = nbt.getLong(name);
        for (K key : keys) {
          long subvalue = (value >>> (key.ordinal() * 8)) & 0xFF;
          if (subvalue > 0 && subvalue < IoMode.values().length) {
            object.put(key, vals[(int) subvalue]);
          } else {
            object.remove(key);
          }
        }
        return object;
      }
      int space = raw[0];
      int mask = (1 << space) - 1;
      long value = ((long) raw[1] << 32) | raw[2];
      for (K key : keys) {
        long subvalue = (value >>> (key.ordinal() * space)) & mask;
        if (subvalue > 0 && subvalue <= vals.length) {
          object.put(key, vals[(int) subvalue - 1]);
        } else if (subvalue == 0) {
          object.remove(key);
        } else {
          Log.error("Found invalid map value when parsing enum2enum map! Data: %s   Field: %s", nbt, field);
          Thread.dumpStack();
        }
      }
    }
    return object;
  }

}
