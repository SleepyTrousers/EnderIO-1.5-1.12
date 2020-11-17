package crazypants.enderio.base.config.recipes.xml;

import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe.Logic;
import crazypants.enderio.util.FuncUtil;
import net.minecraftforge.fluids.FluidStack;

public class Tanking extends AbstractConditional {

  private enum Type {
    EMPTY,
    FILL;
  }

  private Optional<Item> input = empty();
  private Optional<ItemIntegerAmount> output = empty();
  private Optional<FluidAmount> fluid = empty();
  private Logic logic = Logic.NONE;
  private Optional<Type> type = empty();

  private volatile @Nullable TankMachineRecipe registered = null;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (!input.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
      if (!fluid.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <fluid>");
      }
      if (!type.isPresent()) {
        throw new InvalidRecipeConfigException("Missing attribute 'type'");
      }

      valid = input.get().isValid() && fluid.get().isValid() && (!output.isPresent() || output.get().isValid());

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <tanking>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    input.get().enforceValidity();
    if (input.get().getThing().isEmpty()) {
      throw new InvalidRecipeConfigException("Valid <input> child elements are invalid in <tanking>");
    }
    fluid.get().enforceValidity();
    if (output.isPresent()) {
      output.get().enforceValidity();
      if (output.get().getThing().isEmpty()) {
        throw new InvalidRecipeConfigException("Valid <output> child elements are invalid in <tanking>");
      }
    }
  }

  @Override
  public void register(@Nonnull String recipeName, @Nonnull RecipeLevel recipeLevel) {
    if (isValid() && isActive()) {
      final Things inThing = input.get().getThing();
      if (!inThing.isEmpty()) {
        final Things outThing = output.isPresent() ? output.get().getThing() : null;
        final FluidStack fluidStack = fluid.get().getFluidStack();
        final boolean isFilling = type.get() == Type.FILL;

        TankMachineRecipe recipe = new TankMachineRecipe(recipeName, isFilling, inThing, fluidStack, outThing, logic, recipeLevel);
        MachineRecipeRegistry.instance.registerRecipe(registered = recipe);
      }
    }
  }

  @Override
  public void unregister() {
    FuncUtil.doIf(registered, MachineRecipeRegistry.instance::removeRecipe);
    registered = null;
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
        type = of(Type.valueOf(NullHelper.first(value.toUpperCase(Locale.ENGLISH))));
      } catch (IllegalArgumentException e) {
        throw new InvalidRecipeConfigException("'" + value + "' is not a valid value for 'type'");
      }
      return true;
    }
    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name) && !input.isPresent()) {
      input = of(factory.read(new Item().setAllowDelaying(false), startElement));
      return true;
    }
    if ("fluid".equals(name) && !fluid.isPresent()) {
      fluid = of(factory.read(new FluidAmount(), startElement));
      return true;
    }
    if ("output".equals(name) && !output.isPresent()) {
      output = of(factory.read(new ItemIntegerAmount().setAllowDelaying(false), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}
