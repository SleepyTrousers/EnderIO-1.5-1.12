package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.capacitor.ScalerFactory;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Scaler implements RecipeConfigElement {

  private String name;

  protected crazypants.enderio.api.capacitor.Scaler scaler;

  public Scaler() {
  }

  public Scaler(String name) {
    this.name = name;
  }

  @Override
  public Scaler readResolve() throws InvalidRecipeConfigException, XMLStreamException {
    try {
      scaler = ScalerFactory.fromString(name);

      if (scaler == null) {
        throw new InvalidRecipeConfigException("'name' '" + name + "' is invalid");
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <scaler>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

  @Override
  public boolean isValid() {
    return scaler != null;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = value;
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public crazypants.enderio.api.capacitor.Scaler getScaler() {
    return scaler;
  }

}
