package crazypants.enderio.base.config.recipes.xml;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;

public class ItemOptional implements RecipeConfigElement {

  protected String name;
  protected transient @Nonnull ItemStack stack = Prep.getEmpty();
  protected transient Object recipeObject;
  protected transient boolean nullItem;
  protected transient final @Nonnull Things thing = new Things();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (name == null || name.trim().isEmpty()) {
      stack = Prep.getEmpty();
      recipeObject = null;
      nullItem = true;
      return this;
    }
    thing.add(name);
    NNList<ItemStack> itemStacks = thing.getItemStacksRaw();
    stack = itemStacks.isEmpty() ? Prep.getEmpty() : itemStacks.get(0);
    List<Object> recipeObjects = thing.getRecipeObjects();
    if (recipeObjects.size() > 1) {
      throw new InvalidRecipeConfigException("Name \"" + name + "\"> references " + recipeObjects.size() + " different things: " + recipeObjects);
    }
    recipeObject = recipeObjects.isEmpty() ? ItemStack.EMPTY : recipeObjects.get(0);
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      throw new InvalidRecipeConfigException(
          "Could not find a crafting ingredient for '" + name + "' (stack=" + friendlyName(stack) + ", object=" + friendlyName(recipeObject) + ")");
    }
  }

  private String friendlyName(Object o) {
    return o == null || (o instanceof ItemStack && Prep.isInvalid((ItemStack) o)) ? "empty" : o.toString();
  }

  @Override
  public boolean isValid() {
    return nullItem || (Prep.isValid(stack) && recipeObject != null);
  }

  public Object getRecipeObject() {
    return recipeObject;
  }

  public @Nonnull ItemStack getItemStack() {
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

  public @Nonnull Things getThing() {
    return thing;
  }

}