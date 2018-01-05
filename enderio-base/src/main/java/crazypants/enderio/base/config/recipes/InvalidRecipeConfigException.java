package crazypants.enderio.base.config.recipes;

import java.io.ObjectStreamException;

public class InvalidRecipeConfigException extends ObjectStreamException {

  private static final long serialVersionUID = 1L;

  private String filename;

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public InvalidRecipeConfigException(String arg0) {
    super(arg0);
  }

  public InvalidRecipeConfigException(InvalidRecipeConfigException arg0, String at) {
    super(arg0.getMessage() + " " + at);
  }

}