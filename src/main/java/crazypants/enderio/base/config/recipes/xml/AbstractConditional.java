package crazypants.enderio.base.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeGameRecipe;
import crazypants.enderio.base.config.recipes.StaxFactory;

public abstract class AbstractConditional implements RecipeGameRecipe {

  private Level level;

  private List<ConfigReference> configReferences;

  private List<Dependency> dependencies;

  protected transient boolean valid;
  protected transient boolean active;

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

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("level".equals(name)) {
      if (level == null) {
        level = factory.read(new Level(), startElement);
        return true;
      }
    }
    if ("config".equals(name)) {
      if (configReferences == null) {
        configReferences = new ArrayList<ConfigReference>();
      }
      configReferences.add(factory.read(new ConfigReference(), startElement));
      return true;
    }
    if ("dependency".equals(name)) {
      if (dependencies == null) {
        dependencies = new ArrayList<Dependency>();
      }
      dependencies.add(factory.read(new Dependency(), startElement));
      return true;
    }

    return false;
  }

}
