package info.loenwind.autosave;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import com.enderio.core.common.NBTAction;
import info.loenwind.autosave.engine.StorableEngine;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import com.enderio.core.common.util.NullHelper;

/**
 * Restore an object's fields from NBT data.
 *
 */
public class Reader {

  /**
   * Restore an object's fields from NBT data as if its class was annotated
   * {@link Storable} without a special handler.
   * 
   * <p>
   * See also: {@link Store} for the field annotation.
   * 
   * @param registry
   *          The {@link Registry} to look up {@link IHandler}s for the fields
   *          of the given object
   * @param phase
   *          A set of {@link NBTAction}s to indicate which fields to process.
   *          Only fields that are annotated with a matching {@link NBTAction}
   *          are restored.
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound tag, @Nonnull T object) {
    try {
      StorableEngine.read(registry, phase, tag, object);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (InstantiationException e) {
        throw new RuntimeException(e);
    } catch (NoHandlerFoundException e) {
        throw new RuntimeException(e);
    }
  }

  /**
   * Restore an object's fields from NBT data as if its class was annotated
   * {@link Storable} without a special handler using the {@link Registry}
   * {@link Registry#GLOBAL_REGISTRY GLOBAL_REGISTRY}.
   * 
   * <p>
   * See also: {@link Store} for the field annotation.
   * 
   * @param phase
   *          A set of {@link NBTAction}s to indicate which fields to process.
   *          Only fields that are annotated with a matching {@link NBTAction}
   *          are restored.
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nullable Set<NBTAction> phase, @Nullable NBTTagCompound tag, @Nonnull T object) {
    read(Registry.GLOBAL_REGISTRY, NullHelper.notnull(phase, "Missing phase"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  /**
   * Restore an object's fields from NBT data as if its class was annotated
   * {@link Storable} without a special handler.
   * 
   * <p>
   * See also: {@link Store} for the field annotation.
   * 
   * @param registry
   *          The {@link Registry} to look up {@link IHandler}s for the fields
   *          of the given object
   * @param phase
   *          A s{@link NBTAction} to indicate which fields to process. Only
   *          fields that are annotated with a matching {@link NBTAction} are
   *          restored.
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nonnull Registry registry, @Nonnull NBTAction phase, @Nullable NBTTagCompound tag, @Nonnull T object) {
    read(registry, NullHelper.notnullJ(EnumSet.of(phase), "EnumSet.of()"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  /**
   * Restore an object's fields from NBT data as if its class was annotated
   * {@link Storable} without a special handler using the {@link Registry}
   * {@link Registry#GLOBAL_REGISTRY GLOBAL_REGISTRY}.
   * 
   * <p>
   * See also: {@link Store} for the field annotation.
   * 
   * @param phase
   *          A s{@link NBTAction} to indicate which fields to process. Only
   *          fields that are annotated with a matching {@link NBTAction} are
   *          restored.
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nonnull NBTAction phase, @Nullable NBTTagCompound tag, @Nonnull T object) {
    read(Registry.GLOBAL_REGISTRY, NullHelper.notnullJ(EnumSet.of(phase), "EnumSet.of()"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  /**
   * Restore an object's fields from NBT data as if its class was annotated
   * {@link Storable} without a special handler, ignoring {@link NBTAction}
   * restrictions.
   * 
   * <p>
   * See also: {@link Store} for the field annotation.
   * 
   * @param registry
   *          The {@link Registry} to look up {@link IHandler}s for the fields
   *          of the given object
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nonnull Registry registry, @Nullable NBTTagCompound tag, @Nonnull T object) {
    read(registry, NullHelper.notnullJ(EnumSet.allOf(NBTAction.class), "EnumSet.allOf()"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  /**
   * Restore an object's fields from NBT data as if its class was annotated
   * {@link Storable} without a special handler using the {@link Registry}
   * {@link Registry#GLOBAL_REGISTRY GLOBAL_REGISTRY}, ignoring {@link NBTAction}
   * restrictions.
   * 
   * <p>
   * See also: {@link Store} for the field annotation.
   * 
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nullable NBTTagCompound tag, @Nonnull T object) {
    read(Registry.GLOBAL_REGISTRY, NullHelper.notnullJ(EnumSet.allOf(NBTAction.class), "EnumSet.allOf()"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  /**
   * Restore a single field from NBT data.
   * 
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound represents the whole object, with its fields in the tags.
   * @param fieldClass
   *          The class of the field (not the whole object)
   * @param fieldName
   *          The name of the field
   * @param object
   *          The object that should be restored. May be null if the class's handler supports creating new objects.
   * @return The restored object (which may be null), or the parameter object if nothing was to be restored.
   */
  public static <T> T readField(@Nullable NBTTagCompound tag, @Nullable Class<T> fieldClass, @Nullable String fieldName, @Nullable T object) {
    try {
      return StorableEngine.getSingleField(Registry.GLOBAL_REGISTRY, NullHelper.notnullJ(EnumSet.allOf(NBTAction.class), "EnumSet.allOf()"),
          NullHelper.notnull(tag, "Missing NBT"), NullHelper.notnull(fieldName, "Missing field name"), NullHelper.notnull(fieldClass, "Missing field class"),
          object);
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (NoHandlerFoundException e) {
      throw new RuntimeException(e);
    }
  }

}
