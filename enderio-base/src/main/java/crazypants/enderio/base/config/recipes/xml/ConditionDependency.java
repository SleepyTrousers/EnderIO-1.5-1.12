package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraftforge.fml.common.Loader;

public class ConditionDependency implements IRecipeConfigElement {

  private Optional<String> itemString = empty(), modString = empty();
  private boolean reverse, valid;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      if (!itemString.isPresent() && !modString.isPresent()) {
        throw new InvalidRecipeConfigException("Missing item and mod");
      }
      valid = true;
      if (itemString.isPresent()) {
        ItemOptional item = new ItemOptional().setAllowDelaying(false);
        item.setName(get(itemString));
        item.readResolve();
        valid = valid && item.isValid();
      }
      if (modString.isPresent()) {
        if (modString.get().length() > 64) {
          throw new InvalidRecipeConfigException(String.format("The modId %s is longer than the maximum of 64 characters.", modString.get()));
        }
        if (!modString.get().equals(modString.get().toLowerCase(Locale.ENGLISH))) {
          throw new InvalidRecipeConfigException(String.format("The modId %s must be all lowercase.", modString.get()));
        }
        valid = valid && Loader.isModLoaded(modString.get());
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
      this.itemString = ofString(value);
      return true;
    }
    if ("mod".equals(name)) {
      this.modString = ofString(value);
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
