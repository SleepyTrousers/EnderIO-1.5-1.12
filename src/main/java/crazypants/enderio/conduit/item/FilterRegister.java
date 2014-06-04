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
    if(stack == null) {
      return null;
    }
    
    IItemFilter res = null;
    if(stack != null && stack.getItem() == Items.paper) {     
      res = new ItemFilter(true);
    } else if(stack != null && stack.getItem() == Items.stick) {
      res = new ItemFilter(false);
    }
    if(res != null && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("filter")) {      
      res.readFromNBT(stack.stackTagCompound.getCompoundTag("filter"));
    } 
    return res;
  }

  public static void writeFilterToStack(IItemFilter filter, ItemStack stack) {
    if(stack == null || filter == null) {     
      return;
    }    
    NBTTagCompound filterRoot = new NBTTagCompound();
    filter.writeToNBT(filterRoot);
    if(stack.stackTagCompound == null) {
      stack.stackTagCompound = new NBTTagCompound();
    }
    stack.stackTagCompound.setTag("filter", filterRoot);
    stack.setStackDisplayName("Configured");
    
    //System.out.println("FilterRegister.writeFilterToStack: " + stack.stackTagCompound);
    
    
  }

}
