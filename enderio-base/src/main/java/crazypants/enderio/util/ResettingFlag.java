package crazypants.enderio.util;

public class ResettingFlag {

  private boolean flag = false;

  public ResettingFlag() {
  }

  public void set(boolean value) {
    flag = value;
  }

  public void set() {
    flag = true;
  }

  public void reset() {
    flag = false;
  }

  public boolean peek() {
    return flag;
  }

  public boolean read() {
    if (flag) {
      flag = false;
      return true;
    } else {
      return false;
    }
  }

}
