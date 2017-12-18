package crazypants.enderio.base.integration.jei.energy;

public class EnergyIngredient {

  private final int amount;
  private final boolean hasAmount, isPerTick;

  public EnergyIngredient() {
    this(0, false, false);
  }

  public EnergyIngredient(int amount) {
    this(amount, true, false);
  }

  public EnergyIngredient(int amount, boolean isPerTick) {
    this(amount, true, isPerTick);
  }

  private EnergyIngredient(int amount, boolean hasAmount, boolean isPerTick) {
    this.amount = amount;
    this.hasAmount = hasAmount;
    this.isPerTick = isPerTick;
  }

  public int getAmount() {
    return amount;
  }

  public boolean hasAmount() {
    return hasAmount;
  }

  public boolean isPerTick() {
    return isPerTick;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (hasAmount ? amount : 0);
    result = prime * result + (hasAmount ? 1231 : 1237);
    result = prime * result + (hasAmount && isPerTick ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EnergyIngredient other = (EnergyIngredient) obj;
    if (hasAmount != other.hasAmount)
      return false;
    if (hasAmount && amount != other.amount)
      return false;
    if (hasAmount && isPerTick != other.isPerTick)
      return false;
    return true;
  }

}
