package info.loenwind.autosave.annotations;

import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.handlers.IHandler;
import info.loenwind.autosave.handlers.internal.HandleStorable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class to be stored in NBT. If a special handler is given, it will be
 * used when objects of this class are to be (re-)stored to/from NBT as field of
 * another class. Otherwise {@link HandleStorable} will handle this class by
 * (re-)storing those of its fields that are annotated {@link Store}.
 * 
 * <p>
 * Please note that while an object's direct class does not need to be annotated
 * for the object to be processed by {@link Reader} or {@link Writer}, you
 * should nevertheless do so to enable subclassing it to work.
 *
 * <p>
 * Parameters:
 * 
 * <ul>
 * <li>value: A class implementing {@link IHandler} to use for this class
 * instead of {@link HandleStorable}.
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Storable {

  @SuppressWarnings("rawtypes")
  public Class<? extends IHandler> handler() default HandleStorable.class;

}
