package crazypants.enderio.conduit.packet;

import com.enderio.core.common.network.MessageTileEntity;

import net.minecraft.tileentity.TileEntity;

public abstract class AbstractConduitBundlePacket extends MessageTileEntity<TileEntity> {

  public AbstractConduitBundlePacket() {
  }

  public AbstractConduitBundlePacket(TileEntity tile) {
    super(tile);
  }
}
