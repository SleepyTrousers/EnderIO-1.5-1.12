package crazypants.enderio.base.block.painted;

import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.state.IBlockState;

@Storable
public class TileEntityTwicePaintedBlock extends TileEntityPaintedBlock {

  @Store
  private IBlockState paintSource2 = null;

  public void setPaintSource2(IBlockState paintSource2) {
    this.paintSource2 = paintSource2;
    markDirty();
    updateBlock();
  }

  public IBlockState getPaintSource2() {
    return paintSource2;
  }

}