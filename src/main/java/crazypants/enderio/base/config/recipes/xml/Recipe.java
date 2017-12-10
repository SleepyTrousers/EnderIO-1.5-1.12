package crazypants.enderio.base.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Recipe extends AbstractConditional {

  private String name;

  private boolean required;

  private boolean disabled;

  private List<AbstractConditional> craftings;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (disabled) {
      return this;
    }
    try {
      super.readResolve();
      if (craftings == null || craftings.isEmpty()) {
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
          Log.debug("No valid <crafting>s or <smelting>s in optional recipe '" + name + "'");
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
  public void register() {
    if (!disabled && valid && active) {
      Log.debug("Registering XML recipe '" + getName() + "'");
      if (craftings != null) {
        for (AbstractConditional crafting : craftings) {
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
        craftings = new ArrayList<AbstractConditional>();
      }
      craftings.add(factory.read(new Crafting(), startElement));
      return true;
    }
    if ("smelting".equals(name)) {
      if (craftings == null) {
        craftings = new ArrayList<AbstractConditional>();
      }
      craftings.add(factory.read(new Smelting(), startElement));
      return true;
    }
    if ("casting".equals(name)) {
      if (craftings == null) {
        craftings = new ArrayList<AbstractConditional>();
      }
      craftings.add(factory.read(new Casting(), startElement));
      return true;
    }
    if ("enchanting".equals(name)) {
      if (craftings == null) {
        craftings = new ArrayList<AbstractConditional>();
      }
      craftings.add(factory.read(new Enchanting(), startElement));
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