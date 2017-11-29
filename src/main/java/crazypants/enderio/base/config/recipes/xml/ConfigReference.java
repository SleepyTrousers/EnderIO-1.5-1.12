package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class ConfigReference implements RecipeConfigElement {

  private String section;
  private String name;
  private boolean value;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (section == null) {
      throw new InvalidRecipeConfigException("Missing section");
    }
    section = section.toLowerCase(Locale.US);
    if (name == null) {
      throw new InvalidRecipeConfigException("Missing name");
    }
    if (!Config.config.hasKey(section, name)) {
      throw new InvalidRecipeConfigException("Unknown config value '" + section + ":" + name + "'");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return Config.config.getCategory(section).get(name).getBoolean() == value;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("section".equals(name)) {
      this.section = value;
      return true;
    }
    if ("name".equals(name)) {
      this.name = value;
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