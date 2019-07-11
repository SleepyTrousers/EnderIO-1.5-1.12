package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class ConditionConfig implements IRecipeConfigElement {

  private Optional<String> section = empty();
  private Optional<String> name = empty();
  private boolean value;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (!section.isPresent()) {
      throw new InvalidRecipeConfigException("Missing section");
    }
    section = of(section.get().toLowerCase(Locale.ENGLISH));
    if (!name.isPresent()) {
      throw new InvalidRecipeConfigException("Missing name");
    }
    if (!EnderIO.getInstance().getConfiguration().hasKey(get(section), get(name))) {
      throw new InvalidRecipeConfigException("Unknown config value '" + section.get() + ":" + name.get() + "'");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return EnderIO.getInstance().getConfiguration().getCategory(get(section)).get(get(name)).getBoolean() == value;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("section".equals(name)) {
      this.section = ofString(value);
      return true;
    }
    if ("name".equals(name)) {
      this.name = ofString(value);
      return true;
    }
    if ("value".equals(name)) {
      this.value = Boolean.parseBoolean(value);
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