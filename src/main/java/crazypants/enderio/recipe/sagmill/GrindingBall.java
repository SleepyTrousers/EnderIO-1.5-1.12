package crazypants.enderio.recipe.sagmill;

import crazypants.enderio.recipe.RecipeInput;
import net.minecraft.item.ItemStack;

public class GrindingBall extends RecipeInput implements IGrindingMultiplier {

  private float chanceMultiplier = 1;

  private float powerMultiplier = 1;

  private float grindingMultiplier = 1;

  private int durationMJ;

  private RecipeInput ri;

  public GrindingBall(RecipeInput ri, float gm, float cm, float pm, int dmj) {
    super(ri.getInput());
    this.ri = ri;
    grindingMultiplier = gm;
    chanceMultiplier = cm;
    powerMultiplier = pm;
    durationMJ = dmj;
  }

  @Override
  public RecipeInput copy() {
    return new GrindingBall(ri, grindingMultiplier, chanceMultiplier, powerMultiplier, durationMJ);
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
