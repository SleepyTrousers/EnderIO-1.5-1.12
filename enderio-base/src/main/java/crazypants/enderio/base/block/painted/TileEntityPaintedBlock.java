package crazypants.enderio.base.block.painted;

import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.paint.IPaintable;
import info.loenwind.autosave.annotations.Storable;

@Storable
public class TileEntityPaintedBlock extends TileEntityEio implements IPaintable.IPaintableTileEntity {

  public TileEntityPaintedBlock() {
  }

  @Override
  public void onAfterDataPacket() {
    updateBlock();
  }

}
