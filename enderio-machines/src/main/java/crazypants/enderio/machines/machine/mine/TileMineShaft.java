package crazypants.enderio.machines.machine.mine;

import crazypants.enderio.base.TileEntityEio;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.util.math.BlockPos;

@Storable
public class TileMineShaft extends TileEntityEio {

  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  private BlockPos parent = null;

  public BlockPos getParent() {
    return parent;
  }

  public void setParent(BlockPos parent) {
    this.parent = parent;
  }

}
