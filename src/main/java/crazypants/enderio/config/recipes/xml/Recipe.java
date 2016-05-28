package crazypants.enderio.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.Log;
import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.StaxFactory;

public class Recipe extends AbstractConditional {

  private String name;

  private boolean required;

  private boolean disabled;

  private List<AbstractCrafting> craftings;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (disabled) {
      return this;
    }
    try {
      super.readResolve();
      int validSubs = 0;
      int activeSubs = 0;
      int activatableSubs = 0;
      if (craftings != null) {
        for (AbstractCrafting crafting : craftings) {
          if (crafting.isValid()) {
            validSubs++;
          }
          if (crafting.isActive()) {
            activeSubs++;
          }
          if (crafting.isValid() && crafting.isActive()) {
            activatableSubs++;
          }
        }
      }
      valid = validSubs > 0;

      if (active) {
        if (craftings == null || craftings.isEmpty()) {
          throw new InvalidRecipeConfigException("No <crafting>s or <smelting>s");
        }
        if (activatableSubs > 1) {
          throw new InvalidRecipeConfigException("Multiple active <crafting>s and/or <smelting>s");
        }
        if (required && !valid) {
          throw new InvalidRecipeConfigException("No valid <crafting>s or <smelting>s");
        }

        active = activeSubs > 0;
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in recipe '" + getName() + "'");
    }
    return this;
  }

  @Override
  public void register() {
    if (!disabled && valid && active) {
      Log.debug("Registering XML recipe '" + getName() + "'");
      if (craftings != null) {
        for (AbstractCrafting crafting : craftings) {
          if (crafting.isValid() && crafting.isActive()) {
            crafting.register();
            return;
          }
        }
      }
    } else {
      Log.debug("Skipping XML recipe '" + getName() + "' (valid=" + valid + ", active=" + active + ", required=" + required + ", disabled=" + disabled + ")");
    }
  }

  public String getName() {
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
    if ("crafting".equals(name)) {
      if (craftings == null) {
        craftings = new ArrayList<AbstractCrafting>();
      }
      craftings.add(factory.read(new Crafting(), startElement));
      return true;
    }
    if ("smelting".equals(name)) {
      if (craftings == null) {
        craftings = new ArrayList<AbstractCrafting>();
      }
      craftings.add(factory.read(new Smelting(), startElement));
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