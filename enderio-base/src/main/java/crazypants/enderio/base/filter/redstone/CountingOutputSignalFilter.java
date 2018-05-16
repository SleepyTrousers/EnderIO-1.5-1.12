package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import net.minecraft.nbt.NBTTagCompound;

public class CountingOutputSignalFilter implements IOutputSignalFilter {

  private int maxCount = 1;
  private int count = 0;
  private boolean deactivated = true;

  @Override
  @Nonnull
  public CombinedSignal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    CombinedSignal signal = bundledSignal.getSignal(color);
    if (signal.getStrength() > CombinedSignal.NONE.getStrength() && deactivated) {
      count++;
      deactivated = false;
    }
    if (signal.getStrength() == CombinedSignal.NONE.getStrength()) {
      deactivated = true;
    }

    if (count > maxCount) {
      count = 1;
    } else if (count == maxCount) {
      return CombinedSignal.MAX;
    }
    return CombinedSignal.NONE;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    NBTTagCompound t = nbtRoot.getCompoundTag("currentCount");
    count = t.getInteger("count");

    NBTTagCompound d = nbtRoot.getCompoundTag("deactivated");
    deactivated = d.getBoolean("deactivated");

    NBTTagCompound m = nbtRoot.getCompoundTag("maxCount");
    maxCount = m.getInteger("max");
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    NBTTagCompound c = new NBTTagCompound();
    c.setInteger("count", count);
    nbtRoot.setTag("currentCount", c);

    NBTTagCompound d = new NBTTagCompound();
    d.setBoolean("deactivated", deactivated);
    nbtRoot.setTag("deactivated", d);

    NBTTagCompound m = new NBTTagCompound();
    m.setInteger("max", maxCount);
    nbtRoot.setTag("maxCount", m);
  }

  public void setMaxCount(int max) {
    maxCount = max;
  }

  public int getMaxCount() {
    return maxCount;
  }

  @Override
  public boolean isDefault() {
    return false;
  }

  @Override
  public boolean hasGui() {
    return true;
  }

}
