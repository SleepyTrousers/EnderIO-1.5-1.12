package crazypants.enderio.conduit.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;
import crazypants.enderio.Log;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.conduit.item.filter.IItemFilterUpgrade;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.network.NetworkUtil;

public class FilterRegister {

  public static IItemFilter getFilterForUpgrade(ItemStack stack) {
    if(stack == null) {
      return null;
    }
    if(!(stack.getItem() instanceof IItemFilterUpgrade)) {
      return null;
    }
    IItemFilterUpgrade upgrade = (IItemFilterUpgrade)stack.getItem();
    IItemFilter res = upgrade.createFilterFromStack(stack);     
    return res;
  }

  public static boolean isFilterSet(ItemStack stack) {
    return stack != null && stack.stackTagCompound != null && stack.stackTagCompound.hasKey("filter");
  }

  public static void writeFilterToStack(IItemFilter filter, ItemStack stack) {
    if(stack == null || filter == null) {
      return;
    }
    NBTTagCompound filterRoot = new NBTTagCompound();
    writeFilterToNbt(filter, filterRoot);
    if(stack.stackTagCompound == null) {
      stack.stackTagCompound = new NBTTagCompound();
    }
    stack.stackTagCompound.setTag("filter", filterRoot);
  }
  
  public static void writeFilterToNbt(IItemFilter filter, NBTTagCompound filterTag) {
    filterTag.setString("filterClass", filter.getClass().getName());
    filter.writeToNBT(filterTag);
  }

  public static IItemFilter loadFilterFromNbt(NBTTagCompound filterTag) {
    if(filterTag == null) {
      return null;
    }
    IItemFilter filter;    
    //legacy support for release where class names where not stored
    if(!filterTag.hasKey("filterClass")) {
      filter = null;
      Log.warn("Could not load old version of item filter.");
    } else {
      String className = filterTag.getString("filterClass");
      filter = loadFilterFromNbt(className, filterTag);
    }
    return filter;
  }
    

  private static IItemFilter loadFilterFromNbt(String className, NBTTagCompound tag) {
    try {
      Class<?> clz = Class.forName(className);
      IItemFilter filter = (IItemFilter) clz.newInstance();
      filter.readFromNBT(tag);
      return filter;
    } catch (Exception e) {
      Log.error("Could not read item filter with class name: " + className + " from NBT: " + tag + " Error: " + e);
      return null;
    }
  }

  public static void updateLegacyFilterNbt(NBTTagCompound filterTag, int conduitMeta) {
    if(filterTag == null) {
      return;
    }
    if(!filterTag.hasKey("filterClass")) {
      filterTag.setString("filterClass", ItemFilter.class.getName());
    }
    if(!filterTag.hasKey("isAdvanced")) {
      filterTag.setBoolean("isAdvanced", conduitMeta == 1);
    }
  }
  
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
    return loadFilterFromNbt(className, tag);
  }
  
}
