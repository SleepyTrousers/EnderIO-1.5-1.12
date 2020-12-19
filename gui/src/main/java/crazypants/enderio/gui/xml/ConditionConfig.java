package crazypants.enderio.gui.xml;

import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.gamedata.ValueRepository;
import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class ConditionConfig implements IRecipeConfigElement {

  private @Nonnull Optional<String> section = empty();
  private @Nonnull Optional<String> name = empty();
  private boolean value;

  @Override
  public void validate() throws InvalidRecipeConfigException {
    if (!section.isPresent()) {
      throw new InvalidRecipeConfigException("Missing section");
    }
    section = of(section.get().toLowerCase(Locale.ENGLISH));
    if (!name.isPresent()) {
      throw new InvalidRecipeConfigException("Missing name");
    }
    if (!ValueRepository.CONFIGS.isValid(new ResourceLocation(get(section), get(name)))) {
      throw new InvalidRecipeConfigException("Unknown config value '" + section.get() + ":" + name.get() + "'");
    }
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
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
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  protected String getConfigName() {
    return get(name);
  }

  protected String getSection() {
    return get(section);
  }

  protected boolean getValue() {
    return value;
  }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    parent.child("config").attribute("section", section).attribute("name", name).attribute("value", value);
  }

}
