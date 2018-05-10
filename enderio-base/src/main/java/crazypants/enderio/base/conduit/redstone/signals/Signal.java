package crazypants.enderio.base.conduit.redstone.signals;

import javax.annotation.Nonnull;

public class Signal extends CombinedSignal {

  public static final @Nonnull Signal NONE = new Signal(0);
  public static final @Nonnull Signal MAX = new Signal(15);

  private int totalStrength;

  public Signal(int strength) {
    super(strength);
    this.totalStrength = strength;
  }

  public Signal(@Nonnull CombinedSignal signal) {
    this(signal.getStrength());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + getTotalStrength();
    return result;
  }

  public int getTotalStrength() {
    return totalStrength;
  }

  public void addStrength(int str) {
    totalStrength += str;
    setStrength(totalStrength);
  }

  public void removeStrength(int str) {
    totalStrength -= str;
    setStrength(totalStrength);
  }

  public void resetSignal() {
    totalStrength = 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Signal [getStrength()=" + getStrength() + ", getTotalStrengt()=" + getTotalStrength() + "]";
  }

}
