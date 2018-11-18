package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe.Logic;
import net.minecraftforge.fluids.FluidStack;

public class Tanking extends AbstractConditional {

  private enum Type {
    EMPTY,
    FILL;
  }

  private Item input;
  private ItemIntegerAmount output;
  private FluidAmount fluid;
  private @Nonnull Logic logic = Logic.NONE;
  private Type type;

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
      if (type == null) {
        throw new InvalidRecipeConfigException("Missing attribute 'type'");
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
      final Things inThing = input.getThing();
      final Things outThing = output != null ? output.getThing() : null;
      FluidStack fluidStack = fluid.getFluidStack();
      boolean isFilling = type == Type.FILL;

      if (!inThing.isEmpty()) {
        TankMachineRecipe recipe = new TankMachineRecipe(recipeName, isFilling, inThing, fluidStack, outThing, logic, RecipeLevel.IGNORE);
        MachineRecipeRegistry.instance.registerRecipe(recipe);
      }
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
    if ("type".equals(name)) {
      try {
        type = Type.valueOf(value.toUpperCase(Locale.ENGLISH));
      } catch (IllegalArgumentException e) {
        throw new InvalidRecipeConfigException("'" + value + "' is not a valid value for 'type'");
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
        output = factory.read(new ItemIntegerAmount().setAllowDelaying(false), startElement);
        return true;
      }
    }

    return super.setElement(factory, name, startElement);
  }

}
