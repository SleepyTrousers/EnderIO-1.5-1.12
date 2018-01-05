package info.loenwind.autosave.exceptions;

import java.lang.reflect.Field;

public class NoHandlerFoundException extends Exception {

  private static final long serialVersionUID = -6324172401194016237L;

  public NoHandlerFoundException(Field field, Object o) {
    super("No storage handler found for field " + field.getName() + " of type " + field.getType() + " of " + o);
  }

  public NoHandlerFoundException(Class<?> clazz, String name) {
    super("No storage handler found for field " + name + " of type " + clazz);
  }

}
