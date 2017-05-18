package crazypants.enderio.capacitor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CapacitorHelper {

  private CapacitorHelper() {
  }

  public static @Nullable ICapacitorData getCapacitorDataFromItemStack(@Nonnull ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return null;
    }
    final ICapacitorData capData = getNBTCapacitorDataFromItemStack(stack);
    if (capData != null) {
      return capData;
    }
    if (stack.getItem() instanceof ICapacitorDataItem) {
      return ((ICapacitorDataItem) stack.getItem()).getCapacitorData(stack);
    }
    return null;
  }

  public static boolean isValidUpgrade(@Nonnull ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    final ICapacitorData capData = getNBTCapacitorDataFromItemStack(stack);
    if (capData != null) {
      return true;
    }
    if (stack.getItem() instanceof ICapacitorDataItem) {
      return true;
    }
    return false;
  }

  protected static @Nullable ICapacitorData getNBTCapacitorDataFromItemStack(@Nonnull ItemStack stack) {
    final NBTTagCompound nbtRoot = stack.getTagCompound();
    if (nbtRoot == null) {
      return null;
    }
    if (!nbtRoot.hasKey("eiocap", 10)) {
      return null;
    }
    final NBTTagCompound nbtTag = nbtRoot.getCompoundTag("eiocap");
    if (!nbtTag.hasKey("level", 99)) {
      return null;
    }
    final int capLevel = nbtTag.getInteger("level");
    if (capLevel <= 0 || capLevel > 99) {
      return null;
    }
    return new NBTCapacitorData(stack.getItem().getUnlocalizedName(stack), capLevel, nbtTag);
  }

  public static enum SetType {
    LEVEL,
    NAME,
    OWNER_TYPE,
    TYPE;
  }

  public static @Nonnull ItemStack addCapData(@Nonnull ItemStack stack, @Nonnull SetType setType, @Nullable CapacitorKey key, float value) {
    NBTTagCompound root = stack.getTagCompound();
    if (root == null) {
      root = new NBTTagCompound();
      stack.setTagCompound(root);
    }
    NBTTagCompound tag = root.getCompoundTag("eiocap");
    root.setTag("eiocap", tag);
    if (key == null) {
      addCapData(tag, setType, value);
    } else {
      addCapData(tag, setType, key, value);
    }
    return stack;
  }

  private static void addCapData(@Nonnull NBTTagCompound tag, @Nonnull SetType setType, float value) {
    switch (setType) {
    case LEVEL:
      tag.setInteger("level", (int) value);
    default:
      throw new IllegalArgumentException();
    }
  }

  private static void addCapData(@Nonnull NBTTagCompound tag, @Nonnull SetType setType, @Nonnull CapacitorKey key, float value) {
    switch (setType) {
    case NAME:
      tag.setFloat(key.getName(), value);
      break;
    case OWNER_TYPE:
      NBTTagCompound subtag = tag.getCompoundTag(key.getOwner().getUnlocalisedName());
      subtag.setFloat(key.getValueType().getName(), value);
      break;
    case TYPE:
      tag.setFloat(key.getValueType().getName(), value);
      break;
    default:
      throw new IllegalArgumentException();
    }
  }

  public static List<Pair<String, Float>> getCapDataRaw(@Nonnull ItemStack stack) {
    NBTTagCompound tag = stack.getSubCompound("eiocap");
    if (tag == null) {
      return null;
    }
    List<Pair<String, Float>> result = new ArrayList<Pair<String, Float>>();
    for (String key : tag.getKeySet()) {
      if (key != null && tag.hasKey(key, 5)) {
        result.add(Pair.of(key, tag.getFloat(key)));
      }
    }
    return result;
  }

  public static int getCapLevelRaw(@Nonnull ItemStack stack) {
    NBTTagCompound tag = stack.getSubCompound("eiocap");
    if (tag == null) {
      return 1;
    }
    return tag.getInteger("level");
  }

}
