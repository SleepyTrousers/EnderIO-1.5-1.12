package crazypants.enderio.conduit.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.network.AbstractPacketTileEntity;

public abstract class AbstractConduitBundlePacket extends AbstractPacketTileEntity<TileEntity> {

  public AbstractConduitBundlePacket() {
  }

  public AbstractConduitBundlePacket(TileEntity tile) {
    super(tile);
  }

  @Override
  protected void handleClientSide(EntityPlayer player, World worldObj, TileEntity tile) {
    if(!(tile instanceof IConduitBundle)) {
      return;
    }
    handleClientSide(player, worldObj, (IConduitBundle) tile);
  }

  protected void handleClientSide(EntityPlayer player, World worldObj, IConduitBundle tile) {
    handle(player, worldObj, tile);
  }

  @Override
  protected void handleServerSide(EntityPlayer player, World worldObj, TileEntity tile) {
    if(!(tile instanceof IConduitBundle)) {
      return;
    }
    handleServerSide(player, worldObj, (IConduitBundle) tile);
  }

  protected void handleServerSide(EntityPlayer player, World worldObj, IConduitBundle tile) {
    handle(player, worldObj, tile);
  }

  protected void handle(EntityPlayer player, World worldObj, IConduitBundle tile) {
  }

}
