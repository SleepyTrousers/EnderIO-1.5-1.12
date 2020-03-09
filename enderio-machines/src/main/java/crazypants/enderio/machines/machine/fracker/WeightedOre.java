package crazypants.enderio.machines.machine.fracker;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;

import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;

public class WeightedOre extends WeightedRandom.Item {

  private final int min, cost, dim;
  private final @Nonnull Things ore;

  public WeightedOre(int weight, int min, int cost, int dim, @Nonnull Things ore) {
    super(weight);
    this.min = min;
    this.cost = cost;
    this.dim = dim;
    this.ore = ore;
  }

  public int getMinPressure() {
    return min;
  }

  public int getDimension() {
    return dim;
  }

  public @Nonnull ItemStack getOre() {
    return ore.getItemStack().copy();
  }

  int getCost() {
    return cost;
  }

}