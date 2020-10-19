package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.recipe.RecipeLevel;

public class Fuel extends AbstractConditional {

  private Optional<Fluid> fluid = empty();
  private Optional<Integer> perTick = empty(), ticks = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (!fluid.isPresent()) {
        throw new InvalidRecipeConfigException("Missing attribute 'fluid'");
      }
      if (!perTick.isPresent() || perTick.get() < 1) {
        throw new InvalidRecipeConfigException("Missing attribute 'perTick'");
      }
      if (!ticks.isPresent() || ticks.get() < 1) {
        throw new InvalidRecipeConfigException("Missing attribute 'ticks'");
      }

      valid = fluid.get().isValid();

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <fuel>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    fluid.get().enforceValidity();
  }

  @Override
  public void register(@Nonnull String recipeName, @Nonnull RecipeLevel recipeLevel) {
    if (isValid() && isActive()) {
      FluidFuelRegister.instance.addFuel(fluid.get().getFluid(), perTick.get(), ticks.get());
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("fluid".equals(name)) {
      fluid = of(new Fluid());
      fluid.get().setAttribute(factory, "name", value);
      fluid.get().readResolve();
      return true;
    }
    if ("pertick".equals(name)) {
      try {
        perTick = of(Integer.parseInt(value));
      } catch (NumberFormatException e) {
        throw new InvalidRecipeConfigException("Invalid value in 'pertick': Not a number");
      }
      return true;
    }
    if ("ticks".equals(name)) {
      try {
        ticks = of(Integer.parseInt(value));
      } catch (NumberFormatException e) {
        throw new InvalidRecipeConfigException("Invalid value in 'ticks': Not a number");
      }
      return true;
    }
    return super.setAttribute(factory, name, value);
  }

}
