package crazypants.enderio.base.capacitor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import crazypants.enderio.api.capacitor.CapabilityCapacitorData;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;

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
    return stack.getCapability(CapabilityCapacitorData.getCapNN(), null);
  }

  public static boolean isValidUpgrade(@Nonnull ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    final ICapacitorData capData = getNBTCapacitorDataFromItemStack(stack);
    if (capData != null) {
      return true;
    }
    return stack.hasCapability(CapabilityCapacitorData.getCapNN(), null);
  }

  protected static @Nullable ICapacitorData getNBTCapacitorDataFromItemStack(@Nonnull ItemStack stack) {
    final NBTTagCompound nbtRoot = stack.getTagCompound();
    if (nbtRoot == null) {
      return null;
    }
    if (!nbtRoot.hasKey("eiocap", (new NBTTagCompound()).getId())) {
      return null;
    }
    final NBTTagCompound nbtTag = nbtRoot.getCompoundTag("eiocap");
    if (!nbtTag.hasKey("level", (new NBTTagFloat(0)).getId())) {
      return null;
    }
    final float capLevel = nbtTag.getFloat("level");
    if (capLevel < 0 || capLevel >= 10) {
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

  public static @Nonnull ItemStack addCapData(@Nonnull ItemStack stack, @Nonnull SetType setType, @Nullable ICapacitorKey key, float value) {
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
      tag.setFloat("level", value);
      break;
    default:
      throw new IllegalArgumentException();
    }
  }

  private static void addCapData(@Nonnull NBTTagCompound tag, @Nonnull SetType setType, @Nonnull ICapacitorKey key, float value) {
    switch (setType) {
    case NAME:
      tag.setFloat(key.getRegistryName().toString(), value);
      break;
    case OWNER_TYPE:
      NBTTagCompound subtag = tag.getCompoundTag(key.getOwner().getUnlocalisedName());
      subtag.setFloat(key.getValueType().getName(), value);
      tag.setTag(key.getOwner().getUnlocalisedName(), subtag);
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
      if (key != null && !"level".equals(key) && tag.hasKey(key, (new NBTTagFloat(0)).getId())) {
        result.add(Pair.of(key, tag.getFloat(key)));
      }
    }
    return result;
  }

  public static float getCapLevelRaw(@Nonnull ItemStack stack) {
    NBTTagCompound tag = stack.getSubCompound("eiocap");
    if (tag == null) {
      return 1;
    }
    return tag.getFloat("level");
  }

  public static ICapacitorData increaseCapacitorLevel(@Nonnull ICapacitorData data,  float level) {
    return new ICapacitorData() {
      @Override
      public float getUnscaledValue(@Nonnull ICapacitorKey key) {
        return data.getUnscaledValue(key) > 0 ? data.getUnscaledValue(key) + level : 0;
      }

      @Nonnull
      @Override
      public String getUnlocalizedName() {
        return data.getUnlocalizedName();
      }

      @Nonnull
      @Override
      public String getLocalizedName() {
        return data.getUnlocalizedName();
      }
    };
  }

}
