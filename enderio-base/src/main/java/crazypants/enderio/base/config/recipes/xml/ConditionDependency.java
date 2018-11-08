package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraftforge.fml.common.Loader;

public class ConditionDependency implements RecipeConfigElement {

  private String itemString, modString;
  private boolean reverse, valid;

  private transient ItemOptional item;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      final boolean hasItem = itemString != null && !itemString.trim().isEmpty();
      final boolean hasMod = modString != null && !modString.trim().isEmpty();
      if (!hasItem && !hasMod) {
        throw new InvalidRecipeConfigException("Missing item and mod");
      }
      valid = true;
      if (hasItem) {
        item = new ItemOptional().setAllowDelaying(false);
        item.setName(itemString);
        item.readResolve();
        valid = valid && item.isValid();
      }
      if (hasMod) {
        if (modString.length() > 64) {
          throw new InvalidRecipeConfigException(String.format("The modId %s is longer than the maximum of 64 characters.", modString));
        }
        if (!modString.equals(modString.toLowerCase(Locale.ENGLISH))) {
          throw new InvalidRecipeConfigException(String.format("The modId %s must be all lowercase.", modString));
        }
        valid = valid && Loader.isModLoaded(modString);
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <dependency>");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return valid != reverse;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("item".equals(name)) {
      this.itemString = value;
      return true;
    }
    if ("mod".equals(name)) {
      this.modString = value;
      return true;
    }
    if ("reverse".equals(name)) {
      this.reverse = Boolean.parseBoolean(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
  }

}
