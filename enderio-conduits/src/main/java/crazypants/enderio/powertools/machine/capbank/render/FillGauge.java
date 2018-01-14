package crazypants.enderio.powertools.machine.capbank.render;

import javax.annotation.Nonnull;

import crazypants.enderio.powertools.machine.capbank.BlockCapBank;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import net.minecraft.util.EnumFacing;

public class FillGauge implements IInfoRenderer {

  @Override
  public void render(@Nonnull TileCapBank te, @Nonnull EnumFacing dir, float partialTick) {
    FillGaugeBakery bakery = new FillGaugeBakery(te.getWorld(), te.getPos(), dir, BlockCapBank.getGaugeIcon());
    if (bakery.canRender()) {
      bakery.render();
    }
  }

}
