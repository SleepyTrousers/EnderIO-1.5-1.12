package crazypants.enderio.base.conduit.redstone.signals;

public class CombinedSignal {

  private int strength;

  public CombinedSignal(int strength) {
    this.strength = strength;
  }

  public int getStrength() {
    return strength;
  }

  protected void setStrength(int str) {
    if (str > 0) {
      if (str > 15) {
        strength = 15;
      } else {
        strength = str;
      }
    } else {
      strength = 0;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + strength;
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
    CombinedSignal other = (CombinedSignal) obj;
    if (strength != other.strength)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "CombinedSignal [strength=" + strength + "]";
  }

}