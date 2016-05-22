package crazypants.enderio.config.recipes.xml;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class Grid implements RecipeConfigElement {

  @XStreamAsAttribute
  @XStreamAlias("size")
  private String size;

  @XStreamImplicit(itemFieldName = "item")
  private List<Item> items;

  @XStreamOmitField
  private int width;
  @XStreamOmitField
  private int height;
  @XStreamOmitField
  private boolean valid;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      if (size != null && !size.trim().isEmpty() && !"3x3".equals(size.trim())) {
        if (size.trim().length() != 3 || size.charAt(1) != 'x') {
          throw new InvalidRecipeConfigException("Invalid size attribute '" + size.trim());
        }
        String widthString = size.substring(0, 1);
        String heightString = size.substring(2, 3);
        try {
          width = Integer.parseInt(widthString);
          height = Integer.parseInt(heightString);
        } catch (NumberFormatException e) {
          throw new InvalidRecipeConfigException("Invalid size attribute '" + size.trim());
        }
        if (width < 1 || width > 3 || height < 1 || height > 3) {
          throw new InvalidRecipeConfigException("Invalid size attribute '" + size.trim());
        }
      } else {
        width = height = 3;
      }

      if (items == null) {
        throw new InvalidRecipeConfigException("No items");
      }
      if (items.isEmpty() || items.size() < width * height) {
        throw new InvalidRecipeConfigException("Not enough items (required=" + (width * height) + ", provided=" + items.size() + ")");
      }
      if (items.size() > width * height) {
        throw new InvalidRecipeConfigException("Too many items (required=" + (width * height) + ", provided=" + items.size() + ")");
      }
      valid = true;
      for (Item item : items) {
        valid = valid && item.isValid();
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <grid>");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  public Object[] getElements() {
    List<Object> rowStrings = new ArrayList<Object>();
    List<Object> elements = new ArrayList<Object>();

    char letter = 'A';
    for (int row = 0; row < height; row++) {
      String rowString = "";
      for (int col = 0; col < width; col++) {
        int idx = row * width + col;
        Object recipeObject = items.get(idx).getRecipeObject();
        if (recipeObject == null) {
          rowString += " ";
        } else {
          rowString += letter;
          elements.add(letter);
          elements.add(recipeObject);
          letter++;
        }
      }
      rowStrings.add(rowString);
    }

    rowStrings.addAll(elements);
    return rowStrings.toArray();
  }

}