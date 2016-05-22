package crazypants.enderio.config.recipes.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class Dependency implements RecipeConfigElement {

  @XStreamAsAttribute
  @XStreamAlias("item")
  private String itemString;
  @XStreamAsAttribute
  @XStreamAlias("reverse")
  private boolean reverse;

  private Item item;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      if (itemString == null || itemString.trim().isEmpty()) {
        throw new InvalidRecipeConfigException("Missing item");
      }
      item = new Item();
      item.setName(itemString);
      item.readResolve();
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <dependency>");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return item.isValid() != reverse;
  }

}