package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.util.ResourceLocation;

public class Entity implements RecipeConfigElement {

  protected String name;
  protected transient CapturedMob mob = null;
  private double costMultiplier = 1;
  private boolean disabled = false;
  private boolean isDefault = false;
  private boolean isBoss = false;
  private boolean clone = false;
  private boolean soulvial = true;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (name == null || name.trim().isEmpty()) {
      mob = null;
      return this;
    }
    if (name.trim().equals("*")) {
      isDefault = true;
      if (clone) {
        throw new InvalidRecipeConfigException("Cannot set the default energy cost entry to 'clone'");
      }
      if (!soulvial) {
        throw new InvalidRecipeConfigException("Cannot set the default energy cost entry to not 'soulvial'");
      }
    } else if (name.trim().equals("*boss*")) {
      isBoss = true;
      if (clone) {
        throw new InvalidRecipeConfigException("Cannot set the 'all bosses' entry to 'clone'");
      }
    } else {
      mob = CapturedMob.create(new ResourceLocation(name.trim()));
      if (mob == null) {
        Log.info("Could not find an entity for '" + name + "'");
      }
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      Log.warn("Could not find an entity for '" + name + "'");
      Log.warn("Available entities are:");
      for (CapturedMob possible : CapturedMob.getAllSouls()) {
        Log.warn(" -> " + possible.getEntityName() + " (" + possible.getDisplayName() + ")");
        // Log.warn(" <entity name=\"" + possible.getEntityName() + "\" costMultiplier=\"1\" disabled=\"false\"/> <!-- " + possible.getDisplayName() + " -->");
      }
      throw new InvalidRecipeConfigException("Could not find an entity for '" + name + "'");
    }
  }

  @Override
  public boolean isValid() {
    return isDefault || isBoss || mob != null;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = value;
      return true;
    }
    if ("costMultiplier".equals(name)) {
      try {
        this.costMultiplier = Double.parseDouble(value);
      } catch (NumberFormatException e) {
        throw new InvalidRecipeConfigException("Invalid value in 'amount': Not a number");
      }
      return true;
    }
    if ("disabled".equals(name)) {
      this.disabled = Boolean.parseBoolean(value);
      return true;
    }
    if ("clone".equals(name)) {
      this.clone = Boolean.parseBoolean(value);
      return true;
    }
    if ("soulvial".equals(name)) {
      this.soulvial = Boolean.parseBoolean(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public double getCostMultiplier() {
    return costMultiplier;
  }

  public CapturedMob getMob() {
    return mob;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public boolean isBoss() {
    return isBoss;
  }

  public boolean isClone() {
    return clone;
  }

  public boolean isSoulvial() {
    return soulvial;
  }

}