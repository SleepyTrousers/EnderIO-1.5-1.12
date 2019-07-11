package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Data implements IRecipeConfigElement {

  private float value = Float.NaN;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException, XMLStreamException {
    try {
      if (Float.isNaN(value)) {
        throw new InvalidRecipeConfigException("'value' is invalid");
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <data>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

  @Override
  public boolean isValid() {
    return !Float.isNaN(value);
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("value".equals(name)) {
      this.value = Float.parseFloat(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public float getValue() {
    return value;
  }

}
