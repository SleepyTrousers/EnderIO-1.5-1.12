package crazypants.enderio.base.config.recipes.xml;

import java.util.ArrayList;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.RecipeBonusType;
import crazypants.enderio.base.recipe.RecipeOutput;
import crazypants.enderio.base.recipe.ThingsRecipeInput;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;

public class Sagmilling extends AbstractCrafting {

  private int energy;
  private @Nonnull RecipeBonusType bonus = RecipeBonusType.MULTIPLY_OUTPUT;
  private Item input;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      super.readResolve();
      if (input == null) {
        throw new InvalidRecipeConfigException("Missing <input>");
      }
      if (energy < 0) {
        throw new InvalidRecipeConfigException("Invalid negative value for 'energy'");
      }

      valid = valid && input.isValid();

    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <alloying>");
    }
    return this;
  }

  @Override
  protected boolean checkOutputCount(int count) {
    return count >= 1;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    super.enforceValidity();
    input.enforceValidity();
  }

  @Override
  public void register(@Nonnull String recipeName) {
    if (isValid() && isActive()) {
      ThingsRecipeInput recipeInput = new ThingsRecipeInput(input.getThing());
      NNList<RecipeOutput> recipeOutputs = new NNList<>();
      for (Output output : getOutputs()) {
        if (output instanceof OutputWithChance) {
          recipeOutputs.add(new RecipeOutput(output.getItemStack(), ((OutputWithChance) output).getChance()));
        } else {
          recipeOutputs.add(new RecipeOutput(output.getItemStack(), 1));
        }
      }
      Recipe recipe = new Recipe(recipeInput, energy, bonus, recipeOutputs.toArray(new RecipeOutput[0]));
      SagMillRecipeManager.getInstance().addRecipe(recipe);
    }
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("bonus".equals(name)) {
      try {
        this.bonus = RecipeBonusType.valueOf(value.toUpperCase(Locale.ENGLISH));
      } catch (IllegalArgumentException e) {
        throw new InvalidRecipeConfigException("'" + value + "' is not a valid value for 'bonus'");
      }
      return true;
    }
    if ("energy".equals(name)) {
      this.energy = Integer.parseInt(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("input".equals(name)) {
      if (input == null) {
        input = factory.read(new ItemIntegerAmount(), startElement);
        return true;
      }
    }
    if ("output".equals(name)) {
      if (outputs == null) {
        outputs = new ArrayList<Output>();
      }
      outputs.add(factory.read(new OutputWithChance(), startElement));
      return true;
    }

    return super.setElement(factory, name, startElement);
  }

}