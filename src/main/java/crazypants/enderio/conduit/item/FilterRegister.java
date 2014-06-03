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
    if( ! (stack.getItem() instanceof IItemFilterUpgrade) ) {
      return null;
    }    
    IItemFilterUpgrade upgrade = (IItemFilterUpgrade)stack.getItem();
    IItemFilter res = upgrade.createFilterFromStack(stack); 
    return res;
  }

  public static void loadFilterFromStack(IItemFilter filter, ItemStack stack) {
    if(stack != null && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("filter")) {
      filter.readFromNBT(stack.stackTagCompound.getCompoundTag("filter"));
    }    
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
  }
  
  public static boolean isFilterSet(ItemStack stack) {
    return stack != null && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("filter");     
  }

}
