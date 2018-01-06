package crazypants.enderio.base.render.util;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

public class CompositeList<E> extends AbstractList<E> {

  private final @Nonnull List<E> list1;
  private final @Nonnull List<E> list2;

  public CompositeList(@Nonnull List<E> list1, @Nonnull List<E> list2) {
    this.list1 = list1;
    this.list2 = list2;
  }

  public static @Nonnull <E extends Object> List<E> create(List<E> list1, List<E> list2) {
    if (list1 == null || list1.isEmpty()) {
      if (list2 == null || list2.isEmpty()) {
        return Collections.<E> emptyList();
      } else {
        return list2;
      }
    } else {
      if (list2 == null || list2.isEmpty()) {
        return list1;
      } else {
        return new CompositeList<E>(list1, list2);
      }
    }
  }

  @Override
  public E get(int index) {
    if (index < list1.size()) {
      return list1.get(index);
    }
    return list2.get(index - list1.size());
  }

  @Override
  public int size() {
    return list1.size() + list2.size();
  }
}