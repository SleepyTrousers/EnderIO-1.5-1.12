package crazypants.enderio.base.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.item.crafting.Ingredient;

public class Shapeless implements RecipeConfigElement {

  private List<Item> items;

  private transient boolean valid;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      if (items == null || items.isEmpty()) {
        throw new InvalidRecipeConfigException("Not enough items");
      }
      if (items.size() > 9) {
        throw new InvalidRecipeConfigException("Too many items");
      }
      // Sorry mezz, no "Just enough items" exception
      valid = true;
      for (Item item : items) {
        valid = valid && item.isValid();
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <Shapeless>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    for (Item item : items) {
      item.enforceValidity();
    }
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  public @Nonnull NNList<Ingredient> getIngredients() {
    NNList<Ingredient> result = new NNList<>();

    for (ItemOptional item : items) {
      Ingredient ingredient = item.getRecipeObject();
      if (ingredient == null) {
        result.add(Ingredient.EMPTY);
      } else {
        result.add(ingredient);
      }
    }

    return result;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("item".equals(name)) {
      if (items == null) {
        items = new ArrayList<Item>();
      }
      items.add(factory.read(new Item(), startElement));
      return true;
    }

    return false;
  }

}