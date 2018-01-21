package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class Output extends AbstractConditional {

  private int amount;

  private String nbt;

  private Item item;

  private boolean required = true;

  private transient NBTTagCompound tag;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (item == null) {
      throw new InvalidRecipeConfigException("Missing name in <output>");
    }
    if (item.isValid()) {
      if (amount < 0) {
        throw new InvalidRecipeConfigException("Invalid negative amount in <output>");
      }
      if (amount > item.getItemStack().getMaxStackSize()) {
        throw new InvalidRecipeConfigException("Invalid amount in <output>, bigger than maximum stack size");
      }
      if (amount == 0) {
        amount = 1;
      }
    }
    final String nbt_nullchecked = nbt;
    if (nbt_nullchecked != null) {
      if (nbt_nullchecked.trim().isEmpty()) {
        tag = null;
      } else {
        try {
          tag = JsonToNBT.getTagFromJson(nbt_nullchecked);
        } catch (NBTException e) {
          throw new InvalidRecipeConfigException(nbt_nullchecked + " is not valid NBT json.");
        }
      }
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    item.enforceValidity();
  }

  @Override
  public boolean isValid() {
    return item != null && item.isValid();
  }

  @Override
  public boolean isActive() {
    return super.isActive() && (required || item.isValid());
  }

  public @Nonnull ItemStack getItemStack() {
    ItemStack itemStack = item.getItemStack().copy();
    itemStack.setCount(amount);
    if (tag != null) {
      itemStack.setTagCompound(tag);
    }
    return itemStack;
  }

  public boolean hasAlternatives() {
    return item.getThing().getItemStacks().size() > 1;
  }

  public @Nonnull NNList<ItemStack> getAlternatives() {
    NNList<ItemStack> list = item.getThing().getItemStacks().copy();
    list.remove(0);
    for (ItemStack itemStack : list) {
      itemStack.setCount(amount);
      if (tag != null) {
        itemStack.setTagCompound(tag);
      }
    }
    return list;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("amount".equals(name)) {
      this.amount = Integer.valueOf(value);
      return true;
    }
    if ("nbt".equals(name)) {
      this.nbt = value;
      return true;
    }
    if ("name".equals(name)) {
      item = new Item();
      item.setName(value);
      item.readResolve();
      return true;
    }
    if ("required".equals(name)) {
      this.required = Boolean.parseBoolean(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public void register() {
  }

}