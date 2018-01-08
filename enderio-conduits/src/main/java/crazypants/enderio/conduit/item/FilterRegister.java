package crazypants.enderio.conduit.item;

import crazypants.enderio.base.Log;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.conduit.item.filter.IItemFilterUpgrade;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class FilterRegister {

  public static boolean isItemFilter(ItemStack stack) {
    return stack != null && (stack.getItem() instanceof IItemFilterUpgrade);
  }

  public static IItemFilter getFilterForUpgrade(ItemStack stack) {
    if(!isItemFilter(stack)) {
      return null;
    }
    IItemFilterUpgrade upgrade = (IItemFilterUpgrade)stack.getItem();
    IItemFilter res = upgrade.createFilterFromStack(stack);     
    return res;
  }

  public static boolean isFilterSet(ItemStack stack) {
    return stack != null && stack.getTagCompound() != null && stack.getTagCompound().hasKey("filter");
  }

  public static void writeFilterToStack(IItemFilter filter, ItemStack stack) {
    if(stack == null || filter == null) {
      return;
    }
    NBTTagCompound filterRoot = new NBTTagCompound();
    writeFilterToNbt(filter, filterRoot);
    if(stack.getTagCompound() == null) {
      stack.setTagCompound(new NBTTagCompound());
    }
    stack.getTagCompound().setTag("filter", filterRoot);
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

  private static IItemFilter loadFilterFromByteBuf(String className, ByteBuf buf) {
    try {
      Class<?> clz = Class.forName(className);
      IItemFilter filter = (IItemFilter) clz.newInstance();
      filter.readFromByteBuf(buf);
      return filter;
    } catch (Exception e) {
      Log.error("Could not read item filter with class name: " + className + " from ByteBuf Error: " + e);
      return null;
    }
  }

  public static void writeFilter(ByteBuf buf, IItemFilter filter) {
    if(filter == null) {
      ByteBufUtils.writeUTF8String(buf, "nullFilter");
      return;
    }
    String name = filter.getClass().getName();
    ByteBufUtils.writeUTF8String(buf, name);
    filter.writeToByteBuf(buf);
  }

  public static IItemFilter readFilter(ByteBuf buf) {
    String className = ByteBufUtils.readUTF8String(buf);
    if(className.equals("nullFilter")) {
      return null;
    }
    return loadFilterFromByteBuf(className, buf);
  }

}
