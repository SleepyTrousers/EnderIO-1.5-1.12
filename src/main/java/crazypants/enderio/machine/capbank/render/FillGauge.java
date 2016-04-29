package crazypants.enderio.machine.capbank.render;

import net.minecraft.util.EnumFacing;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.TileCapBank;

public class FillGauge implements IInfoRenderer {

  @Override
  public void render(TileCapBank te, EnumFacing dir, double x, double y, double z, float partialTick) {
    FillGaugeBakery bakery = new FillGaugeBakery(te.getWorld(), te.getPos(), dir, EnderIO.blockCapBank.getGaugeIcon());
    if (bakery.canRender()) {
      bakery.render();
    }
  }

}
