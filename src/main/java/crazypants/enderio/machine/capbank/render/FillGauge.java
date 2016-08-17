package crazypants.enderio.machine.capbank.render;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.TileCapBank;
import net.minecraft.util.EnumFacing;

public class FillGauge implements IInfoRenderer {

  @Override
  public void render(TileCapBank te, EnumFacing dir, float partialTick) {
    FillGaugeBakery bakery = new FillGaugeBakery(te.getWorld(), te.getPos(), dir, EnderIO.blockCapBank.getGaugeIcon());
    if (bakery.canRender()) {
      bakery.render();
    }
  }

}
