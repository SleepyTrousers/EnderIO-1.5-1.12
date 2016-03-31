package crazypants.enderio.capacitor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CapacitorHelper {

  private CapacitorHelper() {
  }

  public static ICapacitorData getCapacitorDataFromItemStack(ItemStack stack) {
    if (stack == null) {
      return null;
    }
    final Item item = stack.getItem();
    if (item == null) {
      return null;
    }
    if (item instanceof ICapacitorDataItem) {
      return ((ICapacitorDataItem) item).getCapacitorData(stack);
    }
    if (!stack.hasTagCompound()) {
      return null;
    }
    final NBTTagCompound nbtRoot = stack.getTagCompound();
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
    return new NBTCapacitorData(item.getUnlocalizedName(stack), capLevel, nbtTag);
  }

}
