package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraftforge.fluids.FluidStack;

public class FluidAmount extends Fluid {

  protected int amount = 1000;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (amount < 1) {
      throw new InvalidRecipeConfigException("Invalid negative fluid amount");
    }
    return this;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("amount".equals(name)) {
      try {
        this.amount = Integer.parseInt(value);
      } catch (NumberFormatException e) {
        throw new InvalidRecipeConfigException("Invalid value in 'amount': Not a number");
      }
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public @Nonnull FluidStack getFluidStack() {
    return new FluidStack(fluid.get(), amount, tag.orElse(null));
  }

}
