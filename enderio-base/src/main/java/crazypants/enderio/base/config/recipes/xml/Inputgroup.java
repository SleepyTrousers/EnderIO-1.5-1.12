package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;

public class Inputgroup implements RecipeConfigElement {

  private NNList<ItemMultiplier> inputs = new NNList<>();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (inputs.isEmpty()) {
      throw new InvalidRecipeConfigException("Missing <input> in <inputgroup>");
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    for (Item item : inputs) {
      if (item.isValid()) {
        item.enforceValidity();
        return;
      }
    }
    throw new InvalidRecipeConfigException("No valid <input> in <inputgroup>");
  }

  @Override
  public boolean isValid() {
    for (Item item : inputs) {
      if (item.isValid()) {
        return true;
      }
    }
    return false;
  }

  public @Nonnull NNList<ItemMultiplier> getItems() {
    NNList<ItemMultiplier> result = new NNList<>();
    for (ItemMultiplier item : inputs) {
      if (item.isValid()) {
        result.add(item);
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
    if ("input".equals(name)) {
      inputs.add(factory.read(new ItemMultiplier().setAllowDelaying(false), startElement));
      return true;
    }
    return false;
  }

}