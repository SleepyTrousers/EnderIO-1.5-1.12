package crazypants.enderio.rail;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.EntityUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class PlayerTeleportHandler {

    public static final PlayerTeleportHandler instance = new PlayerTeleportHandler();

    List<TeleportAction> queue = new ArrayList<TeleportAction>();

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        for (TeleportAction action : queue) {
            action.doTeleport();
        }
        queue.clear();
    }

    public void teleportPlayer(TileTransceiver reciever, EntityPlayerMP playerToTP, EntityMinecart playerToMount) {
        // Need to quete these as we cant perform the operation during a tile entities update phase as
        // we need to remove the player from being tracked
        queue.add(new TeleportAction(reciever, playerToTP, playerToMount));
    }

    private static class TeleportAction {

        TileTransceiver reciever;
        EntityPlayerMP playerToTP;
        EntityMinecart playerToMount;

        private TeleportAction(TileTransceiver reciever, EntityPlayerMP playerToTP, EntityMinecart playerToMount) {
            this.reciever = reciever;
            this.playerToTP = playerToTP;
            this.playerToMount = playerToMount;
        }

        void doTeleport() {
            int toDim = reciever.getWorldObj().provider.dimensionId;
            int meta = reciever.getBlockMetadata();

            // Make sure player not on the track and is in a safe position
            ForgeDirection railDir = EnderIO.blockEnderRail.getDirection(meta);
            int xOffset = Math.abs(railDir.offsetX);
            int zOffset = Math.abs(railDir.offsetZ);
            BlockCoord startPos = new BlockCoord(reciever).getLocation(ForgeDirection.UP);
            boolean foundSpot = false;
            for (int i = 1; i < 3 && !foundSpot; i++) {
                // try each side of the track
                playerToTP.setPosition(startPos.x + 0.5 - (xOffset * i), startPos.y, startPos.z + 0.5 - (zOffset * i));
                List<AxisAlignedBB> collides = EntityUtil.getCollidingBlockGeometry(reciever.getWorldObj(), playerToTP);
                foundSpot = collides == null || collides.isEmpty();
                if (!foundSpot) {
                    playerToTP.setPosition(
                            startPos.x + 0.5 + (xOffset * i), startPos.y, startPos.z + 0.5 + (zOffset * i));
                    collides = EntityUtil.getCollidingBlockGeometry(reciever.getWorldObj(), playerToTP);
                    foundSpot = collides == null || collides.isEmpty();
                }
            }
            if (!foundSpot) {
                // If not space each side will have to spawn on the track
                playerToTP.setPosition(startPos.x + 0.5, startPos.y, startPos.z + 0.5);
            }
            //      ChunkCoordinates spawn = new ChunkCoordinates((int) playerToTP.posX, (int) playerToTP.posY, (int)
            // playerToTP.posZ);
            //      TeleportUtil.teleportPlayer((WorldServer) reciever.getWorldObj(), playerToTP, toDim, spawn);

            reciever.getRailController().onPlayerTeleported(playerToTP, playerToMount);
            playerToTP
                    .mcServer
                    .getConfigurationManager()
                    .transferPlayerToDimension(
                            playerToTP, toDim, new TeleporterEIO(playerToTP.mcServer.worldServerForDimension(toDim)));
        }
    }
}
