package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Level implements RecipeConfigElement {

  private Integer minlevel;

  private Integer maxlevel;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (minlevel == null) {
      minlevel = 0;
    } else if (minlevel < 0) {
      throw new InvalidRecipeConfigException("Invalid negative 'minlevel' in <level>");
    }
    if (maxlevel == null) {
      maxlevel = Integer.MAX_VALUE;
    } else if (maxlevel < 0) {
      throw new InvalidRecipeConfigException("Invalid negative 'maxlevel' in <level>");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return Config.recipeLevel >= minlevel && Config.recipeLevel <= maxlevel;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("minlevel".equals(name)) {
      this.minlevel = Integer.valueOf(value);
      return true;
    }
    if ("maxlevel".equals(name)) {
      this.maxlevel = Integer.valueOf(value);
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