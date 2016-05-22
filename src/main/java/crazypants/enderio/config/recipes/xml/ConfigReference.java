package crazypants.enderio.config.recipes.xml;

import java.util.Locale;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import crazypants.enderio.config.Config;

public class ConfigReference implements RecipeConfigElement {

  @XStreamAsAttribute
  private String section;
  @XStreamAsAttribute
  private String name;
  @XStreamAsAttribute
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

}