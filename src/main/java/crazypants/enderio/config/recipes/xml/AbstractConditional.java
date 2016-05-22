package crazypants.enderio.config.recipes.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public abstract class AbstractConditional implements RecipeGameRecipe {

  @XStreamAsAttribute
  @XStreamAlias("level")
  private Level level;

  @XStreamImplicit(itemFieldName = "config")
  private List<ConfigReference> configReferences;

  @XStreamImplicit(itemFieldName = "dependency")
  private List<Dependency> dependencies;

  @XStreamOmitField
  protected boolean valid;
  @XStreamOmitField
  protected boolean active;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    active = true;
    if (configReferences != null) {
      for (ConfigReference configReference : configReferences) {
        if (!configReference.isValid()) {
          active = false;
        }
      }
    }
    if (level != null) {
      if (!level.isValid()) {
        active = false;
      }
    }
    if (dependencies != null) {
      for (Dependency dependency : dependencies) {
        if (!dependency.isValid()) {
          active = false;
        }
      }
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  @Override
  public boolean isActive() {
    return active;
  }

}
