package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.ThingsRecipeInput;
import crazypants.enderio.base.recipe.sagmill.GrindingBall;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;

public class Grindingball extends AbstractConditional {

  private Optional<String> name = empty();

  private boolean required;

  private boolean disabled;

  private Optional<Item> item = empty();

  private float grinding = 1f, chance = 1f, power = 1f;

  private int durability = 0;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (disabled) {
      return this;
    }
    try {
      super.readResolve();
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

      valid = item.get().isValid();

      if (required && !valid && active) {
        throw new InvalidRecipeConfigException("No valid <item>");
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <grindingball>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (disabled || !active) {
      return;
    }
    item.get().enforceValidity();
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (!disabled && valid && active) {
      Log.debug("Registering XML recipe '" + getName() + "'");

      SagMillRecipeManager.getInstance().addBall(new GrindingBall(new ThingsRecipeInput(item.get().getThing()), grinding, chance, power, durability));

    } else {
      Log.debug("Skipping XML recipe '" + getName() + "' (valid=" + valid + ", active=" + active + ", required=" + required + ", disabled=" + disabled + ")");
    }
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getName() {
    return name.orElse("unnamed recipe");
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
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
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("item".equals(name) && !item.isPresent()) {
      item = of(factory.read(new Item().setAllowDelaying(false), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

  @Override
  public boolean isValid() {
    return disabled || super.isValid();
  }

  @Override
  public boolean isActive() {
    return !disabled && super.isActive();
  }

}
