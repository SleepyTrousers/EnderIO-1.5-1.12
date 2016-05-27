package crazypants.enderio.config.recipes.xml;

import javax.xml.stream.XMLStreamException;

import crazypants.enderio.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.config.recipes.StaxFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class Output extends Item {

  private int amount;

  private String nbt;

  private transient NBTTagCompound tag;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (super.isValid()) {
      if (amount < 0) {
        throw new InvalidRecipeConfigException("Invalid negative amount in <output>");
      }
      if (amount > super.getItemStack().getMaxStackSize()) {
        throw new InvalidRecipeConfigException("Invalid amount in <output>, bigger than maximum stack size");
      }
      if (amount == 0) {
        amount = 1;
      }
    }
    if (nbt != null) {
      if (nbt.trim().isEmpty()) {
        tag = null;
      } else {
        try {
          final String nbt2 = nbt;
          if (nbt2 != null) {
            tag = JsonToNBT.getTagFromJson(nbt2);
          } else {
            // TODO handle null value
          }
        } catch (NBTException e) {
          throw new InvalidRecipeConfigException(nbt + " is not valid NBT json.");
        }
      }
    }
    return this;
  }

  @Override
  public ItemStack getItemStack() {
    ItemStack itemStack = super.getItemStack().copy();
    itemStack.stackSize = amount;
    if (tag != null) {
      itemStack.setTagCompound(tag);
    }
    return itemStack;
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

    return super.setAttribute(factory, name, value);
  }

}