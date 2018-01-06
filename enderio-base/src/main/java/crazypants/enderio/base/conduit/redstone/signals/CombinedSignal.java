package crazypants.enderio.base.conduit.redstone.signals;

import javax.annotation.Nonnull;

public class CombinedSignal {

  public static final @Nonnull CombinedSignal NONE = new CombinedSignal(0);
  public static final @Nonnull CombinedSignal MAX = new CombinedSignal(15);

  private final int strength;

  public CombinedSignal(int strength) {
    this.strength = strength;
  }

  public int getStrength() {
    return strength;
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