package info.loenwind.autosave.annotations;

import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.internal.NullHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be stored in NBT.
 *
 * <p>
 * Parameters:
 * 
 * <ul>
 * <li>value: An array of {@link StoreFor} keys to designate for which targets
 * the data should be stored.
 * <li>handler: A class implementing {@link IHandler} to use for this field
 * instead of the registered handler.
 * </ul>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Store {

  // Note: @Inherit does not work on fields. HandleStorable has special code to handle that.

  /**
   * Designates the targets NBT can be stored for.
   * <ul>
   * <li>{@link #SAVE}</li>
   * <li>{@link #CLIENT}</li>
   * <li>{@link #ITEM}</li>
   * </ul>
   *
   */
  public enum StoreFor {
    /**
     * Store in the world save
     */
    SAVE,
    /**
     * Send to the client on status updates
     */
    CLIENT,
    /**
     * Store in the item when the block is broken
     */
    ITEM;
  }

  public StoreFor[] value() default { StoreFor.SAVE, StoreFor.CLIENT, StoreFor.ITEM };

  @SuppressWarnings("rawtypes")
  public Class<? extends IHandler> handler() default NullHandler.class;
}
