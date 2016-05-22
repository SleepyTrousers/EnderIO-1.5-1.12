package crazypants.enderio.config.recipes.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import crazypants.enderio.Log;

public class Recipe extends AbstractConditional {

  @XStreamAsAttribute
  @XStreamAlias("name")
  private String name;

  @XStreamAsAttribute
  @XStreamAlias("required")
  private boolean required;

  @XStreamImplicit(itemFieldName = "crafting")
  private List<Crafting> craftings;

  @XStreamImplicit(itemFieldName = "smelting")
  private List<Smelting> smeltings;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      int validSubs = 0;
      int activeSubs = 0;
      int activatableSubs = 0;
      if (craftings != null) {
        for (Crafting crafting : craftings) {
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
      if (smeltings != null) {
        for (Smelting smelting : smeltings) {
          if (smelting.isValid() && smelting.isActive()) {
            if (valid) {
              throw new InvalidRecipeConfigException("Multiple active <crafting>s");
            }
            valid = true;
          }
        }
      }
      valid = validSubs > 0;

      if (active) {
        if ((craftings == null || craftings.isEmpty()) && (smeltings == null || smeltings.isEmpty())) {
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
    if (valid && active) {
      Log.debug("Registering XML recipe '" + getName() + "'");
      if (craftings != null) {
        for (Crafting crafting : craftings) {
          if (crafting.isValid() && crafting.isActive()) {
            crafting.register();
            return;
          }
        }
      }
      if (smeltings != null) {
        for (Smelting smelting : smeltings) {
          if (smelting.isValid() && smelting.isActive()) {
            smelting.register();
            return;
          }
        }
      }
    } else {
      Log.debug("Skipping XML recipe '" + getName() + "' (valid=" + valid + ", active=" + active + ", required=" + required + ")");
    }
  }

  public String getName() {
    if (name != null && !name.trim().isEmpty()) {
      return name.trim();
    }
    return "unnamed recipe";
  }

}