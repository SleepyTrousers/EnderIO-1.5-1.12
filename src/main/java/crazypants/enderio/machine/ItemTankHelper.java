package crazypants.enderio.machine;

import com.enderio.core.common.fluid.SmartTank;

import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemTankHelper {

  private ItemTankHelper() {
  }

  public static SmartTank getTank(ItemStack stack) {
    if (stack != null) {
      if (stack.hasTagCompound()) {
        return Reader.readField(stack.getTagCompound(), SmartTank.class, "tank", null);
      }
    }
    return null;
  }

  public static void setTank(ItemStack stack, SmartTank tank) {
    if (stack != null) {
      if (!stack.hasTagCompound()) {
        stack.setTagCompound(new NBTTagCompound());
      }
      Writer.writeField(stack.getTagCompound(), SmartTank.class, "tank", tank);
    }
  }

}
