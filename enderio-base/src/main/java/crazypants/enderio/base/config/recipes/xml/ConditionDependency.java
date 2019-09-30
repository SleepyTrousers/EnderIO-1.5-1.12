package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;
import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class ConditionDependency implements IRecipeConfigElement {

  private Optional<String> itemString = empty(), modString = empty(), upgradeString = empty();
  private boolean reverse, valid;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      if (!itemString.isPresent() && !modString.isPresent() && !upgradeString.isPresent()) {
        throw new InvalidRecipeConfigException("Missing item and mod and upgrade");
      }
      valid = true;
      if (itemString.isPresent()) {
        ItemOptional item = new ItemOptional().setAllowDelaying(false);
        item.setName(get(itemString));
        item.readResolve();
        valid = valid && item.isValid();
      }
      if (upgradeString.isPresent()) {
        if (upgradeString.get().length() > 64) {
          throw new InvalidRecipeConfigException(String.format("The upgrade ID %s is longer than the maximum of 64 characters.", upgradeString.get()));
        }
        if (!upgradeString.get().equals(upgradeString.get().toLowerCase(Locale.ENGLISH))) {
          throw new InvalidRecipeConfigException(String.format("The upgrade ID %s must be all lowercase.", upgradeString.get()));
        }
        valid = valid && UpgradeRegistry.getUpgrade(new ResourceLocation(get(upgradeString))) != null;
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
    if ("upgrade".equals(name)) {
      this.upgradeString = ofString(value);
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

  // json

  protected Optional<String> getItemString() {
    return itemString;
  }

  protected Optional<String> getModString() {
    return modString;
  }

  protected Optional<String> getUpgradeString() {
    return upgradeString;
  }

  protected boolean isReverse() {
    return reverse;
  }

}
