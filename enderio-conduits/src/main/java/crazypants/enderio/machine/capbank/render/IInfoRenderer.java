package crazypants.enderio.machine.capbank.render;

import crazypants.enderio.machine.capbank.TileCapBank;
import net.minecraft.util.EnumFacing;

public interface IInfoRenderer {

  void render(TileCapBank cb, EnumFacing dir, float partialTick);

}
