package crazypants.enderio.base.config.recipes;

import java.io.ObjectStreamException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InvalidRecipeConfigException extends ObjectStreamException {

  private static final long serialVersionUID = 1L;

  private String filename;

  public @Nullable String getFilename() {
    return filename;
  }

  public void setFilename(@Nullable String filename) {
    this.filename = filename;
  }

  public InvalidRecipeConfigException(@Nonnull String arg0) {
    super(arg0);
  }

  public InvalidRecipeConfigException(@Nonnull InvalidRecipeConfigException arg0, @Nonnull String at) {
    super(arg0.getMessage() + " " + at);
  }

}