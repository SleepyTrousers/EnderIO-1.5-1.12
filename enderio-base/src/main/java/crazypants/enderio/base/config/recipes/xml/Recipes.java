package crazypants.enderio.base.config.recipes.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeRoot;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraftforge.fml.common.ProgressManager;

public class Recipes implements RecipeRoot {

  private final @Nonnull List<AbstractConditional> recipes = new ArrayList<AbstractConditional>();

  @Override
  public List<AbstractConditional> getRecipes() {
    return recipes;
  }

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    return this;
  }

  @Override
  public boolean isValid() {
    return true;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public void register(@Nonnull String recipeName) {
    final String prefix = recipeName.isEmpty() ? "" : recipeName + ": ";
    Log.debug("Starting registering XML recipes");
    ProgressManager.ProgressBar bar = ProgressManager.push("Recipe", recipes.size());
    for (AbstractConditional recipe : recipes) {
      bar.step(prefix + recipe.getName());
      recipe.register(prefix + recipe.getName());
    }
    ProgressManager.pop(bar);
    Log.debug("Done registering XML recipes");
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends RecipeRoot> T addRecipes(RecipeRoot other, Overrides overrides) throws InvalidRecipeConfigException {
    if (other instanceof Recipes) {
      if (recipes.isEmpty()) {
        return (T) other;
      }

      if (((Recipes) other).recipes.isEmpty()) {
        return (T) this;
      }

      Map<String, AbstractConditional> recipeNames = new HashMap<>();
      for (AbstractConditional recipe : recipes) {
        recipeNames.put(recipe.getName(), recipe);
      }

      for (AbstractConditional recipe : ((Recipes) other).recipes) {
        if (!recipeNames.containsKey(recipe.getName())) {
          recipes.add(recipe);
        } else if (overrides == Overrides.DENY) {
          throw new InvalidRecipeConfigException(
              "Duplicate recipe '" + recipe.getName() + "'. A recipe with the same name was already read from " + recipe.getSource());
        } else if (overrides == Overrides.WARN) {
          Log.warn("Recipe '" + recipe.getName() + "' from '" + recipe.getSource() + "' is being replaced by a recipe from '"
              + recipeNames.get(recipe.getName()).getSource() + "'");
        }
      }

    }
    return (T) this;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("enderio".equals(name)) {
      return true;
    }
    if ("xsi".equals(name)) {
      return true;
    }
    if ("schemaLocation".equals(name)) {
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("alias".equals(name)) {
      factory.skip(startElement);
      return true;
    }
    if ("recipe".equals(name)) {
      addRecipe(new Recipe(), factory, startElement);
      return true;
    }
    if ("grindingball".equals(name)) {
      addRecipe(new Grindingball(), factory, startElement);
      return true;
    }
    if ("capacitor".equals(name)) {
      addRecipe(new Capacitor(), factory, startElement);
      return true;
    }

    return false;
  }

  private <T extends AbstractConditional> void addRecipe(T element, StaxFactory factory, StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    final AbstractConditional recipe = factory.read(element, startElement);
    for (AbstractConditional existingRecipe : recipes) {
      if (existingRecipe.getName().equals(recipe.getName())) {
        throw new InvalidRecipeConfigException(
            "Duplicate recipe '" + recipe.getName() + "'. A recipe with the same name was already read from " + existingRecipe.getSource());
      }
    }
    recipes.add(recipe);
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    for (AbstractConditional recipe : recipes) {
      recipe.enforceValidity();
    }
  }

}
