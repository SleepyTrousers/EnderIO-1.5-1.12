package crazypants.enderio.machine.capbank.render;

import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.machine.capbank.TileCapBank;

public interface IInfoRenderer {

  void render(TileCapBank cb, ForgeDirection dir, double x, double y, double z, float partialTick);

}
