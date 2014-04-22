package crazypants.enderio.machine.crusher;

import net.minecraft.item.ItemStack;
import crazypants.enderio.machine.recipe.RecipeInput;

public class GrindingBall extends RecipeInput implements IGrindingMultiplier {

  float chanceMultiplier = 1;

  float powerMultiplier = 1;

  float grindingMultiplier = 1;

  int durationMJ;

  RecipeInput ri;

  //  public GrindingBall(ItemStack item, boolean useMeta, float multiplier, int slot) {
  //    super(item, useMeta, multiplier, slot);
  //  }
  //
  //  public GrindingBall(ItemStack input, boolean useMeta) {
  //    super(input, useMeta);
  //  }
  //
  //  public GrindingBall(ItemStack input) {
  //    super(input);
  //  }

  public GrindingBall(RecipeInput ri, float gm, float cm, float pm, int dmj) {
    super(ri.getInput());
    this.ri = ri;
    grindingMultiplier = gm;
    chanceMultiplier = cm;
    powerMultiplier = pm;
    durationMJ = dmj;
  }

  @Override
  public boolean isInput(ItemStack test) {
    return ri.isInput(test);
  }

  @Override
  public float getGrindingMultiplier() {
    return grindingMultiplier;
  }

  @Override
  public float getChanceMultiplier() {
    return chanceMultiplier;
  }

  @Override
  public float getPowerMultiplier() {
    return powerMultiplier;
  }

  @Override
  public void setChanceMultiplier(float chanceMultiplier) {
    this.chanceMultiplier = chanceMultiplier;
  }

  @Override
  public void setPowerMultiplier(float powerMultiplier) {
    this.powerMultiplier = powerMultiplier;
  }

  @Override
  public void setGrindingMultiplier(float grindingMultiplier) {
    this.grindingMultiplier = grindingMultiplier;
  }

  @Override
  public int getDurationMJ() {
    return durationMJ;
  }

  @Override
  public void setDurationMJ(int durationMJ) {
    this.durationMJ = durationMJ;
  }

}
