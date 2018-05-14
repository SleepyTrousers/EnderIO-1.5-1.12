package crazypants.enderio.base.filter.redstone;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import net.minecraft.nbt.NBTTagCompound;

public class ToggleOutputSignalFilter implements IOutputSignalFilter {

  private boolean active = false;
  private boolean deactivated = true;

  @Override
  @Nonnull
  public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    Signal signal = bundledSignal.getSignal(color);
    if (signal.getStrength() > Signal.NONE.getStrength() && deactivated) {
      active = !active;
      deactivated = false;
    }
    if (signal.getStrength() == Signal.NONE.getStrength()) {
      deactivated = true;
    }
    return active ? Signal.MAX : Signal.NONE;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    NBTTagCompound t = nbtRoot.getCompoundTag("toggleActive");
    active = t.getBoolean("active");

    NBTTagCompound d = nbtRoot.getCompoundTag("deactivated");
    deactivated = d.getBoolean("deactivated");
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    NBTTagCompound t = new NBTTagCompound();
    t.setBoolean("active", active);
    nbtRoot.setTag("toggleActive", t);

    NBTTagCompound d = new NBTTagCompound();
    d.setBoolean("deactivated", deactivated);
    nbtRoot.setTag("deactivated", d);
  }

  @Override
  public boolean isDefault() {
    return !active;
  }

}
