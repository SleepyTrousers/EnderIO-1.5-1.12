package crazypants.enderio.powertools.machine.monitor;

public class StatArray {

  public static final int MAX_VALUES = 100;
  public static final int BITS = 6;
  public static final int BYTES;
  public static final int MAX_VALUE;
  static {
    int b = 0, i = MAX_VALUES * BITS, v = 0;
    while (i > 0) {
      b++;
      i -= 8;
    }
    for (i = 0; i < BITS; i++) {
      v = (v << 1) | 1;
    }
    BYTES = b;
    MAX_VALUE = v;
  }
  private final byte[] b = new byte[BYTES]; // values * bit/value / 8 bit/byte

  public StatArray() {
  }

  public int getBit(int bit, int pos) {
    assert bit >= 0;
    assert bit < BITS;
    assert pos >= 0;
    assert pos < MAX_VALUES;
    int posr = bit * MAX_VALUES + pos;
    int idx1 = posr >> 3;
    int idx2 = 1 << (posr & 7);
    return (b[idx1] & idx2) == 0 ? 0 : (1 << bit);
  }

  public int getValue(int pos) {
    assert pos >= 0;
    assert pos < MAX_VALUES;
    int result = 0;
    for (int i = 0; i < BITS; i++) {
      result += getBit(i, pos);
    }
    return result;
  }

  public void setBit(int bit, int pos, int value) {
    assert bit >= 0;
    assert bit < BITS;
    assert pos >= 0;
    assert pos < MAX_VALUES;
    int posr = bit * MAX_VALUES + pos;
    int idx1 = posr >> 3;
    int idx2 = 1 << (posr & 7);
    if ((value & (1 << bit)) == 0) {
      b[idx1] &= ~idx2;
    } else {
      b[idx1] |= idx2;
    }
  }

  public void setValue(int pos, int value) {
    assert pos >= 0;
    assert pos < MAX_VALUES;
    assert value >= 0;
    assert value <= MAX_VALUE : value + " > " + MAX_VALUE;
    for (int i = 0; i < BITS; i++) {
      setBit(i, pos, value);
    }
  }

  public void store(byte[] data, int offset) {
    for (int i = 0; i < BYTES; i++) {
      data[i + offset] = b[i];
    }
  }

  public void read(byte[] data, int offset) {
    for (int i = 0; i < BYTES; i++) {
      b[i] = data[i + offset];
    }
  }

}
