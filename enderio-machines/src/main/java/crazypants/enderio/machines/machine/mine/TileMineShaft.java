package crazypants.enderio.machines.machine.mine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.TileEntityEio;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

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

  @Override
  @Nullable
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
    if (parent != null) {
      TileEntity tileEntity = world.getTileEntity(parent);
      if (tileEntity instanceof TileMine) {
        return tileEntity.getCapability(capability, facing);
      }
    }
    return super.getCapability(capability, facing);
  }

}
