package crazypants.enderio.machines.machine.alloy;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;

public enum OperatingMode {
  ALL,
  ALLOY,
  FURNACE;

  @Nonnull
  OperatingMode next() {
    int nextOrd = ordinal() + 1;
    if (nextOrd >= values().length) {
      nextOrd = 0;
    }
    return NullHelper.first(values()[nextOrd], ALL);
  }

  @Nonnull
  OperatingMode prev() {
    int nextOrd = ordinal() - 1;
    if (nextOrd < 0) {
      nextOrd = values().length - 1;
    }
    return NullHelper.first(values()[nextOrd], ALL);
  }
}