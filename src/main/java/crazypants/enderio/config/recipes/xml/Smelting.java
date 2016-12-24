package crazypants.enderio.config.recipes.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.StaxFactory;
import crazypants.enderio.integration.tic.TicProxy;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Smelting extends AbstractCrafting {

  private Float exp;
  private boolean tinkers = false;
  private boolean vanilla = true;

  private FloatItem input;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (exp == null) {
        if (valid) {
          exp = FurnaceRecipes.instance().getSmeltingExperience(getOutput().getItemStack());
        }
      } else {
        if (exp < 0) {
          throw new InvalidRecipeConfigException("Invalid negative value for 'exp'");
        }
        if (exp > 1) {
          throw new InvalidRecipeConfigException("Invalid value for 'exp', above 100%");
        }
      }
      if (input == null) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
      if (!vanilla && !tinkers) {
        throw new InvalidRecipeConfigException("One or more of 'vanilla' or 'tinkers' must be enabled");
      }
      if (vanilla && input.amount != 1f) {
        throw new InvalidRecipeConfigException("For 'vanilla' setting an input amount is not valid");
      }

      valid = valid && input.isValid();

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <crafting>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    input.enforceValidity();
  }

  @Override
  public void register() {
    if (isValid() && isActive()) {
      if (vanilla) {
        GameRegistry.addSmelting(input.getItemStack(), getOutput().getItemStack(), exp);
      }
      if (tinkers) {
        TicProxy.registerSmelterySmelting(input.getItemStack(), getOutput().getItemStack(), 1f / input.amount);
      }
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("exp".equals(name)) {
      this.exp = Float.parseFloat(value);
      return true;
    }
    if ("tinkers".equals(name)) {
      this.tinkers = Boolean.parseBoolean(value);
      return true;
    }
    if ("vanilla".equals(name)) {
      this.vanilla = Boolean.parseBoolean(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name)) {
      if (input == null) {
        input = factory.read(new FloatItem(), startElement);
        return true;
      }
    }

    return super.setElement(factory, name, startElement);
  }

}