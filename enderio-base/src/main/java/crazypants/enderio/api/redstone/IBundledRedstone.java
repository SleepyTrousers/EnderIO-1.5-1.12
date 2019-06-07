package crazypants.enderio.api.redstone;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.EnumDyeColor;

public interface IBundledRedstone {

  default @Nullable Map<EnumDyeColor, Integer> readSimpleSignal() {
    return null;
  }

  @Nonnull
  List<IBundledRedstoneSignalSource> getSignalSources(@Nonnull Set<IBundledRedstoneSignalNetwork> seen);

  void notifySignalSourceChange(@Nonnull Set<IBundledRedstoneSignalNetwork> seen);

  void notifySignalValueChange(@Nonnull Set<IBundledRedstoneSignalNetwork> seen);

}
