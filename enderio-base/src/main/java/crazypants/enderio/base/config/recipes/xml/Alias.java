package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Alias extends AbstractConditional {

  private String name;

  private String item;

  @Override
  public @Nonnull String getName() {
    return NullHelper.first(name, "(unnamed)");
  }

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in alias '" + item + "'");
    }
    if (isActive()) {
      Things.addAlias(name, item);
    }
    return this;
  }

  @Override
  public void register(@Nonnull String recipeName) {
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = value;
      return true;
    }
    if ("item".equals(name)) {
      this.item = value;
      return true;
    }
    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return super.setElement(factory, name, startElement);
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

}
