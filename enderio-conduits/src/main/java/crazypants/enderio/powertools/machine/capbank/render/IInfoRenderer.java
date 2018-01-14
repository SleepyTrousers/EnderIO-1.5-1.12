package crazypants.enderio.powertools.machine.capbank.render;

import javax.annotation.Nonnull;

import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import net.minecraft.util.EnumFacing;

public interface IInfoRenderer {

  void render(@Nonnull TileCapBank cb, @Nonnull EnumFacing dir, float partialTick);

}
