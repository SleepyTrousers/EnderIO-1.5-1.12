package crazypants.enderio.base.config.recipes.xml;

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
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.ThingsRecipeInput;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;

public class Slicing extends AbstractCrafting {

  private int energy;
  private NNList<Item> inputs = new NNList<>();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (inputs.size() != 6) {
        throw new InvalidRecipeConfigException("Wrong number of <input>");
      }
      if (energy < 0) {
        throw new InvalidRecipeConfigException("Invalid negative value for 'energy'");
      }

      for (Item input : inputs) {
        valid = valid && input.isValid();
      }

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <slicing>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    for (Item input : inputs) {
      input.enforceValidity();
    }
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      NNList<IRecipeInput> inputStacks = new NNList<>();
      for (NNIterator<Item> itr = inputs.fastIterator(); itr.hasNext();) {
        final Item item = itr.next();
        inputStacks.add(new ThingsRecipeInput(item.getThing(), item.getItemStack(), inputStacks.size()));
      }
      RecipeOutput recipeOutput = new RecipeOutput(getOutput().getItemStack());
      SliceAndSpliceRecipeManager.getInstance()
          .addRecipe(new Recipe(recipeOutput, energy, RecipeBonusType.NONE, inputStacks.toArray(new IRecipeInput[inputStacks.size()])));
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
    if ("input".equals(name)) {
      inputs.add(factory.read(new ItemIntegerAmount(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}