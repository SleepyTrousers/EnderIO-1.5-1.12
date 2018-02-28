package crazypants.enderio.base.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Recipe extends AbstractConditional {

  private String name;

  private boolean required;

  private boolean disabled;

  private final @Nonnull List<AbstractConditional> craftings = new ArrayList<AbstractConditional>();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (disabled) {
      return this;
    }
    try {
      super.readResolve();
      if (craftings.isEmpty()) {
        throw new InvalidRecipeConfigException("No <crafting>s or <smelting>s");
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in recipe '" + getName() + "'");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (disabled || !active) {
      return;
    }
    try {
      int count = 0;
      for (AbstractConditional crafting : craftings) {
        if (required) {
          if (crafting.isActive()) {
            crafting.enforceValidity();
            if (crafting.isValid()) {
              count++;
            }
          }
        } else {
          if (crafting.isActive() && crafting.isValid()) {
            count++;
          }
        }
      }
      if (count > 1) {
        throw new InvalidRecipeConfigException("Multiple active <crafting>s and/or <smelting>s");
      } else if (count < 1) {
        if (required) {
          throw new InvalidRecipeConfigException("No valid <crafting>s or <smelting>s");
        } else {
          valid = false;
        }
      } else {
        valid = true;
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in recipe '" + getName() + "'");
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (!disabled && valid && active) {
      Log.debug("Registering XML recipe '" + getName() + "'");
      for (AbstractConditional crafting : craftings) {
        if (crafting.isValid() && crafting.isActive()) {
          crafting.register(recipeName);
          return;
        }
      }
    } else {
      Log.debug("Skipping XML recipe '" + getName() + "' (valid=" + valid + ", active=" + active + ", required=" + required + ", disabled=" + disabled + ")");
    }
  }

  @Override
  public @Nonnull String getName() {
    if (name != null && !name.trim().isEmpty()) {
      return name.trim();
    }
    return "unnamed recipe";
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = value;
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

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    try {
      if ("crafting".equals(name)) {
        craftings.add(factory.read(new Crafting(), startElement));
        return true;
      }
      if ("smelting".equals(name)) {
        craftings.add(factory.read(new Smelting(), startElement));
        return true;
      }
      if ("casting".equals(name)) {
        craftings.add(factory.read(new Casting(), startElement));
        return true;
      }
      if ("enchanting".equals(name)) {
        craftings.add(factory.read(new Enchanting(), startElement));
        return true;
      }
      if ("spawning".equals(name)) {
        craftings.add(factory.read(new Spawning(), startElement));
        return true;
      }
      if ("alloying".equals(name)) {
        craftings.add(factory.read(new Alloying(), startElement));
        return true;
      }
      if ("sagmilling".equals(name)) {
        craftings.add(factory.read(new Sagmilling(), startElement));
        return true;
      }
      if ("slicing".equals(name)) {
        craftings.add(factory.read(new Slicing(), startElement));
        return true;
      }
      if ("fermenting".equals(name)) {
        craftings.add(factory.read(new Fermenting(), startElement));
        return true;
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in recipe '" + getName() + "'");
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