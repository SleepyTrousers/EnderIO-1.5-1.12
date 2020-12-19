package crazypants.enderio.gui.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.gamedata.ValueRepository;
import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class Capacitor extends AbstractConditional {

  private @Nonnull Optional<ResourceLocation> key = empty();

  private boolean required;

  private boolean disabled;

  private int base = Integer.MIN_VALUE;

  private @Nonnull Optional<Scaler> scaler = empty();

  @Override
  public void validate() throws InvalidRecipeConfigException {
    super.validate();
    if (!disabled) {
      if (!scaler.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <scaler> or <indexed>");
      }
      if (base == Integer.MIN_VALUE) {
        throw new InvalidRecipeConfigException("'base' is invalid");
      }
      if (key.isPresent()) {
        if (!ValueRepository.CAP_KEYS.isValid(get(key))) {
          throw new InvalidRecipeConfigException("'key' is invalid");
        }
      } else {
        throw new InvalidRecipeConfigException("'key' is missing");
      }
    }
  }

  @Override
  public @Nonnull String getName() {
    if (key.isPresent()) {
      return key.get().toString();
    }
    return "unnamed recipe";
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("key".equals(name)) {
      this.key = of(new ResourceLocation(value));
      return true;
    }
    if ("required".equals(name)) {
      this.required = Boolean.parseBoolean(value);
      return true;
    }
    if ("disabled".equals(name)) {
      this.disabled = Boolean.parseBoolean(value);
      return true;
    }
    if ("base".equals(name)) {
      this.base = Integer.parseInt(value);
      return true;
    }
    if ("scaler".equals(name) && !scaler.isPresent()) {
      scaler = of(new Scaler(value).readResolve());
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("scaler".equals(name) && !scaler.isPresent()) {
      scaler = of(factory.read(new Scaler(), startElement));
      return true;
    }
    if ("indexed".equals(name) && !scaler.isPresent()) {
      scaler = of(factory.read(new IndexedScaler(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<capacitor key='");
    builder.append(key);
    builder.append("' required='");
    builder.append(required);
    builder.append("' disabled='");
    builder.append(disabled);
    builder.append("' base='");
    builder.append(base);
    builder.append("'");
    builder.append(">");
    if (scaler.isPresent()) {
      builder.append(get(scaler));
    }
    builder.append("</capacitor>");
    return builder.toString();
  }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    IXMLBuilder ƒ = parent.child("capacitor").superCall(super::write).attribute("key", key).attribute("required", required).attribute("disabled", disabled)
        .attribute("base", base);
    super.write(ƒ);
    if (scaler.isPresent()) {
      get(scaler).write(ƒ);
    }
  }

  @Override
  @Nonnull
  public ElementList getSubElements() {
    return super.getSubElements().add(scaler);
  }

  public @Nonnull Optional<ResourceLocation> getKey() {
    return key;
  }

  public void setKey(@Nonnull Optional<ResourceLocation> key) {
    this.key = key;
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

  public int getBase() {
    return base;
  }

  public void setBase(int base) {
    this.base = base;
  }

  public @Nonnull Optional<Scaler> getScaler() {
    return scaler;
  }

  public void setScaler(@Nonnull Optional<Scaler> scaler) {
    this.scaler = scaler;
  }

}
