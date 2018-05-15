package crazypants.enderio.base.conduit.redstone.signals;

import javax.annotation.Nonnull;

public class Signal extends CombinedSignal {

  private int id;

  public Signal(int strength, int id) {
    super(strength);
    this.id = id;
  }

  public Signal(@Nonnull CombinedSignal signal, int id) {
    this(signal.getStrength(), id);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + getId();
    return result;
  }

  public int getId() {
    return id;
  }

  public void addStrength(int str) {
    str = getStrength() + str;
    setStrength(str);
  }

  public void removeStrength(int str) {
    str = getStrength() - str;
    setStrength(str);
  }

  public void resetSignal() {
    setStrength(0);
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
    return "Signal [getStrength()=" + getStrength() + ", getId()=" + getId() + "]";
  }

}
