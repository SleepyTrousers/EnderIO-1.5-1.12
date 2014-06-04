package crazypants.enderio.conduit.item;

import cpw.mods.fml.common.network.ByteBufUtils;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.Log;
import crazypants.enderio.network.NetworkUtil;
import io.netty.buffer.ByteBuf;

public class FilterRegister {

  public static void writeFilter(ByteBuf buf, IItemFilter filter) {
    if(filter == null) {
      ByteBufUtils.writeUTF8String(buf, "nullFilter");
      return;
    }
    String name = filter.getClass().getName();
    ByteBufUtils.writeUTF8String(buf, name);

    NBTTagCompound root = new NBTTagCompound();
    filter.writeToNBT(root);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  public static IItemFilter readFilter(ByteBuf buf) {
    String className = ByteBufUtils.readUTF8String(buf);
    if(className.equals("nullFilter")) {
      return null;
    }
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    try {
      Class<?> clz = Class.forName(className);
      IItemFilter filter = (IItemFilter) clz.newInstance();
      filter.readFromNBT(tag);
      return filter;
    } catch (Exception e) {
      Log.error("Could not read item filter: " + e);
      return null;
    }
  }

  public static IItemFilter getFilterForUpgrade(ItemStack stack) {
    if(stack != null && stack.getItem() == Items.paper) {     
      return new ItemFilter(true);
    } else if(stack != null && stack.getItem() == Items.stick) {
      return new ItemFilter(false);
    }
    return null;
  }

}
