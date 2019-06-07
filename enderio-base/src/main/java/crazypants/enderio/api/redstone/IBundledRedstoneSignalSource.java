package crazypants.enderio.api.redstone;

import java.util.Map;

import javax.annotation.Nonnull;

import net.minecraft.item.EnumDyeColor;

public interface IBundledRedstoneSignalSource {

  @Nonnull
  Map<EnumDyeColor, Integer> getSignals();

}