package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.util.EnumFacing.Plane;

public class BasinOrientation implements RecipeConfigElement {
  
  private Plane orientation;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException, XMLStreamException {
    if (orientation == null) {
      throw new InvalidRecipeConfigException("orientation must be specified");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

  @Override
  public boolean isValid() {
    return orientation == null;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      try {
        this.orientation = Plane.valueOf(value.toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException e) {
        throw new InvalidRecipeConfigException("Invalid orientation: " + value);
      }
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public Plane getOrientation() {
    return orientation;
  }
}
