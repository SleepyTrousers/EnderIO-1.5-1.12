package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.recipes.IRecipeGameRecipe;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public abstract class AbstractConditional implements IRecipeGameRecipe {

  private final NNList<ConditionConfig> configReferences = new NNList<>();

  private final NNList<ConditionDependency> dependencies = new NNList<>();

  protected transient boolean valid;
  protected transient boolean active;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    active = true;
      for (ConditionConfig configReference : configReferences) {
        if (!configReference.isValid()) {
          active = false;
        }
      }
      for (ConditionDependency dependency : dependencies) {
        if (!dependency.isValid()) {
          active = false;
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
      configReferences.add(factory.read(new ConditionConfig(), startElement));
      return true;
    }
    if ("dependency".equals(name)) {
      dependencies.add(factory.read(new ConditionDependency(), startElement));
      return true;
    }

    return false;
  }

  private @Nullable String source;

  @Override
  public void setSource(String source) {
    this.source = source;
  }

  @Override
  public String getSource() {
    return NullHelper.first(source, "unknown");
  }

}
