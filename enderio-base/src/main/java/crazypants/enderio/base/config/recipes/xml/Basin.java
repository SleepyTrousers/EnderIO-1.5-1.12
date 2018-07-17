package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.basin.BasinRecipeManager;

public class Basin extends AbstractCrafting {

  private int energy;
  private BasinOrientation orientation;
  private Fluid inputA, inputB;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (energy < 0) {
        throw new InvalidRecipeConfigException("Invalid negative value for 'energy'");
      }
      if (orientation == null) {
        throw new InvalidRecipeConfigException("Missing <orientation>");
      }
      if (inputA == null || inputB == null) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <basin>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    inputA.enforceValidity();
    inputB.enforceValidity();
  }
  
  @Override
  public boolean isValid() {
    return super.isValid() && inputA.isValid() && inputB.isValid();
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      BasinRecipeManager.getInstance().addRecipe(inputA.getFluidStack(), inputB.getFluidStack(), getOutput().getItemStack(), orientation.getOrientation(), energy);
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("energy".equals(name)) {
      this.energy = Integer.parseInt(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("inputfluid".equals(name)) {
      Fluid input = factory.read(new FluidAmount(), startElement);
      if (inputA == null) {
        inputA = input;
      } else if (inputB == null) {
        inputB = input;
      } else {
        throw new InvalidRecipeConfigException("Too many inputs");
      }
      return true;
    }
    
    if ("orientation".equals(name)) {
      orientation = factory.read(new BasinOrientation(), startElement);
      return true;
    }

    return super.setElement(factory, name, startElement);
  }
}
