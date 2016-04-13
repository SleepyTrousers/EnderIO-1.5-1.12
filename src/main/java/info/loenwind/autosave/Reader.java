package info.loenwind.autosave;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.engine.StorableEngine;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.util.NullHelper;

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
   *          A set of {@link StoreFor}s to indicate which fields to process.
   *          Only fields that are annotated with a matching {@link StoreFor}
   *          are restored.
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull T object) {
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
   *          A set of {@link StoreFor}s to indicate which fields to process.
   *          Only fields that are annotated with a matching {@link StoreFor}
   *          are restored.
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nullable Set<Store.StoreFor> phase, @Nullable NBTTagCompound tag, @Nonnull T object) {
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
   *          A s{@link StoreFor} to indicate which fields to process. Only
   *          fields that are annotated with a matching {@link StoreFor} are
   *          restored.
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nonnull Registry registry, @Nonnull StoreFor phase, @Nullable NBTTagCompound tag, @Nonnull T object) {
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
   *          A s{@link StoreFor} to indicate which fields to process. Only
   *          fields that are annotated with a matching {@link StoreFor} are
   *          restored.
   * @param tag
   *          A {@link NBTTagCompound} to read from. This NBTTagCompound
   *          represents the whole object, with its fields in the tags.
   * @param object
   *          The object that should be restored
   */
  public static <T> void read(@Nonnull StoreFor phase, @Nullable NBTTagCompound tag, @Nonnull T object) {
    read(Registry.GLOBAL_REGISTRY, NullHelper.notnullJ(EnumSet.of(phase), "EnumSet.of()"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  /**
   * Restore an object's fields from NBT data as if its class was annotated
   * {@link Storable} without a special handler, ignoring {@link StoreFor}
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
    read(registry, NullHelper.notnullJ(EnumSet.allOf(StoreFor.class), "EnumSet.allOf()"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

  /**
   * Restore an object's fields from NBT data as if its class was annotated
   * {@link Storable} without a special handler using the {@link Registry}
   * {@link Registry#GLOBAL_REGISTRY GLOBAL_REGISTRY}, ignoring {@link StoreFor}
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
    read(Registry.GLOBAL_REGISTRY, NullHelper.notnullJ(EnumSet.allOf(StoreFor.class), "EnumSet.allOf()"), NullHelper.notnull(tag, "Missing NBT"), object);
  }

}
