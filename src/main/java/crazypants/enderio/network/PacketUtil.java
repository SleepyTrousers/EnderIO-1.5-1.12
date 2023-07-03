package crazypants.enderio.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.Log;
import crazypants.enderio.machine.gui.IContainerWithTileEntity;

public class PacketUtil {

    /**
     * Validates if TileEntity received from client is actually the one player is interacting with. It prevents
     * malicious user disturbing random machine settings etc.
     */
    public static boolean isInvalidPacketForGui(MessageContext ctx, TileEntity receivedTile, Class<?> messageClass) {
        if (receivedTile == null) {
            // Invalid, but not harmful
            return true;
        }
        if (ctx.side == Side.CLIENT) return false;
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        Container container = player.openContainer;
        if (!(container instanceof IContainerWithTileEntity)) {
            Log.LOGGER.warn(
                    Log.securityMarker,
                    "Player {} tried to send {} while not opening correct GUI. Target TileEntity at {}, {}, {}",
                    player.getGameProfile(),
                    messageClass.getSimpleName(),
                    receivedTile.xCoord,
                    receivedTile.yCoord,
                    receivedTile.zCoord);
            return true;
        }
        TileEntity expectedTile = ((IContainerWithTileEntity) container).getTileEntity();
        if (receivedTile != expectedTile) {
            Log.LOGGER.warn(
                    Log.securityMarker,
                    "Player {} tried to send {} which attempts to modify setting of TileEntity at {}, {}, {}. Expected: {}, {}, {}",
                    player.getGameProfile(),
                    messageClass.getSimpleName(),
                    receivedTile.xCoord,
                    receivedTile.yCoord,
                    receivedTile.zCoord,
                    expectedTile.xCoord,
                    expectedTile.yCoord,
                    expectedTile.zCoord);
            return true;
        }
        return false;
    }
}
