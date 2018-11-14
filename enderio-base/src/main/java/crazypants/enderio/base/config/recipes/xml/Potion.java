package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.potion.PotionType;

public class Potion implements RecipeConfigElement {

  protected String name;
  protected transient PotionType potion = null;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (name == null || name.trim().isEmpty()) {
      potion = null;
      return this;
    }
    potion = PotionType.getPotionTypeForName(name.trim());
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      throw new InvalidRecipeConfigException("Could not find a potion for '" + name + "'");
    }
  }

  @Override
  public boolean isValid() {
    return potion != null;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name) || "potion".equals(name)) {
      this.name = value;
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public PotionType getPotion() {
    return potion;
  }

}
