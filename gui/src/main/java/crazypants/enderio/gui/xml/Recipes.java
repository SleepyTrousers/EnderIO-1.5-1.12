package crazypants.enderio.gui.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public class Recipes implements IRecipeRoot {

  protected final @Nonnull List<AbstractConditional> recipes = new ArrayList<AbstractConditional>();

  @Override
  public @Nonnull List<AbstractConditional> getRecipes() {
    return recipes;
  }

  @Override
  public boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value)
      throws InvalidRecipeConfigException, XMLStreamException {
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
  public boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    AbstractConditional element = get(name);
    if (element != null) {
      addRecipe(element, factory, startElement);
      return true;
    }

    return false;
  }

  protected <T extends AbstractConditional> void addRecipe(T element, StaxFactory factory, StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException {
    final AbstractConditional recipe = factory.read(element, startElement);
    if (!recipe.supportsDuplicates()) {
      for (AbstractConditional existingRecipe : recipes) {
        if (existingRecipe.getName().equals(recipe.getName())) {
          throw new InvalidRecipeConfigException(
              "Duplicate recipe '" + recipe.getName() + "'. A recipe with the same name was already read from " + existingRecipe.getSource());
        }
      }
    }
    recipes.add(recipe);
  }

  @Override
  public void validate() throws InvalidRecipeConfigException {
  }

  @Override
  @Nonnull
  public ElementList getSubElements() {
    return IRecipeRoot.super.getSubElements().add(recipes);
  }

  private static final Map<String, Class<? extends AbstractConditional>> MAPPING = new HashMap<>();

  static {
    register(Recipe.class, Grindingball.class, Capacitor.class, Alias.class);
  }

  public static void register(String tagname, Class<? extends AbstractConditional> clazz) {
    MAPPING.put(tagname, clazz);
  }

  @SafeVarargs
  public static void register(Class<? extends AbstractConditional>... clazzes) {
    for (Class<? extends AbstractConditional> clazz : clazzes) {
      MAPPING.put(clazz.getSimpleName().toLowerCase(Locale.ENGLISH), clazz);
    }
  }

  public static @Nullable AbstractConditional get(String tagname) {
    Class<? extends AbstractConditional> clazz = MAPPING.get(tagname);
    if (clazz != null) {
      try {
        return clazz.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  @Override
  public void write(@Nonnull IXMLBuilder parent) {
    IXMLBuilder ƒ = parent.child("enderio:recipes");
    ƒ.attribute("xmlns:enderio", "http://enderio.com/recipes");
    ƒ.attribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    ƒ.attribute("xsi:schemaLocation", "http://enderio.com/recipes recipes.xsd ");
    recipes.forEach(recipe -> recipe.write(ƒ));
  }

}
