package crazypants.enderio.capacitor;

import net.minecraft.util.WeightedRandom;

class WeightedInteger extends WeightedRandom.Item {
  private final int i;

  public WeightedInteger(int weight, int i) {
    super(weight);
    this.i = i;
  }

  public int getInteger() {
    return i;
  }

}