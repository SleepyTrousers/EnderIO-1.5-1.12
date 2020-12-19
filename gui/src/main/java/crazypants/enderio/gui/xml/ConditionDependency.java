package crazypants.enderio.gui.xml;

import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class ConditionDependency implements IRecipeConfigElement {

  private @Nonnull Optional<String> itemString = empty(), modString = empty(), upgradeString = empty();
  private boolean reverse;

  @Override
  public void validate() throws InvalidRecipeConfigException {
    if (!itemString.isPresent() && !modString.isPresent() && !upgradeString.isPresent()) {
      throw new InvalidRecipeConfigException("Missing item and mod and upgrade");
    }
    if (upgradeString.isPresent()) {
      if (upgradeString.get().length() > 64) {
        throw new InvalidRecipeConfigException(String.format("The upgrade ID %s is longer than the maximum of 64 characters.", upgradeString.get()));
      }
      if (!upgradeString.get().equals(upgradeString.get().toLowerCase(Locale.ENGLISH))) {
        throw new InvalidRecipeConfigException(String.format("The upgrade ID %s must be all lowercase.", upgradeString.get()));
      }
    }
    if (modString.isPresent()) {
      if (modString.get().length() > 64) {
        throw new InvalidRecipeConfigException(String.format("The modId %s is longer than the maximum of 64 characters.", modString.get()));
      }
      if (!modString.get().equals(modString.get().toLowerCase(Locale.ENGLISH))) {
        throw new InvalidRecipeConfigException(String.format("The modId %s must be all lowercase.", modString.get()));
      }
    }
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
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
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

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

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    parent.child("dependency").attribute("item", itemString, true).attribute("mod", modString, true).attribute("upgrade", upgradeString, true)
        .attribute("reverse", reverse);
  }

}
