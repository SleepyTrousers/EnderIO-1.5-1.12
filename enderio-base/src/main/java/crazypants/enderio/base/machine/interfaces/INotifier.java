package crazypants.enderio.base.machine.interfaces;

import java.util.Set;

import javax.annotation.Nonnull;

import crazypants.enderio.api.ILocalizable;

public interface INotifier {

  @Nonnull
  Set<? extends ILocalizable> getNotification();

}
