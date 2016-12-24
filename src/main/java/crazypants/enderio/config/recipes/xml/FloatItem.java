package crazypants.enderio.config.recipes.xml;

import javax.xml.stream.XMLStreamException;

import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.StaxFactory;

public class FloatItem extends Item {

  protected float amount = 1f;

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("amount".equals(name)) {
      try {
        this.amount = Float.parseFloat(value);
      } catch (NumberFormatException e) {
        throw new InvalidRecipeConfigException("Invalid value in 'amount': Not a number");
      }
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

}
