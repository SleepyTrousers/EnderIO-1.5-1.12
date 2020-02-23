package crazypants.enderio.machines.machine.light;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.base.TileEntityEio;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.util.math.BlockPos;

import static crazypants.enderio.machines.init.MachineObject.block_electric_light;

@Storable
public class TileLightNode extends TileEntityEio {

  @Store
  private @Nonnull BlockPos parent = BlockPos.ORIGIN;

  public @Nullable TileElectricLight getParent() {
    if (parent == BlockPos.ORIGIN) { // yes, identity check. Could be a real parent at 0,0,0 instead
      return null;
    }
    return BlockEnder.getAnyTileEntitySafe(world, parent, TileElectricLight.class);
  }

  public void checkParent() {
    if (hasWorld() && parent != BlockPos.ORIGIN && world.isBlockLoaded(parent)) {
      if (world.getBlockState(parent).getBlock() != block_electric_light.getBlock()) {
        world.setBlockToAir(pos);
      }
    }
  }

  public void onNeighbourChanged() {
    TileElectricLight p = getParent();
    if (p != null) {
      p.nodeNeighbourChanged(this);
    }
  }

  public void onBlockRemoved() {
    TileElectricLight p = getParent();
    if (p != null) {
      p.nodeRemoved(this);
    }
  }

  @Override
  public String toString() {
    return "TileLightNode [parent=" + parent + ",  pos=" + pos + ", tileEntityInvalid=" + tileEntityInvalid + "]";
  }

  public void setParentPos(BlockPos pos) {
    parent = pos.toImmutable();
  }

  public @Nonnull BlockPos getParentPos() {
    return parent;
  }

}
