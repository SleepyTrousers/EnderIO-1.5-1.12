package crazypants.enderio.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class Shapeless implements RecipeConfigElement {

  @XStreamImplicit(itemFieldName = "item")
  private List<Item> items;

  @XStreamOmitField
  private boolean valid;

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
  public boolean isValid() {
    return valid;
  }

  public Object[] getElements() {
    List<Object> elements = new ArrayList<Object>();

    for (Item item : items) {
      elements.add(item.getRecipeObject());
    }

    return elements.toArray();
  }

}