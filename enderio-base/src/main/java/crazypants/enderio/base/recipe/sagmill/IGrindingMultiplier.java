package crazypants.enderio.base.recipe.sagmill;

public interface IGrindingMultiplier {

  /**
   * Sets the durability of the ball in energy units.
   */
  void setDurability(int durability);

  /**
   * Gets the durability of the ball in energy units
   */
  int getDurability();

  void setGrindingMultiplier(float grindingMultiplier);

  void setPowerMultiplier(float powerMultiplier);

  void setChanceMultiplier(float chanceMultiplier);

  float getPowerMultiplier();

  float getChanceMultiplier();

  float getGrindingMultiplier();

}
