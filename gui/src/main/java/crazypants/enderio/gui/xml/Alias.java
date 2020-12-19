package crazypants.enderio.gui.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

public class Alias extends AbstractConditional {

  private @Nonnull Optional<String> name = empty();

  private @Nonnull NameField item = new NameField();

  @Override
  public @Nonnull String getName() {
    return get(name);
  }

  @Override
  public void validate() throws InvalidRecipeConfigException {
    super.validate();
    if (!name.isPresent()) {
      throw new InvalidRecipeConfigException("Missing name");
    }
    if (item.isEmpty()) {
      throw new InvalidRecipeConfigException("Missing item");
    }
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
      return true;
    }
    if ("item".equals(name)) {
      this.item.add(value);
      return true;
    }
    return super.setAttribute(factory, name, value);
  }

}
