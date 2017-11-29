package crazypants.enderio.base.config.recipes;

import java.io.ObjectStreamException;

public class InvalidRecipeConfigException extends ObjectStreamException {

  private static final long serialVersionUID = 1L;

  public InvalidRecipeConfigException(String arg0) {
    super(arg0);
  }

  public InvalidRecipeConfigException(InvalidRecipeConfigException arg0, String at) {
    super(arg0.getMessage() + " " + at);
  }

}