package crazypants.enderio.base.recipe.sagmill;

import javax.annotation.Nonnull;

import crazypants.enderio.base.recipe.RecipeInput;
import net.minecraft.item.ItemStack;

public class GrindingBall extends RecipeInput implements IGrindingMultiplier {

  private float chanceMultiplier = 1;

  private float powerMultiplier = 1;

  private float grindingMultiplier = 1;

  private int durationMJ;

  private @Nonnull RecipeInput ri;

  public GrindingBall(@Nonnull RecipeInput ri, float gm, float cm, float pm, int dmj) {
    super(ri.getInput());
    this.ri = ri;
    grindingMultiplier = gm;
    chanceMultiplier = cm;
    powerMultiplier = pm;
    durationMJ = dmj;
  }

  @Override
  public @Nonnull RecipeInput copy() {
    return new GrindingBall(ri, grindingMultiplier, chanceMultiplier, powerMultiplier, durationMJ);
  }

  @Override
  public boolean isInput(@Nonnull ItemStack test) {
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
