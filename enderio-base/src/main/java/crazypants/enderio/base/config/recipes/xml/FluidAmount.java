package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraftforge.fluids.FluidStack;

public class FluidAmount extends Fluid {

  protected int amount = 1000;

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
  
  public @Nonnull FluidStack getFluidStack() {
    FluidStack ret = super.getFluidStack();
    ret.amount = this.amount;
    return ret;
  }

}
