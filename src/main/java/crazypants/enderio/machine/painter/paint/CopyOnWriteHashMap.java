package crazypants.enderio.machine.painter.paint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nonnull;

public class CopyOnWriteHashMap<K, V> implements Map<K, V> {

  @Nonnull
  private Map<K, V> base = new HashMap<K, V>();
  @Nonnull
  final transient ReentrantLock theLock = new ReentrantLock();

  ReentrantLock getLock() {
    return theLock;
  }

  @Override
  public int size() {
    return base.size();
  }

  @Override
  public boolean isEmpty() {
    return base.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return base.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return base.containsValue(value);
  }

  @Override
  public V get(Object key) {
    return base.get(key);
  }

  Map<K, V> cow() {
    return base = new HashMap<K, V>(base);
  }

  @Override
  public V put(K key, V value) {
    final ReentrantLock lock = getLock();
    lock.lock();
    try {
      return cow().put(key, value);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public V remove(Object key) {
    final ReentrantLock lock = getLock();
    lock.lock();
    try {
      return cow().remove(key);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    final ReentrantLock lock = getLock();
    lock.lock();
    try {
      cow().putAll(m);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void clear() {
    final ReentrantLock lock = this.theLock;
    lock.lock();
    try {
      cow().clear();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Note: The iterator's remove() method is not thread-safe.
   */
  @Override
  public Set<K> keySet() {
    return base.keySet();
  }

  /**
   * Note: The iterator's remove() method is not thread-safe.
   */
  @Override
  public Collection<V> values() {
    return base.values();
  }

  /**
   * Note: The iterator's remove() method is not thread-safe.
   */
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    return base.entrySet();
  }

}