package crazypants.enderio.powertools.machine.capbank.render;

import static crazypants.enderio.base.machine.MachineObject.blockCapBank;

import crazypants.enderio.powertools.machine.capbank.BlockCapBank;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import net.minecraft.util.EnumFacing;

public class FillGauge implements IInfoRenderer {

  @Override
  public void render(TileCapBank te, EnumFacing dir, float partialTick) {
    FillGaugeBakery bakery = new FillGaugeBakery(te.getWorld(), te.getPos(), dir, ((BlockCapBank) blockCapBank.getBlock()).getGaugeIcon());
    if (bakery.canRender()) {
      bakery.render();
    }
  }

}
