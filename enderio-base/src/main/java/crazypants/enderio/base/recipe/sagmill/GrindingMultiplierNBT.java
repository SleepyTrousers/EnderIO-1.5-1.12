package crazypants.enderio.base.recipe.sagmill;

public class GrindingMultiplierNBT implements IGrindingMultiplier {

  private float chanceMultiplier = 1;

  private float powerMultiplier = 1;

  private float grindingMultiplier = 1;

  private int durationMJ;

  public GrindingMultiplierNBT(float chanceMultiplier, float powerMultiplier, float grindingMultiplier, int durationMJ) {
    this.chanceMultiplier = chanceMultiplier;
    this.powerMultiplier = powerMultiplier;
    this.grindingMultiplier = grindingMultiplier;
    this.durationMJ = durationMJ;
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
