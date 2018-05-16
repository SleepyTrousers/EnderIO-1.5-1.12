package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TimerInputSignalFilter implements IInputSignalFilter, IFilterIncrementingValue {

  private int time = 20;
  private int currentTime = 0;

  @Override
  @Nonnull
  public CombinedSignal apply(@Nonnull CombinedSignal signal, @Nonnull World world, @Nonnull BlockPos pos) {
    if (signal.getStrength() == 0 && currentTime == time) {
      currentTime = 0;
      return CombinedSignal.MAX;
    }
    return CombinedSignal.NONE;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    NBTTagCompound t = nbtRoot.getCompoundTag("currentTime");
    currentTime = t.getInteger("time");

    NBTTagCompound m = nbtRoot.getCompoundTag("maxTime");
    time = m.getInteger("max");
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    NBTTagCompound c = new NBTTagCompound();
    c.setInteger("time", currentTime);
    nbtRoot.setTag("currentTime", c);

    NBTTagCompound m = new NBTTagCompound();
    m.setInteger("max", time);
    nbtRoot.setTag("maxTime", m);
  }

  @Override
  public boolean hasGui() {
    return true;
  }

  @Override
  public boolean shouldUpdate() {
    currentTime++;
    if (currentTime > time) {
      currentTime = 1;
    }
    return currentTime == time || currentTime == 1;
  }

  @Override
  public int getIncrementingValue() {
    return time;
  }

  @Override
  public void setIncrementingValue(int value) {
    this.time = value;
  }

  @Override
  public String getFilterHeading() {
    return Lang.GUI_REDSTONE_FILTER_TIMER.get();
  }

  @Override
  public String getIncrementingValueName() {
    return Lang.GUI_REDSTONE_FILTER_TIME.get();
  }

}
