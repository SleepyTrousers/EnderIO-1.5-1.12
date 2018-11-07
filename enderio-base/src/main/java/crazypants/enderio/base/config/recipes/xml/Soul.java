package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.util.ResourceLocation;

public class Soul implements RecipeConfigElement {

  protected String name;
  protected transient CapturedMob mob = null;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (name == null || name.trim().isEmpty()) {
      mob = null;
      return this;
    }
    mob = CapturedMob.create(new ResourceLocation(name.trim()));
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      Log.warn("Could not find an entity for '" + name + "'");
      Log.warn("Available entities are:");
      for (CapturedMob possible : CapturedMob.getAllSouls()) {
        Log.warn(" -> " + possible.getEntityName() + " (" + possible.getDisplayName() + ")");
      }
      throw new InvalidRecipeConfigException("Could not find an entity for '" + name + "'");
    }
  }

  @Override
  public boolean isValid() {
    return mob != null;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = value;
      return true;
    }
    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public CapturedMob getMob() {
    return mob;
  }

}
