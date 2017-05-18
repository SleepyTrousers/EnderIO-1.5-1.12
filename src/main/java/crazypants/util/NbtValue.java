package crazypants.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public enum NbtValue { // TODO: DONE111
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
  DISPLAYMODE("enderio.displaymode"),
  MAGNET_ACTIVE("magnetActive"),

  ;

  private final @Nonnull String key;

  private NbtValue(@Nonnull String key) {
    this.key = key;
  }

  public @Nonnull String getKey() {
    return key;
  }

  // ITEMSTACK STRING

  @SuppressWarnings("null")
  public @Nonnull String getString(@Nonnull ItemStack stack, @Nonnull String _default) {
    if (Prep.isValid(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      return stack.getTagCompound().getString(key);
    }
    return _default;
  }

  public String getString(@Nonnull ItemStack stack) {
    return getString(stack, "");
  }

  @SuppressWarnings("null")
  public @Nonnull ItemStack setString(@Nonnull ItemStack stack, String value) {
    if (Prep.isValid(stack) && value != null) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      stack.getTagCompound().setString(key, value);
    } else {
      removeTag(stack);
    }
    return stack;
  }

  public @Nonnull ItemStack setStringCopy(@Nonnull ItemStack stack, String value) {
    return setString(stack.copy(), value);
  }

  public @Nonnull ItemStack setStringCopy(@Nonnull ItemStack stack, String value, int stackSize) {
    final ItemStack stack2 = setStringCopy(stack, value);
    stack2.setCount(stackSize);
      return stack2;
  }

  // ITEMSTACK INT

  @SuppressWarnings("null")
  public int getInt(@Nonnull ItemStack stack, int _default) {
    if (Prep.isValid(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      return stack.getTagCompound().getInteger(key);
    }
    return _default;
  }

  public int getInt(@Nonnull ItemStack stack) {
    return getInt(stack, 0);
  }

  @SuppressWarnings("null")
  public @Nonnull ItemStack setInt(@Nonnull ItemStack stack, int value) {
    if (Prep.isValid(stack)) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      stack.getTagCompound().setInteger(key, value);
    }
    return stack;
  }

  public @Nonnull ItemStack setIntCopy(@Nonnull ItemStack stack, int value) {
    return setInt(stack.copy(), value);
  }

  public @Nonnull ItemStack setIntCopy(@Nonnull ItemStack stack, int value, int stackSize) {
    final ItemStack stack2 = setIntCopy(stack, value);
    stack2.setCount(stackSize);
      return stack2;
  }

  // ITEMSTACK BOOL

  @SuppressWarnings("null")
  public boolean getBoolean(@Nonnull ItemStack stack, boolean _default) {
    if (Prep.isValid(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      return stack.getTagCompound().getBoolean(key);
    }
    return _default;
  }

  public boolean getBoolean(@Nonnull ItemStack stack) {
    return getBoolean(stack, false);
  }

  @SuppressWarnings("null")
  public @Nonnull ItemStack setBoolean(@Nonnull ItemStack stack, boolean value) {
    if (Prep.isValid(stack)) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      stack.getTagCompound().setBoolean(key, value);
    }
    return stack;
  }

  public @Nonnull ItemStack setBooleanCopy(@Nonnull ItemStack stack, boolean value) {
    return setBoolean(stack.copy(), value);
  }

  public @Nonnull ItemStack setBooleanCopy(@Nonnull ItemStack stack, boolean value, int stackSize) {
    final ItemStack stack2 = setBooleanCopy(stack, value);
    stack2.setCount(stackSize);
    return stack2;
  }

  // ITEMSTACK

  @SuppressWarnings("null")
  public boolean hasTag(@Nonnull ItemStack stack) {
    return stack.hasTagCompound() && stack.getTagCompound().hasKey(key);
  }

  @SuppressWarnings("null")
  public @Nonnull ItemStack removeTag(@Nonnull ItemStack stack) {
    if (stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      stack.getTagCompound().removeTag(key);
    }
    return stack;
  }

  public @Nonnull ItemStack removeTagCopy(@Nonnull ItemStack stack) {
    return removeTag(stack.copy());
  }

  public NBTTagCompound getTag(@Nonnull ItemStack tag) {
    return getTag(tag, new NBTTagCompound());
  }

  @SuppressWarnings("null")
  public NBTTagCompound getTag(@Nonnull ItemStack stack, NBTTagCompound _default) {
    if (stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      return (NBTTagCompound) stack.getTagCompound().getTag(key);
    }
    setTag(stack, _default);
    return _default;
  }

  @SuppressWarnings("null")
  public @Nonnull ItemStack setTag(@Nonnull ItemStack stack, NBTTagCompound value) {
    if (Prep.isValid(stack)) {
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

  public @Nonnull String getString(@Nullable NBTTagCompound tag, @Nonnull String _default) {
    if (tag != null && tag.hasKey(key)) {
      return tag.getString(key);
    }
    return _default;
  }

  public @Nonnull String getString(@Nullable NBTTagCompound tag) {
    return getString(tag, "");
  }

  public @Nullable NBTTagCompound setString(@Nullable NBTTagCompound tag, @Nullable String value) {
    if (tag != null && value != null) {
      tag.setString(key, value);
    } else {
      removeTag(tag);
    }
    return tag;
  }

  public @Nullable NBTTagCompound setStringCopy(@Nullable NBTTagCompound tag, @Nullable String value) {
    return tag != null ? setString(tag.copy(), value) : null;
  }

  // NBT INT

  public int getInt(@Nullable NBTTagCompound tag, int _default) {
    if (tag != null && tag.hasKey(key)) {
      return tag.getInteger(key);
    }
    return _default;
  }

  public int getInt(@Nullable NBTTagCompound tag) {
    return getInt(tag, 0);
  }

  public @Nullable NBTTagCompound setInt(@Nullable NBTTagCompound tag, int value) {
    if (tag != null) {
      tag.setInteger(key, value);
    }
    return tag;
  }

  public @Nullable NBTTagCompound setIntCopy(@Nullable NBTTagCompound tag, int value) {
    return tag != null ? setInt(tag.copy(), value) : null;
  }

  // NBT

  public boolean hasTag(@Nullable NBTTagCompound tag) {
    return tag != null && tag.hasKey(key);
  }

  public @Nullable NBTTagCompound removeTag(@Nullable NBTTagCompound tag) {
    if (tag != null && tag.hasKey(key)) {
      tag.removeTag(key);
    }
    return tag;
  }

  public @Nullable NBTTagCompound removeTagCopy(@Nullable NBTTagCompound tag) {
    return tag != null ? removeTag(tag.copy()) : null;
  }

  public @Nullable NBTTagCompound getTag(@Nullable NBTTagCompound tag) {
    return getTag(tag, new NBTTagCompound());
  }

  public @Nullable NBTTagCompound getTag(@Nullable NBTTagCompound tag, @Nullable NBTTagCompound _default) {
    if (tag != null && tag.hasKey(key)) {
      return (NBTTagCompound) tag.getTag(key);
    }
    setTag(tag, _default);
    return _default;
  }

  public NBTTagCompound setTag(@Nullable NBTTagCompound tag, @Nullable NBTTagCompound value) {
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
