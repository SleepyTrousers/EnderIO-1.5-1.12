package crazypants.enderio.base.item.darksteel.upgrade.storage;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.MutablePair;

import com.enderio.core.common.util.NullHelper;

public class NNPair<L, R> extends MutablePair<L, R> {

  private static final long serialVersionUID = 5714349103405358085L;

  public static <L, R> NNPair<L, R> of(final @Nonnull L left, final @Nonnull R right) {
    return new NNPair<L, R>(left, right);
  }

  public NNPair(@Nonnull L left, @Nonnull R right) {
    super(left, right);
  }

  @Override
  public @Nonnull L getLeft() {
    return NullHelper.notnull(super.getLeft(), "internal logic Error");
  }

  @Override
  public @Nonnull R getRight() {
    return NullHelper.notnull(super.getRight(), "internal logic Error");
  }

  @Override
  public @Nonnull R getValue() {
    return NullHelper.notnull(super.getValue(), "internal logic Error");
  }

  @Override
  public void setLeft(L left) {
    super.setLeft(NullHelper.notnull(left, "internal logic Error"));
  }

  @Override
  public void setRight(R right) {
    super.setRight(NullHelper.notnull(right, "internal logic Error"));
  }

  @Override
  public @Nonnull R setValue(R value) {
    return NullHelper.notnull(super.setValue(NullHelper.notnull(value, "internal logic Error")), "internal logic Error");
  }

}
