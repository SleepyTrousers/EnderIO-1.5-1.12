package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe.Logic;

public class Tanking extends AbstractConditional {

  private Item input, output;
  private FluidAmount fluid;
  private Logic logic = Logic.NONE;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (input == null) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
      if (fluid == null) {
        throw new InvalidRecipeConfigException("Missing <fluid>");
      }

      valid = input.isValid() && fluid.isValid() && (output == null || output.isValid());

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <tanking>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    input.enforceValidity();
    if (input.getThing().isEmpty()) {
      throw new InvalidRecipeConfigException("Valid child elements are invalid in <tanking>");
    }
    fluid.enforceValidity();
    if (output != null) {
      output.enforceValidity();
      if (output.getThing().isEmpty()) {
        throw new InvalidRecipeConfigException("Valid child elements are invalid in <tanking>");
      }
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      // final Things thing = input.getThing();
      // PotionType inPotion = in.getPotion();
      // PotionType outPotion = out.getPotion();
      // if (!thing.isEmpty() && inPotion != null && outPotion != null) {
      // PotionHelper.addMix(inPotion, thing.asIngredient(), outPotion);
      // }
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("logic".equals(name)) {
      try {
        logic = Logic.valueOf(value.toUpperCase(Locale.ENGLISH));
      } catch (IllegalArgumentException e) {
        throw new InvalidRecipeConfigException("'" + value + "' is not a valid value for 'logic'");
      }
      return true;
    }
    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name)) {
      if (input == null) {
        input = factory.read(new Item().setAllowDelaying(false), startElement);
        return true;
      }
    }
    if ("fluid".equals(name)) {
      if (fluid == null) {
        fluid = factory.read(new FluidAmount(), startElement);
        return true;
      }
    }
    if ("output".equals(name)) {
      if (output == null) {
        output = factory.read(new Item().setAllowDelaying(false), startElement);
        return true;
      }
    }

    return super.setElement(factory, name, startElement);
  }

}
