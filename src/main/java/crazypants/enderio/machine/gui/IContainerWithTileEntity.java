package crazypants.enderio.machine.gui;

import net.minecraft.tileentity.TileEntity;

/**
 * Container implementing this interface holds TileEntity player is interacting with.
 *
 * @see crazypants.enderio.network.PacketUtil#isInvalidPacketForGui
 */
public interface IContainerWithTileEntity {

    TileEntity getTileEntity();
}
