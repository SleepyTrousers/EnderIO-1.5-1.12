package crazypants.enderio.config.recipes.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.Log;
import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.RecipeConfigElement;
import crazypants.enderio.config.recipes.StaxFactory;
import crazypants.util.Things;
import net.minecraft.item.ItemStack;

public class OptionalItem implements RecipeConfigElement {

  private String name;
  private transient ItemStack stack;
  private transient Object recipeObject;
  protected transient boolean valid;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (name == null || name.trim().isEmpty()) {
      stack = null;
      recipeObject = null;
      valid = true;
      return this;
    }
    Things thing = new Things(name);
    List<ItemStack> itemStacks = thing.getItemStacksRaw();
    stack = itemStacks.isEmpty() ? null : itemStacks.get(0);
    List<Object> recipeObjects = thing.getRecipeObjects();
    if (recipeObjects.size() > 1) {
      throw new InvalidRecipeConfigException("Name \"" + name + "\"> references " + itemStacks.size() + " different things: " + recipeObjects);
    }
    recipeObject = recipeObjects.isEmpty() ? null : recipeObjects.get(0);
    if (!isValid()) {
      Log.info("Could not find a crafting ingredient for '" + name + "' (stack=" + stack + ", object=" + recipeObject + ")");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return valid || (stack != null && recipeObject != null);
  }

  public Object getRecipeObject() {
    return recipeObject;
  }

  public ItemStack getItemStack() {
    return stack;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = value;
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

}