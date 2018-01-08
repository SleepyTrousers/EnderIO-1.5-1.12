package info.loenwind.autosave.handlers;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;

import info.loenwind.autosave.Registry;
import info.loenwind.autosave.exceptions.NoHandlerFoundException;
import net.minecraft.nbt.NBTTagCompound;

/**
 * IHandlers convert objects into NBT and vice versa.
 * 
 * <p>
 * IHandlers can be created for a specific class/interface and registered in the
 * {@link Registry}, they can be annotated to classes to overwrite the
 * registered class handler, or they can be annotated on specific fields to be
 * used for only that field.
 * 
 * @param <T>
 *          An optional generic to have Java do the class casting of the
 *          'object' parameter.
 */
public interface IHandler<T> {

  /**
   * Checks if the handler can handle the given class. This is ony used for
   * handlers that are registered with the {@link Registry}.
   * 
   * @param clazz
   *          A class that wants to be handled
   * @return true if the handler can handle the class
   */
  boolean canHandle(Class<?> clazz);

  /**
   * Stores an object in a NBT structure.
   * 
   * @param registry
   *          The handler registry to use
   * @param phase
   *          The phase to work in. Any sub-elements that are not for this phase
   *          should be ignored
   * @param nbt
   *          The NBT to store the data
   * @param name
   *          The name of the tag in the nbt where the data should be stored. Please note that this will be overwritten by the final handler if you are
   *          returning false. Use name + "__" as a prefix in this case
   * @param object
   *          The object to store
   * @return True if the object was completely handled. False otherwise. In that
   *         case, the next matching handler will be called. Please note that it
   *         <i>is</i> ok to partially handle a object and return false. This
   *         can be used to handle inheritance
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws NoHandlerFoundException
   */
  boolean store(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nonnull String name, @Nonnull T object)
      throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException;

  /**
   * Reads an object from a NBT structure
   * 
   * FIXME: This method will not be called if the saved value was null. It *will* be called if there is no saved value, e.g. because the data is read from a
   * fresh itemstack. Creating an empty object in this case could be problematic, as the owner may not expect an empty object. This case needs to be handled
   * better.
   * 
   * @param registry
   *          The handler registry to use
   * @param phase
   *          The phase to work in. Any sub-elements that are not for this phase
   *          should be ignored
   * @param nbt
   *          The NBT to read the data from
   * @param field
   *          A reference to the field the returned object belongs to. This is purely to allow the handler to use reflection to determine further details about
   *          the data type. The handler is NOT allowed to change the field. This will be null if the handler is not running in the context of a @Storable
   *          class, e.g. when it is running on the values of a List or Map.
   * @param name
   *          The name of the tag in the nbt where the data should be read from.
   *          This tag may not exist!
   * @param object
   *          The existing object into which the data should be read, or which
   *          should be replaced by a new object with the read data. The
   *          decision which of those two possibilities to execute is open to
   *          the implementation. This may be null!
   * @return The object that should be placed into the field. This may be the
   *         changed parameter object or a new one. If this returns null, the
   *         next matching handler will be given a chance to execute. Please
   *         note that it <i>is</i> ok to change the input object in this case.
   *         This can be used to implement inheritance.
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * @throws NoHandlerFoundException
   */
  T read(@Nonnull Registry registry, @Nonnull Set<NBTAction> phase, @Nonnull NBTTagCompound nbt, @Nullable Field field, @Nonnull String name,
      @Nullable T object) throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoHandlerFoundException;
}
