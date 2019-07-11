package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.capacitor.ScalerFactory;
import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Scaler implements IRecipeConfigElement {

  private Optional<String> name = empty();

  protected Optional<crazypants.enderio.api.capacitor.Scaler> scaler = empty();

  public Scaler() {
  }

  public Scaler(String name) {
    this.name = ofString(name);
  }

  @Override
  public Scaler readResolve() throws InvalidRecipeConfigException, XMLStreamException {
    try {
      if (!name.isPresent()) {
        throw new InvalidRecipeConfigException("name is missing");
      }
      scaler = ofNullable(ScalerFactory.fromString(get(name)));

      if (!scaler.isPresent()) {
        throw new InvalidRecipeConfigException("'name' '" + name.get() + "' is invalid");
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
    return scaler.isPresent();
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public crazypants.enderio.api.capacitor.Scaler getScaler() {
    return get(scaler);
  }

  public String getScalerString() {
    return get(name);
  }

}
