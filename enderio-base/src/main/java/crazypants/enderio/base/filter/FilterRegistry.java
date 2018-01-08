package crazypants.enderio.base.filter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.Log;
import crazypants.enderio.util.NbtValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class FilterRegistry {

  public static boolean isItemFilter(@Nonnull ItemStack stack) {
    return stack.getItem() instanceof IItemFilterUpgrade;
  }

  public static @Nullable IItemFilter getFilterForUpgrade(@Nonnull ItemStack stack) {
    if (!isItemFilter(stack)) {
      return null;
    }
    IItemFilterUpgrade upgrade = (IItemFilterUpgrade) stack.getItem();
    IItemFilter res = upgrade.createFilterFromStack(stack);
    return res;
  }

  public static boolean isFilterSet(@Nonnull ItemStack stack) {
    return NbtValue.FILTER.hasTag(stack);
  }

  public static void writeFilterToStack(@Nullable IItemFilter filter, @Nonnull ItemStack stack) {
    if (filter == null) {
      return;
    }
    NBTTagCompound filterRoot = new NBTTagCompound();
    writeFilterToNbt(filter, filterRoot);
    NbtValue.FILTER.setTag(stack, filterRoot);
  }

  public static void writeFilterToNbt(@Nullable IItemFilter filter, @Nonnull NBTTagCompound filterTag) {
    if (filter == null) {
      return;
    }
    NbtValue.FILTER_CLASS.setString(filterTag, filter.getClass().getName());
    filter.writeToNBT(filterTag);
  }

  public static IItemFilter loadFilterFromNbt(@Nullable NBTTagCompound filterTag) {
    if (filterTag == null) {
      return null;
    }
    String className = NbtValue.FILTER_CLASS.getString(filterTag);
    return loadFilterFromNbt(className, filterTag);
  }

  private static IItemFilter loadFilterFromNbt(@Nullable String className, @Nonnull NBTTagCompound tag) {
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

  private static IItemFilter loadFilterFromByteBuf(@Nonnull String className, @Nonnull ByteBuf buf) {
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

  public static void writeFilter(@Nonnull ByteBuf buf, @Nullable IItemFilter filter) {
    if (filter == null) {
      ByteBufUtils.writeUTF8String(buf, "nullFilter");
      return;
    }
    String name = filter.getClass().getName();
    ByteBufUtils.writeUTF8String(buf, name);
    filter.writeToByteBuf(buf);
  }

  public static IItemFilter readFilter(@Nonnull ByteBuf buf) {
    String className = ByteBufUtils.readUTF8String(buf);
    if (className.equals("nullFilter")) {
      return null;
    }
    return loadFilterFromByteBuf(className, buf);
  }

}
