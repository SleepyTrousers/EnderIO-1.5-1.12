package crazypants.enderio.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

public class O2OMap<K, V> implements Map<K, V> {

  private final @Nonnull Map<K, V> keyMap = new HashMap<>();
  private final @Nonnull Map<V, K> valueMap = new HashMap<>();

  @Override
  public int size() {
    return keyMap.size();
  }

  @Override
  public boolean isEmpty() {
    return keyMap.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return keyMap.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return valueMap.containsKey(value);
  }

  @Override
  public V get(Object key) {
    return keyMap.get(key);
  }

  public V getValue(K key) {
    return keyMap.get(key);
  }

  public K getKey(V key) {
    return valueMap.get(key);
  }

  @Override
  public V put(K key, V value) {
    K key2 = valueMap.get(value);
    V value2 = keyMap.get(key);
    valueMap.remove(value2);
    keyMap.remove(key2);
    valueMap.put(value, key);
    keyMap.put(key, value);
    return value2;
  }

  /**
   * Adds a value mapping given that neither key nor value is already mapped. If that constraint is violated, a {@link RuntimeException} is thrown.
   * <p>
   * This methods otherwise follows the definition of {@link #put(Object,Object) put(k, v)}.
   * 
   * @param key
   *          key with which the specified value is to be associated
   * @param value
   *          value to be associated with the specified key
   * @return <code>null</code> (the previous value associated with <tt>key</tt>)
   * @throws RuntimeException
   */
  public V putNoOverride(K key, V value) {
    K key2 = valueMap.get(value);
    if (key2 != null) {
      throw new RuntimeException("value " + value + " already is mapped to key " + key2);
    }
    V value2 = keyMap.get(key);
    if (value2 != null) {
      throw new RuntimeException("key " + key + " already is mapped to value " + value2);
    }
    valueMap.put(value, key);
    keyMap.put(key, value);
    return value2;
  }

  @Override
  public V remove(Object key) {
    V value = keyMap.get(key);
    valueMap.remove(value);
    keyMap.remove(key);
    return value;
  }

  public V removeKey(K key) {
    V value = keyMap.get(key);
    valueMap.remove(value);
    keyMap.remove(key);
    return value;
  }

  public K removeValue(V value) {
    K key = valueMap.get(value);
    keyMap.remove(key);
    valueMap.remove(value);
    return key;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  public void putAllValues(Map<? extends V, ? extends K> m) {
    for (Entry<? extends V, ? extends K> entry : m.entrySet()) {
      put(entry.getValue(), entry.getKey());
    }
  }

  @Override
  public void clear() {
    keyMap.clear();
    valueMap.clear();
  }

  @Override
  public Set<K> keySet() {
    throw new UnsupportedOperationException();
  }

  @Override
  public @Nonnull Collection<V> values() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    throw new UnsupportedOperationException();
  }

}
