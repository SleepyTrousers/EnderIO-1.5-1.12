package crazypants.enderio.base.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Enchantment implements RecipeConfigElement {

  protected String name;
  protected transient net.minecraft.enchantment.Enchantment enchantment = null;
  private double costMultiplier = 1;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (name == null || name.trim().isEmpty()) {
      enchantment = null;
      return this;
    }
    enchantment = net.minecraft.enchantment.Enchantment.getEnchantmentByLocation(name.trim());
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      throw new InvalidRecipeConfigException(
          "Could not find an enchantment for '" + name + "'");
    }
  }

  @Override
  public boolean isValid() {
    return enchantment != null;
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

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public net.minecraft.enchantment.Enchantment getEnchantment() {
    return enchantment;
  }

  public double getCostMultiplier() {
    return costMultiplier;
  }

}