package crazypants.enderio.capability;

import javax.annotation.Nullable;

public interface Callback<T> {

  void onChange(@Nullable T oldStack, @Nullable T newStack);

}
