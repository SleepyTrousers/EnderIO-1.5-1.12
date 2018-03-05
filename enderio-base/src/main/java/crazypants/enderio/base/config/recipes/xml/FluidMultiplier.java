package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class FluidMultiplier extends Fluid {

  protected float multiplier = 1f;

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("multiplier".equals(name)) {
      try {
        this.multiplier = Float.parseFloat(value);
      } catch (NumberFormatException e) {
        throw new InvalidRecipeConfigException("Invalid value in 'multiplier': Not a number");
      }
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

}
