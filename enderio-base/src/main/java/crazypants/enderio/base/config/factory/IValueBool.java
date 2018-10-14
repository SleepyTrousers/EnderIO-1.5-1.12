package crazypants.enderio.base.config.factory;

import javax.annotation.Nonnull;

public final class IValueBool {

  public static class And implements IValue<Boolean> {

    private final @Nonnull IValue<Boolean> a, b;

    public And(@Nonnull IValue<Boolean> a, @Nonnull IValue<Boolean> b) {
      this.a = a;
      this.b = b;
    }

    @Override
    @Nonnull
    public Boolean get() {
      return a.get() && b.get();
    }

  }

  public static class Or implements IValue<Boolean> {

    private final @Nonnull IValue<Boolean> a, b;

    public Or(@Nonnull IValue<Boolean> a, @Nonnull IValue<Boolean> b) {
      this.a = a;
      this.b = b;
    }

    @Override
    @Nonnull
    public Boolean get() {
      return a.get() || b.get();
    }

  }

}