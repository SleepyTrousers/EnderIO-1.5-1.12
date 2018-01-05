package crazypants.enderio.machine.monitor;

import javax.annotation.Nonnull;

public class StatCollector {

  public static final int MAX_VALUES = StatArray.MAX_VALUES;
  private final int max_values;
  private final StatArray mins = new StatArray();
  private final StatArray maxs = new StatArray();
  private final int collectTarget;
  private int collectCount;
  private int pos;

  public StatCollector(int collectTarget, int max_values) {
    this.collectTarget = collectTarget;
    this.collectCount = collectTarget;
    this.pos = -1;
    this.max_values = max_values;
  }

  public StatCollector(int collectTarget) {
    this.collectTarget = collectTarget;
    this.collectCount = collectTarget;
    this.pos = -1;
    this.max_values = MAX_VALUES;
  }

  public void addValue(int value) {
    assert collectCount <= collectTarget;
    assert collectCount > 0;
    assert pos < max_values;
    if (collectCount == collectTarget) {
      pos++;
      assert pos >= 0;
      if (pos >= max_values) {
        pos = 0;
      }
      mins.setValue(pos, value);
      maxs.setValue(pos, value);
      collectCount = 1;
    } else {
      assert pos >= 0;
      if (value < mins.getValue(pos)) {
        mins.setValue(pos, value);
      }
      if (value > maxs.getValue(pos)) {
        maxs.setValue(pos, value);
      }
      collectCount++;
    }
  }

  public int[][] getValues() {
    int[][] result = { new int[MAX_VALUES], new int[MAX_VALUES] }; // sic
    for (int i = 0; i < max_values; i++) {
      int j = i + pos + 1;
      if (j >= max_values) {
        j -= max_values;
      }
      assert j >= 0;
      assert j < max_values;
      result[0][i] = mins.getValue(j);
      result[1][i] = maxs.getValue(j);
    }
    return result;
  }

  public @Nonnull byte[] getData() {
    byte[] data = new byte[StatArray.BYTES * 2];
    mins.store(data, 0);
    maxs.store(data, StatArray.BYTES);
    return data;
  }

  public void setData(byte[] data) {
    if (data != null && data.length == StatArray.BYTES * 2) {
      mins.read(data, 0);
      maxs.read(data, StatArray.BYTES);
    }
  }

  public int getCollectCount() {
    return collectCount;
  }

  public void setCollectCount(int collectCount) {
    this.collectCount = collectCount;
  }

  public int getPos() {
    return pos;
  }

  public void setPos(int pos) {
    this.pos = pos;
  }

}
