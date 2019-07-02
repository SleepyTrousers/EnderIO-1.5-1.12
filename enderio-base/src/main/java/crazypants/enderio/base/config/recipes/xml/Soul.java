package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.util.ResourceLocation;

public class Soul implements IRecipeConfigElement {

  protected Optional<String> name = empty();
  protected transient Optional<CapturedMob> mob = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (!name.isPresent()) {
      mob = empty();
      return this;
    }
    mob = ofNullable(CapturedMob.create(new ResourceLocation(get(name))));
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      Log.warn("Could not find an entity for '" + name.get() + "'");
      Log.warn("Available entities are:");
      for (CapturedMob possible : CapturedMob.getAllSouls()) {
        Log.warn(" -> " + possible.getEntityName() + " (" + possible.getDisplayName() + ")");
      }
      throw new InvalidRecipeConfigException("Could not find an entity for '" + name.get() + "'");
    }
  }

  @Override
  public boolean isValid() {
    return mob.isPresent();
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
      return true;
    }
    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public CapturedMob getMob() {
    return get(mob);
  }

}
