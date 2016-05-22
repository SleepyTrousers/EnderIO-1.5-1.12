package crazypants.enderio.config.recipes.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import crazypants.enderio.config.Config;

public class Level implements RecipeConfigElement {

  @XStreamAsAttribute
  @XStreamAlias("minlevel")
  private Integer minlevel;

  @XStreamAsAttribute
  @XStreamAlias("maxlevel")
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

}