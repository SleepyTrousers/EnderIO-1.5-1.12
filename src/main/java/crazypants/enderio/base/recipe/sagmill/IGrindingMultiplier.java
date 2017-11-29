package crazypants.enderio.base.recipe.sagmill;

public interface IGrindingMultiplier {

  void setDurationMJ(int durationMJ);

  int getDurationMJ();

  void setGrindingMultiplier(float grindingMultiplier);

  void setPowerMultiplier(float powerMultiplier);

  void setChanceMultiplier(float chanceMultiplier);

  float getPowerMultiplier();

  float getChanceMultiplier();

  float getGrindingMultiplier();

}
