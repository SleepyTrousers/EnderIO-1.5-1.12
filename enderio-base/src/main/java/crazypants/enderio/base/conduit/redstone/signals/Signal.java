package crazypants.enderio.base.conduit.redstone.signals;

import javax.annotation.Nonnull;

public class Signal {

  private final int id;
  private final int strength;

  public Signal(int strength, int id) {
    this.id = id;
    this.strength = strength;
  }

  public Signal(@Nonnull CombinedSignal signal, int id) {
    this(signal.getStrength(), id);
  }

  public int getStrength() {
    return strength;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + strength;
    result = prime * result + getId();
    return result;
  }

  public int getId() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Signal other = (Signal) obj;
    if (strength != other.getStrength())
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Signal [getStrength()=" + getStrength() + ", getId()=" + getId() + "]";
  }

}
