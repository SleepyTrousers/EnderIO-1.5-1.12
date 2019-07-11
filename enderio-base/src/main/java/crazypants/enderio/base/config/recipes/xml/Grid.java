package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.item.crafting.Ingredient;

public class Grid implements IRecipeConfigElement {

  private Optional<String> size = empty();

  private final NNList<ItemOptional> items = new NNList<>();

  private transient int width;
  private transient int height;
  private transient boolean valid;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    try {
      if (size.isPresent() && !"3x3".equals(size.get())) {
        if (size.get().length() != 3 || size.get().charAt(1) != 'x') {
          throw new InvalidRecipeConfigException("Invalid size attribute '" + size.get());
        }
        String widthString = size.get().substring(0, 1);
        String heightString = size.get().substring(2, 3);
        try {
          width = Integer.parseInt(widthString);
          height = Integer.parseInt(heightString);
        } catch (NumberFormatException e) {
          throw new InvalidRecipeConfigException("Invalid size attribute '" + size.get());
        }
        if (width < 1 || width > 3 || height < 1 || height > 3) {
          throw new InvalidRecipeConfigException("Invalid size attribute '" + size.get());
        }
      } else {
        width = height = 3;
      }

      if (items.isEmpty()) {
        throw new InvalidRecipeConfigException("No items");
      }
      if (items.size() < width * height) {
        throw new InvalidRecipeConfigException("Not enough items (required=" + (width * height) + ", provided=" + items.size() + ")");
      }
      if (items.size() > width * height) {
        throw new InvalidRecipeConfigException("Too many items (required=" + (width * height) + ", provided=" + items.size() + ")");
      }
      valid = true;
      for (ItemOptional item : items) {
        valid = valid && item.isValid();
      }
    } catch (InvalidRecipeConfigException e) {
      throw new InvalidRecipeConfigException(e, "in <grid>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    boolean hasAtLeastOneItem = false;
    for (ItemOptional item : items) {
      item.enforceValidity();
      hasAtLeastOneItem = hasAtLeastOneItem || item.getRecipeObject() != null;
    }
    if (!hasAtLeastOneItem) {
      throw new InvalidRecipeConfigException("Rejecting crafting recipe without any items, only empty spaces in <grid>");
    }
  }

  @Override
  public boolean isValid() {
    return valid;
  }

  public NNList<Ingredient> getIngredients() {
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
    if ("size".equals(name)) {
      this.size = ofString(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    if ("item".equals(name)) {
      items.add(factory.read(new ItemOptional().setAllowDelaying(false), startElement));
      return true;
    }

    return false;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

}