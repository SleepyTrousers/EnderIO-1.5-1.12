package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Enchantment implements IRecipeConfigElement {

  protected Optional<String> name = empty();
  protected transient Optional<net.minecraft.enchantment.Enchantment> enchantment = empty();
  private double costMultiplier = 1;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (!name.isPresent()) {
      enchantment = empty();
      return this;
    }
    enchantment = ofNullable(net.minecraft.enchantment.Enchantment.getEnchantmentByLocation(get(name)));
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      throw new InvalidRecipeConfigException(
          "Could not find an enchantment for '" + name.get() + "'");
    }
  }

  @Override
  public boolean isValid() {
    return enchantment.isPresent();
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
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

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public net.minecraft.enchantment.Enchantment getEnchantment() {
    return get(enchantment);
  }

  public double getCostMultiplier() {
    return costMultiplier;
  }

}