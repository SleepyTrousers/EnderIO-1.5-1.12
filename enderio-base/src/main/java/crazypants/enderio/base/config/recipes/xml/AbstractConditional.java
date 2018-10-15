package crazypants.enderio.base.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeGameRecipe;
import crazypants.enderio.base.config.recipes.StaxFactory;

public abstract class AbstractConditional implements RecipeGameRecipe {

  private List<ConditionConfig> configReferences;

  private List<ConditionDependency> dependencies;

  protected transient boolean valid;
  protected transient boolean active;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    active = true;
    if (configReferences != null) {
      for (ConditionConfig configReference : configReferences) {
        if (!configReference.isValid()) {
          active = false;
        }
      }
    }
    if (dependencies != null) {
      for (ConditionDependency dependency : dependencies) {
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

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("config".equals(name)) {
      if (configReferences == null) {
        configReferences = new ArrayList<ConditionConfig>();
      }
      configReferences.add(factory.read(new ConditionConfig(), startElement));
      return true;
    }
    if ("dependency".equals(name)) {
      if (dependencies == null) {
        dependencies = new ArrayList<ConditionDependency>();
      }
      dependencies.add(factory.read(new ConditionDependency(), startElement));
      return true;
    }

    return false;
  }

  private String source;

  @Override
  public void setSource(String source) {
    this.source = source;
  }

  @Override
  public String getSource() {
    return source;
  }

}
