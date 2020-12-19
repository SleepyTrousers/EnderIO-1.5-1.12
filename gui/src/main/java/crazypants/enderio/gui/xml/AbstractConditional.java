package crazypants.enderio.gui.xml;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public abstract class AbstractConditional implements IRecipeConfigElement {

  private final List<ConditionConfig> configReferences = new ArrayList<>();

  private final List<ConditionDependency> dependencies = new ArrayList<>();

  @Override
  public void validate() throws InvalidRecipeConfigException {
  }

  @Override
  @Nonnull
  public ElementList getSubElements() {
    return IRecipeConfigElement.super.getSubElements().add(configReferences, dependencies);
  }

  protected boolean isConditional() {
    return !configReferences.isEmpty() || !dependencies.isEmpty();
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  @Override
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
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

  protected boolean required;

  protected boolean disabled;

  @Override
  public void setSource(@Nonnull String source) {
    this.source = source;
  }

  @Override
  public @Nonnull String getSource() {
    return source != null ? source : "unknown";
  }

  protected List<ConditionConfig> getConfigReferences() {
    return configReferences;
  }

  protected List<ConditionDependency> getDependencies() {
    return dependencies;
  }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    IXMLBuilder ƒ = parent.child("!--");
    configReferences.forEach(c -> c.write(ƒ));
    dependencies.forEach(c -> c.write(ƒ));
  }

  public boolean isRequired() {
    return required;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public void setDisabled(boolean disabled) {
    this.disabled = disabled;
  }
}
