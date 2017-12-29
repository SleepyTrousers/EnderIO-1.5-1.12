package crazypants.enderio.base.recipe.sagmill;

import javax.annotation.Nonnull;

import crazypants.enderio.base.recipe.IRecipeInput;
import crazypants.enderio.base.recipe.RecipeInput;
import net.minecraft.item.ItemStack;

public class GrindingBall extends RecipeInput implements IGrindingMultiplier {

  private float chanceMultiplier = 1;

  private float powerMultiplier = 1;

  private float grindingMultiplier = 1;

  private int durationMJ;

  private @Nonnull IRecipeInput ri;

  public GrindingBall(@Nonnull IRecipeInput ri, float gm, float cm, float pm, int dmj) {
    super(ri.getInput());
    this.ri = ri;
    grindingMultiplier = gm;
    chanceMultiplier = cm;
    powerMultiplier = pm;
    durationMJ = dmj;
  }

  @Override
  public @Nonnull GrindingBall copy() {
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
  public int getDurability() {
    return durationMJ;
  }

  @Override
  public void setDurability(int durationMJ) {
    this.durationMJ = durationMJ;
  }

}
