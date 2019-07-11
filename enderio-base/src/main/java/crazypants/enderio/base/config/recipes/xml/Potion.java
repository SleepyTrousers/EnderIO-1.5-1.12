package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.potion.PotionType;

public class Potion implements IRecipeConfigElement {

  protected Optional<String> name = empty();
  protected transient Optional<PotionType> potion = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (!name.isPresent()) {
      potion = empty();
      return this;
    }
    potion = ofNullable(PotionType.getPotionTypeForName(get(name)));
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      throw new InvalidRecipeConfigException("Could not find a potion for '" + name.get() + "'");
    }
  }

  @Override
  public boolean isValid() {
    return potion.isPresent();
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name) || "potion".equals(name)) {
      this.name = ofString(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public PotionType getPotion() {
    return get(potion);
  }

}
