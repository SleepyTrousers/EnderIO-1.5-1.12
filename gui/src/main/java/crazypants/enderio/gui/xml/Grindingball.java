package crazypants.enderio.gui.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class Grindingball extends AbstractConditional {

  private @Nonnull Optional<String> name = empty();

  private boolean required;

  private boolean disabled;

  private @Nonnull Optional<Item> item = empty();

  private float grinding = 1f, chance = 1f, power = 1f;

  private int durability = 0;

  @Override
  public void validate() throws InvalidRecipeConfigException {
    super.validate();
    if (!disabled) {
      if (!item.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <item>");
      }
      if (durability <= 0) {
        throw new InvalidRecipeConfigException("'durability' is invalid'");
      }
      if (grinding <= 0 || grinding > 5f) {
        throw new InvalidRecipeConfigException("'grinding' is invalid'");
      }
      if (chance <= 0 || chance > 5f) {
        throw new InvalidRecipeConfigException("'chance' is invalid'");
      }
      if (power <= 0 || power > 5f) {
        throw new InvalidRecipeConfigException("'power' is invalid'");
      }

    }
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getName() {
    return name.orElse("unnamed recipe");
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
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
    if ("grinding".equals(name)) {
      this.grinding = Float.parseFloat(value);
      return true;
    }
    if ("chance".equals(name)) {
      this.chance = Float.parseFloat(value);
      return true;
    }
    if ("power".equals(name)) {
      this.power = Float.parseFloat(value);
      return true;
    }
    if ("durability".equals(name)) {
      this.durability = Integer.parseInt(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    if ("item".equals(name) && !item.isPresent()) {
      item = of(factory.read(new Item(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

  // @Override
  // public String toString() {
  // StringBuilder builder = new StringBuilder();
  // builder.append("<grindingball name='");
  // builder.append(get(name));
  // builder.append("' required='");
  // builder.append(required);
  // builder.append("' disabled='");
  // builder.append(disabled);
  // builder.append("' grinding='");
  // builder.append(grinding);
  // builder.append("' chance='");
  // builder.append(chance);
  // builder.append("' power='");
  // builder.append(power);
  // builder.append("' durability='");
  // builder.append(durability);
  // builder.append("'>");
  // builder.append(get(item));
  // builder.append("</grindingball>");
  // return builder.toString();
  // }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    IXMLBuilder ƒ = parent.child("grindingball").superCall(super::write).attribute("name", name).attribute("required", required).attribute("disabled", disabled)
        .attribute("grinding", grinding).attribute("chance", chance).attribute("power", power).attribute("durability", durability);
    super.write(ƒ);
    if (item.isPresent()) {
      get(item).write(ƒ);
    }
  }

  @Override
  @Nonnull
  public ElementList getSubElements() {
    return super.getSubElements().add(item);
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

  public @Nonnull Optional<Item> getItem() {
    return item;
  }

  public void setItem(@Nonnull Optional<Item> item) {
    this.item = item;
  }

  public float getGrinding() {
    return grinding;
  }

  public void setGrinding(float grinding) {
    this.grinding = grinding;
  }

  public float getChance() {
    return chance;
  }

  public void setChance(float chance) {
    this.chance = chance;
  }

  public float getPower() {
    return power;
  }

  public void setPower(float power) {
    this.power = power;
  }

  public int getDurability() {
    return durability;
  }

  public void setDurability(int durability) {
    this.durability = durability;
  }

  public void setName(@Nonnull Optional<String> name) {
    this.name = name;
  }

}
