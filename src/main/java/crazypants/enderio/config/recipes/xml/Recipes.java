package crazypants.enderio.config.recipes.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.Log;
import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.RecipeRoot;
import crazypants.enderio.config.recipes.StaxFactory;

public class Recipes implements RecipeRoot {

  private List<Alias> aliases;

  private List<Recipe> recipes;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    return this;
  }

  @Override
  public boolean isValid() {
    return recipes != null || aliases != null;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Override
  public void register() {
    Log.debug("Starting registering XML recipes");
    if (recipes != null) {
      for (Recipe recipe : recipes) {
        recipe.register();
      }
    }
    Log.debug("Done registering XML recipes");
  }

  void initEmpty() {
    recipes = new ArrayList<Recipe>();
  }

  @Override
  public void addRecipes(RecipeRoot other) {
    if (other instanceof Recipes && ((Recipes) other).recipes != null) {
      if (recipes != null) {
        Set<String> recipeNames = new HashSet<String>();
        for (Recipe recipe : recipes) {
          recipeNames.add(recipe.getName());
        }

        for (Recipe recipe : ((Recipes) other).recipes) {
          if (!recipeNames.contains(recipe.getName())) {
            recipes.add(recipe);
          }
        }
      } else {
        recipes = ((Recipes) other).recipes;
      }
    }
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
      if (aliases == null) {
        aliases = new ArrayList<Alias>();
      }
      aliases.add(factory.read(new Alias(), startElement));
      return true;
    }
    if ("recipe".equals(name)) {
      if (recipes == null) {
        recipes = new ArrayList<Recipe>();
      }
      recipes.add(factory.read(new Recipe(), startElement));
      return true;
    }

    return false;
  }

}
