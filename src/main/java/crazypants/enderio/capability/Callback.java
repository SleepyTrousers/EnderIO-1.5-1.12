package crazypants.enderio.capability;

import javax.annotation.Nullable;

//TODO 1.11 - use ec version
public interface Callback<T> {

  void onChange(@Nullable T oldStack, @Nullable T newStack);

}
