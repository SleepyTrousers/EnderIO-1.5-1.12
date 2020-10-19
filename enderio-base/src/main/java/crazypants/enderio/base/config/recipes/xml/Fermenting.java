package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeInput;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.ThingsRecipeInput;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;

public class Fermenting extends AbstractConditional {

  private int energy;
  private NNList<Inputgroup> inputgroup = new NNList<>();
  private Optional<FluidMultiplier> inputfluid = empty();
  private Optional<Fluid> outputfluid = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (inputgroup.size() < 1 || inputgroup.size() > 2) {
        throw new InvalidRecipeConfigException("Wrong number of <inputgroup>");
      }
      if (energy <= 0) {
        throw new InvalidRecipeConfigException("Invalid low value for 'energy'");
      }
      if (!inputfluid.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <inputfluid>");
      }
      if (!outputfluid.isPresent()) {
        throw new InvalidRecipeConfigException("Missing <outputfluid>");
      }

      valid = inputfluid.get().isValid() && outputfluid.get().isValid();
      for (Inputgroup input : inputgroup) {
        valid = valid && input.isValid();
      }

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <fermenting>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    for (Inputgroup input : inputgroup) {
      input.enforceValidity();
    }
    inputfluid.get().enforceValidity();
    outputfluid.get().enforceValidity();
  }

  @Override
  public void register(@Nonnull String recipeName, @Nonnull RecipeLevel recipeLevel) {
    if (isValid() && isActive()) {
      NNList<IRecipeInput> inputStacks = new NNList<>();
      int slot = 0;
      for (NNIterator<Inputgroup> itr = inputgroup.fastIterator(); itr.hasNext();) {
        for (NNIterator<ItemMultiplier> itr2 = itr.next().getItems().fastIterator(); itr2.hasNext();) {
          ItemMultiplier item = itr2.next();
          inputStacks.add(new ThingsRecipeInput(item.getThing(), slot, item.multiplier));
        }
        slot++;
      }
      inputStacks.add(new RecipeInput(inputfluid.get().getFluidStack(), inputfluid.get().multiplier));
      RecipeOutput recipeOutput = new RecipeOutput(outputfluid.get().getFluidStack());
      VatRecipeManager.getInstance()
          .addRecipe(new Recipe(recipeOutput, energy, RecipeBonusType.NONE, recipeLevel, inputStacks.toArray(new IRecipeInput[inputStacks.size()])));
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
    if ("inputgroup".equals(name)) {
      inputgroup.add(factory.read(new Inputgroup(), startElement));
      return true;
    }
    if ("inputfluid".equals(name) && !inputfluid.isPresent()) {
      inputfluid = of(factory.read(new FluidMultiplier(), startElement));
      return true;
    }
    if ("outputfluid".equals(name) && !outputfluid.isPresent()) {
      outputfluid = of(factory.read(new Fluid(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}