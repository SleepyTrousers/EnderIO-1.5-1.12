package crazypants.enderio.conduit;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.gas.GasUtil;
import crazypants.enderio.conduit.me.MEUtil;

public enum ConduitDisplayMode {
  ALL,
  POWER,
  REDSTONE,
  FLUID,
  ITEM,
  GAS, 
  ME;

  public static ConduitDisplayMode next(ConduitDisplayMode mode) {
    int index = mode.ordinal() + 1;
    if(index >= values().length) {
      index = 0;
    }
    ConduitDisplayMode res = values()[index];
    if(res == GAS && !GasUtil.isGasConduitEnabled()) {
      return next(res);
    }
    if(res == ME && !MEUtil.isMEEnabled()) {
      return next(res);
    }
    return res;
  }

  public static ConduitDisplayMode previous(ConduitDisplayMode mode) {
    int index = mode.ordinal() - 1;
    if(index < 0) {
      index = values().length - 1;
    }
    ConduitDisplayMode res = values()[index];
    if(res == GAS && !GasUtil.isGasConduitEnabled()) {
      return previous(res);
    }
    if(res == ME && !MEUtil.isMEEnabled()) {
      return previous(res);
    }
    return res;
  }

  private static final String NBT_KEY = "enderio.displaymode";

  public static ConduitDisplayMode getDisplayMode(ItemStack equipped) {
    if(equipped == null || !(equipped.getItem() instanceof IConduitControl)) {
      return ALL;
    }
    initDisplayModeTag(equipped);
    int index = equipped.stackTagCompound.getInteger(NBT_KEY);
    index = MathHelper.clamp_int(index, 0, ConduitDisplayMode.values().length - 1);
    return ConduitDisplayMode.values()[index];
  }

  public static void setDisplayMode(ItemStack equipped, ConduitDisplayMode mode) {
    if(mode == null || equipped == null || !(equipped.getItem() instanceof IConduitControl)) {
      return;
    }
    initDisplayModeTag(equipped);
    equipped.stackTagCompound.setInteger(NBT_KEY, mode.ordinal());
  }

  private static void initDisplayModeTag(ItemStack stack) {
    if (stack.stackTagCompound == null) {
      stack.stackTagCompound = new NBTTagCompound();
      stack.stackTagCompound.setInteger(NBT_KEY, ConduitDisplayMode.ALL.ordinal());
    }
  }

  public ConduitDisplayMode next() {
    return next(this);
  }

  public ConduitDisplayMode previous() {
    return previous(this);
  }

}