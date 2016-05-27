package crazypants.enderio.config.recipes.xml;

public interface RecipeConfigElement {

  Object readResolve() throws InvalidRecipeConfigException;

  boolean isValid();

}