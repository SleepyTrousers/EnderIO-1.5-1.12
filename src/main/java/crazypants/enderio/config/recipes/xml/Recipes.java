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

  @SuppressWarnings("unchecked")
  @Override
  public <T extends RecipeRoot> T addRecipes(RecipeRoot other) {
    if (other instanceof Recipes) {
      if (((Recipes) other).recipes == null) {
        // NOP
      } else if (recipes != null) {
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

      if (((Recipes) other).aliases == null) {
        // NOP
      } else if (aliases != null) {
        Set<String> aliasNames = new HashSet<String>();
        for (Alias alias : aliases) {
          aliasNames.add(alias.getName());
        }

        for (Alias alias : ((Recipes) other).aliases) {
          if (!aliasNames.contains(alias.getName())) {
            aliases.add(alias);
          }
        }
      } else {
        aliases = ((Recipes) other).aliases;
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
      if (aliases == null) {
        aliases = new ArrayList<Alias>();
      }
      aliases.add(factory.read(new Alias(), startElement));
      return true;
    }
    if ("recipe".equals(name)) {
      final Recipe recipe = factory.read(new Recipe(), startElement);
      if (recipes == null) {
        recipes = new ArrayList<Recipe>();
      } else {
        for (Recipe existingRecipe : recipes) {
          if (existingRecipe.getName().equals(recipe.getName())) {
            throw new InvalidRecipeConfigException("Duplicate recipe " + recipe.getName());
          }
        }
      }
      recipes.add(recipe);
      return true;
    }

    return false;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (recipes != null) {
      for (Recipe recipe : recipes) {
        recipe.enforceValidity();
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends RecipeRoot> T copy(T in) {
    if (in instanceof Recipes) {
      Recipes result = new Recipes();
      if (this.aliases != null) {
        result.aliases = new ArrayList<Alias>(this.aliases);
      }
      if (this.recipes != null) {
        result.recipes = new ArrayList<Recipe>(this.recipes);
      }
      return (T) result;
    } else {
      return null;
    }
  }

}
