package crazypants.enderio.gui.xml.builder;

import javax.annotation.Nonnull;

public class XMLBuilderWrapper implements IXMLBuilder {

  private final @Nonnull IXMLBuilder wrapped;

  protected XMLBuilderWrapper(@Nonnull IXMLBuilder wrapped) {
    this.wrapped = wrapped;
  }

  @Override
  public @Nonnull IXMLBuilder attribute(@Nonnull String key, @Nonnull String value, String defaultValue) {
    wrapped.attribute(key, value, defaultValue);
    return this;
  }

  @Override
  public @Nonnull IXMLBuilder comment(@Nonnull String line) {
    wrapped.comment(line);
    return this;
  }

  @Override
  public @Nonnull IXMLBuilder child(@Nonnull String name) {
    return wrapped;
  }

  @Override
  public String toString() {
    return wrapped.toString();
  }

}
