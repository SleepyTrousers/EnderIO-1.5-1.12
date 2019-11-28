package crazypants.enderio.base.loot;

import net.minecraft.util.WeightedRandom;

public class WeightedInteger extends WeightedRandom.Item {

  private final int i;

  public WeightedInteger(int weight, int i) {
    super(weight);
    this.i = i;
  }

  public int getInteger() {
    return i;
  }

}
