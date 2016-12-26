package crazypants.enderio.capacitor;

import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static crazypants.enderio.ModObject.itemBasicCapacitor;

public class CapacitorHelper {

  private CapacitorHelper() {
  }

  public static ICapacitorData getCapacitorDataFromItemStack(ItemStack stack) {
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

  public static boolean isValidUpgrade(ItemStack stack) {
    if (Prep.isInvalid(stack)) {
      return false;
    }
    final ICapacitorData capData = getNBTCapacitorDataFromItemStack(stack);
    if (capData != null) {
      return true;
    }
    if (stack.getItem() == itemBasicCapacitor.getItem()) {
      return stack.getItemDamage() > 0;
    }
    if (stack.getItem() instanceof ICapacitorDataItem) {
      return true;
    }
    return false;
  }

  protected static ICapacitorData getNBTCapacitorDataFromItemStack(ItemStack stack) {
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

  public static ItemStack addCapData(ItemStack stack, SetType setType, CapacitorKey key, float value) {
    NBTTagCompound root = stack.getTagCompound();
    if (root == null) {
      root = new NBTTagCompound();
      stack.setTagCompound(root);
    }
    NBTTagCompound tag = root.getCompoundTag("eiocap");
    root.setTag("eiocap", tag);
    switch (setType) {
    case LEVEL:
      tag.setInteger("level", (int) value);
      break;
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
    }
    return stack;
  }

}
