package crazypants.enderio.farming;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

public enum FarmingTool {
  HOE,
  AXE,
  SHEARS,
  TREETAP,
  HAND;

  public final @Nonnull Things items = new Things(); // TODO 1.11
}
