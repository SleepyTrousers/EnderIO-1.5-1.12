package crazypants.enderio.base.filter.redstone;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;

public interface ILogicSignalFilterType {

  @Nonnull
  public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors);

  public int getNumButtons();

  @Nonnull
  public String getHeading();

}
