package crazypants.enderio.config.recipes.xml;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import crazypants.enderio.Log;
import crazypants.util.Things;
import net.minecraft.item.ItemStack;

public class Item implements RecipeConfigElement {

  @XStreamAlias("name")
  @XStreamAsAttribute
  private String name;

  @XStreamOmitField
  private ItemStack stack;

  @XStreamOmitField
  private Object recipeObject;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (name == null || name.trim().isEmpty()) {
      throw new InvalidRecipeConfigException("Missing name in <item>");
    }
    Things thing = new Things(name);
    List<ItemStack> itemStacks = thing.getItemStacksRaw();
    stack = itemStacks.isEmpty() ? null : itemStacks.get(0);
    List<Object> recipeObjects = thing.getRecipeObjects();
    if (recipeObjects.size() > 1) {
      throw new InvalidRecipeConfigException("<item name=\"" + name + "\"> references " + itemStacks.size() + " different things: " + recipeObjects);
    }
    recipeObject = recipeObjects.isEmpty() ? null : recipeObjects.get(0);
    if (!isValid()) {
      Log.info("Could not find a crafting ingredient for '" + name + "' (stack=" + stack + ", object=" + recipeObject + ")");
    }
    return this;
  }

  @Override
  public boolean isValid() {
    return stack != null && recipeObject != null;
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

}