package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.util.Prep;

public class ConditionDependency implements RecipeConfigElement {

  private String itemString;
  private boolean reverse;

  private transient ItemOptional item;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      if (itemString == null || itemString.trim().isEmpty()) {
        throw new InvalidRecipeConfigException("Missing item");
      }
      item = new ItemOptional();
      item.setName(itemString);
      item.readResolve();
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <dependency>");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return (item.isValid() && Prep.isValid(item.getItemStack())) != reverse;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("item".equals(name)) {
      this.itemString = value;
      return true;
    }
    if ("reverse".equals(name)) {
      this.reverse = Boolean.parseBoolean(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

}