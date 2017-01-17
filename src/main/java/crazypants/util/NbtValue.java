package crazypants.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public enum NbtValue {
  @Deprecated
  SOURCE_BLOCK("sourceBlockId"),
  @Deprecated
  SOURCE_META("sourceBlockMeta"),
  GLINT("glinted"),
  CAPNAME("capname"),
  CAPNO("capno"),
  /**
   * Used on item stacks to signal the renderer that the stack is not real but is used as a GUI element. The effects are specific to the item, e.g. the tank
   * only renders the fluid.
   */
  FAKE("fake"),
  REMOTE_X("eiox"),
  REMOTE_Y("eioy"),
  REMOTE_Z("eioz"),
  REMOTE_D("eiod"),
  ENERGY("Energy"),
  FLUIDAMOUNT("famount"),
  BLOCKSTATE("paint"),

  ;

  private final String key;

  private NbtValue(String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  // ITEMSTACK STRING

  public String getString(ItemStack stack, String _default) {
    if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      return stack.getTagCompound().getString(key);
    }
    return _default;
  }

  public String getString(ItemStack stack) {
    return getString(stack, "");
  }

  public ItemStack setString(ItemStack stack, String value) {
    if (stack != null && value != null) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      stack.getTagCompound().setString(key, value);
    } else {
      removeTag(stack);
    }
    return stack;
  }

  public ItemStack setStringCopy(ItemStack stack, String value) {
    return stack != null ? setString(stack.copy(), value) : null;
  }

  public ItemStack setStringCopy(ItemStack stack, String value, int stackSize) {
    if (stack != null) {
      final ItemStack stack2 = setString(stack.copy(), value);
      stack2.stackSize = stackSize;
      return stack2;
    } else {
      return null;
    }
  }

  // ITEMSTACK INT

  public int getInt(ItemStack stack, int _default) {
    if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      return stack.getTagCompound().getInteger(key);
    }
    return _default;
  }

  public int getInt(ItemStack stack) {
    return getInt(stack, 0);
  }

  public ItemStack setInt(ItemStack stack, int value) {
    if (stack != null) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      stack.getTagCompound().setInteger(key, value);
    }
    return stack;
  }

  public ItemStack setIntCopy(ItemStack stack, int value) {
    return stack != null ? setInt(stack.copy(), value) : null;
  }

  public ItemStack setIntCopy(ItemStack stack, int value, int stackSize) {
    if (stack != null) {
      final ItemStack stack2 = setInt(stack.copy(), value);
      stack2.stackSize = stackSize;
      return stack2;
    } else {
      return null;
    }
  }

  // ITEMSTACK

  public boolean hasTag(ItemStack stack) {
    return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(key);
  }

  public ItemStack removeTag(ItemStack stack) {
    if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      stack.getTagCompound().removeTag(key);
    }
    return stack;
  }

  public ItemStack removeTagCopy(ItemStack stack) {
    return stack != null ? removeTag(stack.copy()) : null;
  }

  public NBTTagCompound getTag(ItemStack tag) {
    return getTag(tag, new NBTTagCompound());
  }

  public NBTTagCompound getTag(ItemStack stack, NBTTagCompound _default) {
    if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      return (NBTTagCompound) stack.getTagCompound().getTag(key);
    }
    setTag(stack, _default);
    return _default;
  }

  public ItemStack setTag(ItemStack stack, NBTTagCompound value) {
    if (stack != null) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      if (value == null) {
        removeTag(stack);
      } else {
        stack.getTagCompound().setTag(key, value);
      }
    }
    return stack;
  }

  // NBT STRING

  public String getString(NBTTagCompound tag, String _default) {
    if (tag != null && tag.hasKey(key)) {
      return tag.getString(key);
    }
    return _default;
  }

  public String getString(NBTTagCompound tag) {
    return getString(tag, "");
  }

  public NBTTagCompound setString(NBTTagCompound tag, String value) {
    if (tag != null && value != null) {
      tag.setString(key, value);
    } else {
      removeTag(tag);
    }
    return tag;
  }

  public NBTTagCompound setStringCopy(NBTTagCompound tag, String value) {
    return tag != null ? setString((NBTTagCompound) tag.copy(), value) : null;
  }

  // NBT INT

  public int getInt(NBTTagCompound tag, int _default) {
    if (tag != null && tag.hasKey(key)) {
      return tag.getInteger(key);
    }
    return _default;
  }

  public int getInt(NBTTagCompound tag) {
    return getInt(tag, 0);
  }

  public NBTTagCompound setInt(NBTTagCompound tag, int value) {
    if (tag != null) {
      tag.setInteger(key, value);
    }
    return tag;
  }

  public NBTTagCompound setIntCopy(NBTTagCompound tag, int value) {
    return tag != null ? setInt((NBTTagCompound) tag.copy(), value) : null;
  }

  // NBT

  public boolean hasTag(NBTTagCompound tag) {
    return tag != null && tag.hasKey(key);
  }

  public NBTTagCompound removeTag(NBTTagCompound tag) {
    if (tag != null && tag.hasKey(key)) {
      tag.removeTag(key);
    }
    return tag;
  }

  public NBTTagCompound removeTagCopy(NBTTagCompound tag) {
    return tag != null ? removeTag((NBTTagCompound) tag.copy()) : null;
  }

  public NBTTagCompound getTag(NBTTagCompound tag) {
    return getTag(tag, new NBTTagCompound());
  }

  public NBTTagCompound getTag(NBTTagCompound tag, NBTTagCompound _default) {
    if (tag != null && tag.hasKey(key)) {
      return (NBTTagCompound) tag.getTag(key);
    }
    setTag(tag, _default);
    return _default;
  }

  public NBTTagCompound setTag(NBTTagCompound tag, NBTTagCompound value) {
    if (tag != null) {
      if (value == null) {
        removeTag(tag);
      } else {
        tag.setTag(key, value);
      }
    }
    return tag;
  }

}
