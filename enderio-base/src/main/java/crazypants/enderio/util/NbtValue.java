package crazypants.enderio.util;

import crazypants.enderio.base.EnderIO;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Locale;

public enum NbtValue { // TODO: DONE111
  GLINT("glinted"),
  CAPNAME("capname"),
  CAPNO("capno"),
  /**
   * Used on item stacks to signal the renderer that the stack is not real but is used as a GUI element. The effects are specific to the item, e.g. the tank
   * only renders the fluid.
   */
  FAKE("fake"),
  REMOTE_X("x"),
  REMOTE_Y("y"),
  REMOTE_Z("z"),
  REMOTE_D("d"),
  REMOTE_POS("pos"),
  REMOTE_NAME("name"),
  REMOTE_ICON("icon"),
  ENERGY("Energy"),
  FLUIDAMOUNT("famount"),
  BLOCKSTATE("paint"),
  DISPLAYMODE("displaymode"),
  MAGNET_ACTIVE("magnetActive"),
  LAST_USED_TICK("lastUsedAt"),
  FILTER("filter"),
  FILTER_CLASS("class"),
  FILTER_BLACKLIST("isBlacklist"),
  FILTER_META("matchMeta"),
  FILTER_NBT("matchNBT"),
  FILTER_OREDICT("useOreDict"),
  FILTER_STICKY("sticky"),
  FILTER_ADVANCED("isAdvanced"),
  FILTER_LIMITED("isLimited"),
  FILTER_DAMAGE("damageMode"),
  CONDUIT("conduit"),

  ;

  private final @Nonnull String key;

  private NbtValue(@Nonnull String key) {
    this.key = EnderIO.DOMAIN + ":" + key.toLowerCase(Locale.ENGLISH);
  }

  public @Nonnull String getKey() {
    return key;
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // ITEMSTACK STRING
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

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

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // ITEMSTACK INT
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

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

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // ITEMSTACK LONG
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

  @SuppressWarnings("null")
  public long getLong(@Nonnull ItemStack stack, long _default) {
    if (Prep.isValid(stack) && stack.hasTagCompound() && stack.getTagCompound().hasKey(key)) {
      return stack.getTagCompound().getLong(key);
    }
    return _default;
  }

  public long getLong(@Nonnull ItemStack stack) {
    return getLong(stack, 0L);
  }

  @SuppressWarnings("null")
  public @Nonnull ItemStack setLong(@Nonnull ItemStack stack, long value) {
    if (Prep.isValid(stack)) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      stack.getTagCompound().setLong(key, value);
    }
    return stack;
  }

  public @Nonnull ItemStack setLongCopy(@Nonnull ItemStack stack, long value) {
    return setLong(stack.copy(), value);
  }

  public @Nonnull ItemStack setLongCopy(@Nonnull ItemStack stack, long value, int stackSize) {
    final ItemStack stack2 = setLongCopy(stack, value);
    stack2.setCount(stackSize);
    return stack2;
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // ITEMSTACK BOOL
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

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

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // ITEMSTACK TAGCOMPOUND
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

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

  @SuppressWarnings("null")
  public @Nonnull NBTTagCompound getTag(@Nonnull ItemStack tag) {
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

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // NBT STRING
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

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

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // NBT INT
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

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

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // NBT LONG
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

  public long getLong(@Nullable NBTTagCompound tag, long _default) {
    if (tag != null && tag.hasKey(key)) {
      return tag.getLong(key);
    }
    return _default;
  }

  public long getLong(@Nullable NBTTagCompound tag) {
    return getLong(tag, 0);
  }

  public @Nullable NBTTagCompound setLong(@Nullable NBTTagCompound tag, long value) {
    if (tag != null) {
      tag.setLong(key, value);
    }
    return tag;
  }

  public @Nullable NBTTagCompound setLongCopy(@Nullable NBTTagCompound tag, long value) {
    return tag != null ? setLong(tag.copy(), value) : null;
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // NBT BOOL
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

  public boolean getBoolean(@Nullable NBTTagCompound tag, boolean _default) {
    if (tag != null && tag.hasKey(key)) {
      return tag.getBoolean(key);
    }
    return _default;
  }

  public boolean getBoolean(@Nullable NBTTagCompound tag) {
    return getBoolean(tag, false);
  }

  public @Nullable NBTTagCompound setBoolean(@Nullable NBTTagCompound tag, boolean value) {
    if (tag != null) {
      tag.setBoolean(key, value);
    }
    return tag;
  }

  public @Nullable NBTTagCompound setBooleanCopy(@Nullable NBTTagCompound tag, boolean value) {
    return tag != null ? setBoolean(tag.copy(), value) : null;
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // NBT BLOCKPOS
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

  public @Nonnull BlockPos getBlockPos(@Nullable NBTTagCompound tag, @Nonnull BlockPos _default) {
    return BlockPos.fromLong(getLong(tag, _default.toLong()));
  }

  public @Nonnull BlockPos getBlockPos(@Nullable NBTTagCompound tag) {
    return BlockPos.fromLong(getLong(tag, 0L));
  }

  public @Nullable NBTTagCompound setBlockPos(@Nullable NBTTagCompound tag, @Nonnull BlockPos value) {
    return setLong(tag, value.toLong());
  }

  public @Nullable NBTTagCompound setBlockPosCopy(@Nullable NBTTagCompound tag, @Nonnull BlockPos value) {
    return setLongCopy(tag, value.toLong());
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // NBT ITEMSTACK
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

  public @Nonnull ItemStack getStack(@Nullable NBTTagCompound tag, @Nonnull ItemStack _default) {
    if (tag != null && tag.hasKey(key)) {
      return new ItemStack(tag.getCompoundTag(key));
    }
    return _default;
  }

  public @Nonnull ItemStack getStack(@Nullable NBTTagCompound tag) {
    return getStack(tag, Prep.getEmpty());
  }

  public @Nullable NBTTagCompound setStack(@Nullable NBTTagCompound tag, @Nonnull ItemStack value) {
    if (tag != null) {
      tag.setTag(key, value.writeToNBT(new NBTTagCompound()));
    }
    return tag;
  }

  public @Nullable NBTTagCompound setStackCopy(@Nullable NBTTagCompound tag, @Nonnull ItemStack value) {
    return tag != null ? setStack(tag.copy(), value) : null;
  }

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // NBT TAGCOMPOUND
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

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

  // /////////////////////////////////////////////////////////////////////////////////////////////////////
  // ITEMSTACK ROOT
  // /////////////////////////////////////////////////////////////////////////////////////////////////////

  @SuppressWarnings("null")
  public static @Nonnull NBTTagCompound getRoot(@Nonnull ItemStack stack) {
    if (Prep.isValid(stack)) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      return stack.getTagCompound();
    }
    return new NBTTagCompound();
  }

}
