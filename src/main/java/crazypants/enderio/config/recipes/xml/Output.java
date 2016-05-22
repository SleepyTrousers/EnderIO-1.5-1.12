package crazypants.enderio.config.recipes.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class Output extends Item {

  @XStreamAsAttribute
  @XStreamAlias("amount")
  private int amount;

  @XStreamAsAttribute
  @XStreamAlias("nbt")
  private String nbt;

  @XStreamOmitField
  private NBTTagCompound tag;

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
          tag = (NBTTagCompound) JsonToNBT.getTagFromJson(nbt);
        } catch (NBTException e) {
          throw new InvalidRecipeConfigException(nbt + " is not valid NBT json.");
        }
      }
    }
    return this;
  }

  public ItemStack getItemStack() {
    ItemStack itemStack = super.getItemStack().copy();
    itemStack.stackSize = amount;
    if (tag != null) {
      itemStack.setTagCompound(tag);
    }
    return itemStack;
  }

}