package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.fluid.FluidFuelRegister;

public class Coolant extends AbstractConditional {

  private Optional<Fluid> fluid = empty();
  private Optional<Float> amount = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (!fluid.isPresent()) {
        throw new InvalidRecipeConfigException("Missing attribute 'fluid'");
      }
      if (!amount.isPresent() || amount.get() <= 0f) {
        throw new InvalidRecipeConfigException("Missing attribute 'amount'");
      }

      valid = fluid.get().isValid();

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <coolant>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    fluid.get().enforceValidity();
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      FluidFuelRegister.instance.addCoolant(fluid.get().getFluid(), amount.get());
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
    if ("amount".equals(name)) {
      try {
        amount = of(Float.parseFloat(value));
      } catch (NumberFormatException e) {
        throw new InvalidRecipeConfigException("Invalid value in 'amount': Not a number");
      }
      return true;
    }
    return super.setAttribute(factory, name, value);
  }

}
