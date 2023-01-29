package crazypants.enderio.conduit.packet;

import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.MessageTileEntity;

public abstract class AbstractConduitBundlePacket extends MessageTileEntity<TileEntity> {

    public AbstractConduitBundlePacket() {}

    public AbstractConduitBundlePacket(TileEntity tile) {
        super(tile);
    }
}
