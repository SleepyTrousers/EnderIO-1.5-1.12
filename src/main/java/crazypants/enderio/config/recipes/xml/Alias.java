package crazypants.enderio.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.StaxFactory;

public class Alias extends AbstractConditional {

  private String name;

  private String item;

  public String getName() {
    return name;
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
  public void register() {
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
