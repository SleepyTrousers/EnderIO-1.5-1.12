package crazypants.enderio.machine.capbank.render;

import crazypants.enderio.machine.capbank.TileCapBank;
import net.minecraftforge.common.util.ForgeDirection;

public interface IInfoRenderer {

    void render(TileCapBank cb, ForgeDirection dir, double x, double y, double z, float partialTick);
}
