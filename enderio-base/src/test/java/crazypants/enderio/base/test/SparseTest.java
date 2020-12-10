package crazypants.enderio.base.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.jupiter.api.Test;

import crazypants.enderio.util.SparseArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SparseTest {

  private static final int COUNT = 10_000;

  @Test
  void test() { // too lazy to split this into single tests...
    SparseArray a = new SparseArray();
    Map<Integer, Integer> m = new HashMap<>();
    Random r = new Random();

    // (0)
    // no values pop out of the empty array
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertEquals(0, a.get(k));
    }

    // (1)
    // fill with some data
    for (int i = 0; i < COUNT; i++) {
      int v = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      a.put(i, v);
      m.put(i, v);
    }
    // all values can be read again
    for (Entry<Integer, Integer> e : m.entrySet()) {
      assertEquals((int) e.getValue(), a.get(e.getKey()));
    }
    // no values pop out that we didn't put in
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertTrue(m.containsKey(k) == (a.get(k) > 0));
    }

    // (2)
    // replacing values with the same data
    for (Entry<Integer, Integer> e : m.entrySet()) {
      a.put(e.getKey(), e.getValue());
    }
    // all values can still be read again
    for (Entry<Integer, Integer> e : m.entrySet()) {
      assertEquals((int) e.getValue(), a.get(e.getKey()));
    }
    // no values pop out that we didn't put in
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertTrue(m.containsKey(k) == (a.get(k) > 0));
    }

    // (3)
    // replacing values with the different data
    for (Entry<Integer, Integer> e : m.entrySet()) {
      a.put(e.getKey(), e.getValue() + 1337);
    }
    // all values can still be read again
    for (Entry<Integer, Integer> e : m.entrySet()) {
      assertEquals(e.getValue() + 1337, a.get(e.getKey()));
    }
    // no values pop out that we didn't put in
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertTrue(m.containsKey(k) == (a.get(k) > 0));
    }

    // (4)
    // replacing values with the same data
    for (Entry<Integer, Integer> e : m.entrySet()) {
      a.put(e.getKey(), e.getValue());
    }
    // all values can still be read again
    for (Entry<Integer, Integer> e : m.entrySet()) {
      assertEquals((int) e.getValue(), a.get(e.getKey()));
    }
    // no values pop out that we didn't put in
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertTrue(m.containsKey(k) == (a.get(k) > 0));
    }

    // (5)
    // deleting some values
    for (Iterator<Entry<Integer, Integer>> i = m.entrySet().iterator(); i.hasNext();) {
      Entry<Integer, Integer> e = i.next();
      if (r.nextBoolean()) {
        a.delete(e.getKey());
        i.remove();
      }
    }
    // all values can still be read again
    for (Entry<Integer, Integer> e : m.entrySet()) {
      assertEquals((int) e.getValue(), a.get(e.getKey()));
    }
    // no values pop out that we didn't put in
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertTrue(m.containsKey(k) == (a.get(k) > 0));
    }

    // (6)
    // put in more data
    for (int i = 0; i < COUNT; i++) {
      int v = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      a.put(i, v);
      m.put(i, v);
    }
    // all values can be read again
    for (Entry<Integer, Integer> e : m.entrySet()) {
      assertEquals((int) e.getValue(), a.get(e.getKey()));
    }
    // no values pop out that we didn't put in
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertTrue(m.containsKey(k) == (a.get(k) > 0));
    }

    // (7)
    // Copy the array
    a = new SparseArray(a.toNBT());
    // all values can be read again
    for (Entry<Integer, Integer> e : m.entrySet()) {
      assertEquals((int) e.getValue(), a.get(e.getKey()));
    }
    // no values pop out that we didn't put in
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertTrue(m.containsKey(k) == (a.get(k) > 0));
    }

    // (8)
    // deleting some values
    for (Iterator<Entry<Integer, Integer>> i = m.entrySet().iterator(); i.hasNext();) {
      Entry<Integer, Integer> e = i.next();
      if (r.nextBoolean()) {
        a.delete(e.getKey());
        i.remove();
      }
    }
    // all values can still be read again
    for (Entry<Integer, Integer> e : m.entrySet()) {
      assertEquals((int) e.getValue(), a.get(e.getKey()));
    }
    // no values pop out that we didn't put in
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertTrue(m.containsKey(k) == (a.get(k) > 0));
    }

    // (9)
    // put in more data
    for (int i = 0; i < COUNT; i++) {
      int v = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      a.put(i, v);
      m.put(i, v);
    }
    // all values can be read again
    for (Entry<Integer, Integer> e : m.entrySet()) {
      assertEquals((int) e.getValue(), a.get(e.getKey()));
    }
    // no values pop out that we didn't put in
    for (int i = 0; i < COUNT; i++) {
      int k = r.nextInt(Integer.MAX_VALUE - 1) + 1;
      assertTrue(m.containsKey(k) == (a.get(k) > 0));
    }

  }

}
