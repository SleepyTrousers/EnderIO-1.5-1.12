package crazypants.enderio.conduit.packet;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import net.minecraft.tileentity.TileEntity;

public abstract class AbstractConduitBundlePacket extends MessageTileEntity<TileEntity> {

  public AbstractConduitBundlePacket() {
  }

  public AbstractConduitBundlePacket(@Nonnull TileEntity tile) {
    super(tile);
  }

}
