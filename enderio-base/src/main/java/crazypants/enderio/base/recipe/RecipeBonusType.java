package crazypants.enderio.base.recipe;

public enum RecipeBonusType {
  NONE(false, false),
  MULTIPLY_OUTPUT(true, true),
  CHANCE_ONLY(false, true);

  private final boolean multiply, chances;

  private RecipeBonusType(boolean multiply, boolean chances) {
    this.multiply = multiply;
    this.chances = chances;
  }

  public boolean doMultiply() {
    return multiply;
  }

  public boolean doChances() {
    return chances;
  }

  public boolean useBalls() {
    return multiply || chances;
  }

}
