package crazypants.util;

import java.lang.reflect.Array;

public class ArrayMapper<T extends Object> {

  private final byte[] from_mapping;
  private final byte[] to_mapping;

  public ArrayMapper(String from_mapping, String to_mapping) {
    this.from_mapping = from_mapping.getBytes();
    this.to_mapping = to_mapping.getBytes();
  }

  private T getNextT(T[] in, byte what) {
    for (int i = 0; i < from_mapping.length; i++) {
      if (from_mapping[i] == what && in[i] != null) {
        T result = in[i];
        in[i] = null;
        return result;
      }
    }
    return null;
  }

  public T[] map(T[] in) {
    T[] in_tmp = in.clone();
    @SuppressWarnings("unchecked")
    T[] out = (T[]) Array.newInstance(in.getClass().getComponentType(), to_mapping.length);
    for (int i = 0; i < to_mapping.length; i++) {
      out[i] = getNextT(in_tmp, to_mapping[i]);
    }
    return out;
  }

}
