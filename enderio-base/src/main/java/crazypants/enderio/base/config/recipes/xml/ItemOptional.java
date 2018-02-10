package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

public class ItemOptional implements RecipeConfigElement {

  protected String name;
  protected String nbt;
  protected transient boolean nullItem;
  protected transient final @Nonnull Things thing = new Things();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (name == null || name.trim().isEmpty()) {
      nullItem = true;
      return this;
    }
    thing.add(name);
    final String nbt_nullchecked = nbt;
    if (nbt_nullchecked != null) {
      if (!nbt_nullchecked.trim().isEmpty()) {
        try {
          thing.setNbt(JsonToNBT.getTagFromJson(nbt_nullchecked));
        } catch (NBTException e) {
          throw new InvalidRecipeConfigException(nbt_nullchecked + " is not valid NBT json.");
        }
      }
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      throw new InvalidRecipeConfigException("Could not find a crafting ingredient for '" + name);
    }
  }

  @Override
  public boolean isValid() {
    return nullItem || (thing.isPotentiallyValid());
  }

  public Ingredient getRecipeObject() {
    return nullItem ? null : thing.asIngredient();
  }

  public @Nonnull ItemStack getItemStack() {
    ItemStack itemStack = thing.getItemStack();
    itemStack.setCount(1);
    return itemStack;
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
    if ("nbt".equals(name)) {
      this.nbt = value;
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