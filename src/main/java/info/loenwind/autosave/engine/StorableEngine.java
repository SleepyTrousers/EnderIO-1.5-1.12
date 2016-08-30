package info.loenwind.autosave.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Registry;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.internal.HandleStorable;
import info.loenwind.autosave.handlers.internal.NullHandler;
import net.minecraft.nbt.NBTTagCompound;

/**
 * The thread-safe engine that handles (re-)storing {@link Storable} objects by
 * storing their fields. The fields to (re-)store must be annotated
 * {@link Store}.
 * <p>
 * It will also process the annotated fields of superclasses, as long as there
 * is an unbroken chain of {@link Storable} annotations (without special
 * handlers). Fields that have the same name as a field in a sub-/super-class
 * will be processed independently.
 * <p>
 * If the final superclass has an {@link IHandler} registered in the
 * {@link Registry}, it will also be processed. However, this will <i>not</i>
 * work for handlers that return a new object instead of changing the given one.
 * A handler can check for this case by seeing if its "name" parameter is
 * {@link StorableEngine#SUPERCLASS_KEY}.
 * <p>
 * Note: If a {@link Storable} object is encountered in a {@link Store} field,
 * it is handled by {@link HandleStorable}---which delegates here.
 * <p>
 * Note 2: There are public entrances to this class in {@link Writer} and
 * {@link Reader}.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class StorableEngine {

  private static final ThreadLocal<StorableEngine> INSTANCE = new ThreadLocal<StorableEngine>() {
    @Override
    protected StorableEngine initialValue() {
      return new StorableEngine();
    }
  };

  public static final @Nonnull String NULL_POSTFIX = "__null";
  public static final @Nonnull String SUPERCLASS_KEY = "__superclass";
  private final @Nonnull Map<Class<?>, List<Field>> fieldCache = new HashMap<Class<?>, List<Field>>();
  private final @Nonnull Map<Field, Set<StoreFor>> phaseCache = new HashMap<Field, Set<StoreFor>>();
  private final @Nonnull Map<Field, List<IHandler>> fieldHandlerCache = new HashMap<Field, List<IHandler>>();
  private final @Nonnull Map<Class<?>, Class<?>> superclassCache = new HashMap<Class<?>, Class<?>>();
  private final @Nonnull Map<Class<?>, List<IHandler>> superclassHandlerCache = new HashMap<Class<?>, List<IHandler>>();

  private StorableEngine() {
  }

  public static <T> void read(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull T object)
      throws IllegalAccessException, InstantiationException, NoHandlerFoundException {
    INSTANCE.get().read_impl(registry, phase, tag, object);
  }

  public static <T> void store(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull T object)
      throws IllegalAccessException, InstantiationException, NoHandlerFoundException {
    INSTANCE.get().store_impl(registry, phase, tag, object);
  }

  public <T> void read_impl(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull T object)
      throws IllegalAccessException, InstantiationException, NoHandlerFoundException {
    Class<? extends Object> clazz = object.getClass();
    if (!fieldCache.containsKey(clazz)) {
      cacheHandlers(registry, clazz);
    }

    for (Field field : fieldCache.get(clazz)) {
      if (!Collections.disjoint(phaseCache.get(field), phase)) {
        Object fieldData = field.get(object);
        String fieldName = field.getName();
        if (!tag.hasKey(fieldName + NULL_POSTFIX) && fieldName != null) {
          for (IHandler handler : fieldHandlerCache.get(field)) {
            Object result = handler.read(registry, phase, tag, field, fieldName, fieldData);
            if (result != null) {
              field.set(object, result);
              break;
            }
          }
        } else {
          field.set(object, null);
        }
      }
    }

    Class<?> superclazz = superclassCache.get(clazz);
    if (superclazz != null) {
      for (IHandler handler : superclassHandlerCache.get(superclazz)) {
        if (handler.read(registry, phase, tag, null, SUPERCLASS_KEY, object) != null) {
          break;
        }
      }
    }

  }

  public <T> void store_impl(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull T object)
      throws IllegalAccessException, InstantiationException, NoHandlerFoundException {
    Class<? extends Object> clazz = object.getClass();
    if (!fieldCache.containsKey(clazz)) {
      cacheHandlers(registry, clazz);
    }

    for (Field field : fieldCache.get(clazz)) {
      if (!Collections.disjoint(phaseCache.get(field), phase)) {
        Object fieldData = field.get(object);
        String fieldName = field.getName();
        if (fieldData != null && fieldName != null) {
          for (IHandler handler : fieldHandlerCache.get(field)) {
            if (handler.store(registry, phase, tag, fieldName, fieldData)) {
              break;
            }
          }
        } else {
          tag.setBoolean(fieldName + NULL_POSTFIX, true);
        }
      }
    }

    Class<?> superclazz = superclassCache.get(clazz);
    if (superclazz != null) {
      for (IHandler handler : superclassHandlerCache.get(superclazz)) {
        if (handler.store(registry, phase, tag, SUPERCLASS_KEY, object)) {
          break;
        }
      }
    }
  }

  public static <T> T getSingleField(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull String fieldName,
      @Nonnull Class<T> clazz, @Nullable T object) throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoHandlerFoundException {
    if (!tag.hasKey(fieldName + NULL_POSTFIX)) {
      for (IHandler<T> handler : registry.findHandlers(clazz)) {
        T result = handler.read(registry, phase, tag, null, fieldName, object);
        if (result != null) {
          return result;
        }
      }
    }
    return null;
  }

  public static <T> void setSingleField(@Nonnull Registry registry, @Nonnull Set<StoreFor> phase, @Nonnull NBTTagCompound tag, @Nonnull String fieldName,
      @Nonnull Class<T> clazz, @Nullable T fieldData) throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoHandlerFoundException {
    if (fieldData != null) {
      tag.removeTag(fieldName + NULL_POSTFIX);
      for (IHandler<T> handler : registry.findHandlers(clazz)) {
        if (handler.store(registry, phase, tag, fieldName, fieldData)) {
          return;
        }
      }
      throw new NoHandlerFoundException(clazz, fieldName);
    } else {
      tag.removeTag(fieldName);
      tag.setBoolean(fieldName + NULL_POSTFIX, true);
      return;
    }
  }

  private void cacheHandlers(@Nonnull Registry registry, Class<?> clazz) throws IllegalAccessException, InstantiationException, NoHandlerFoundException {
    final ArrayList<Field> fieldList = new ArrayList<Field>();
    for (Field field : clazz.getDeclaredFields()) {
      Store annotation = field.getAnnotation(Store.class);
      if (annotation != null) {
        ArrayList<IHandler> handlerList = new ArrayList<IHandler>();
        String fieldName = field.getName();
        if (fieldName != null) {
          Class<?> fieldType = field.getType();
          if (annotation.handler() != NullHandler.class) {
            handlerList.add(annotation.handler().newInstance());
          }
          handlerList.addAll(registry.findHandlers(fieldType));
          if (handlerList.isEmpty()) {
            throw new NoHandlerFoundException(field, clazz);
          }
          EnumSet<StoreFor> enumSet = EnumSet.noneOf(StoreFor.class);
          enumSet.addAll(Arrays.asList(annotation.value()));
          phaseCache.put(field, enumSet);
          field.setAccessible(true);
          fieldList.add(field);
          fieldHandlerCache.put(field, handlerList);
        }
      }
    }

    Class<?> superclazz = clazz.getSuperclass();
    if (superclazz != null) {
      Storable annotation = superclazz.getAnnotation(Storable.class);
      if (annotation != null) {
        if (annotation.handler() == HandleStorable.class) {
          cacheHandlers(registry, superclazz);
          fieldList.addAll(fieldCache.get(superclazz));
        } else {
          superclassCache.put(clazz, superclazz);
          if (!superclassCache.containsKey(superclazz)) {
            superclassHandlerCache.put(superclazz, (List<IHandler>) Arrays.asList(annotation.handler().newInstance()));
          }
        }
      } else {
        List<IHandler> handlers = registry.findHandlers(superclazz);
        if (!handlers.isEmpty()) {
          superclassCache.put(clazz, superclazz);
          if (!superclassCache.containsKey(superclazz)) {
            superclassHandlerCache.put(superclazz, handlers);
          }
        }
      }
    }

    fieldCache.put(clazz, fieldList);
  }

}
