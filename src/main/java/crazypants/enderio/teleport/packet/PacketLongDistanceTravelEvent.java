package crazypants.enderio.teleport.packet;

import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraftforge.common.MinecraftForge;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.Log;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.teleport.ItemTeleportStaff;
import crazypants.enderio.teleport.TravelController;
import io.netty.buffer.ByteBuf;

public class PacketLongDistanceTravelEvent
        implements IMessage, IMessageHandler<PacketLongDistanceTravelEvent, IMessage> {

    boolean conserveMotion;
    int entityId;
    int source;

    public PacketLongDistanceTravelEvent() {}

    public PacketLongDistanceTravelEvent(Entity entity, boolean conserveMotion, TravelSource source) {
        this.conserveMotion = conserveMotion;
        this.entityId = entity instanceof EntityPlayer ? -1 : entity.getEntityId();
        this.source = source.ordinal();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(conserveMotion);
        buf.writeInt(entityId);
        buf.writeInt(source);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        conserveMotion = buf.readBoolean();
        entityId = buf.readInt();
        source = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketLongDistanceTravelEvent message, MessageContext ctx) {
        if (message.entityId != -1) {
            // after checking the code base, this type of packet won't be sent at all,
            // so we can assume this to be an attempt to hack
            Log.LOGGER.warn(
                    Log.securityMarker,
                    "Player {} tried to illegally tp other entity {}.",
                    ctx.getServerHandler().playerEntity.getGameProfile(),
                    message.entityId);
            return null;
        }
        EntityPlayerMP toTp = ctx.getServerHandler().playerEntity;

        TravelSource source = TravelSource.values()[message.source];

        if (!validate(toTp, source)) {
            Log.LOGGER.warn(
                    Log.securityMarker,
                    "Player {} tried to tp without valid prereq.",
                    ctx.getServerHandler().playerEntity.getGameProfile());
            return null;
        }

        doServerTeleport(toTp, message.conserveMotion, source);

        return null;
    }

    private static boolean validate(EntityPlayerMP toTp, TravelSource source) {
        ItemStack equippedItem = toTp.getCurrentEquippedItem();
        switch (source) {
            case STAFF:
                return equippedItem != null && equippedItem.getItem() instanceof IItemOfTravel
                        && ((IItemOfTravel) equippedItem.getItem()).isActive(toTp, equippedItem);
            case TELEPORT_STAFF:
                // tp staff is creative version of traveling staff
                // no energy check or anything else needed
                // but the player must actually be equipped with one of these
                return equippedItem != null && equippedItem.getItem() instanceof ItemTeleportStaff;
            default:
                // all other types are not allowed
                return false;
        }
    }

    public static boolean doServerTeleport(Entity toTp, boolean conserveMotion, TravelSource source) {
        EntityPlayer player = toTp instanceof EntityPlayer ? (EntityPlayer) toTp : null;
        Optional<BlockCoord> travelDestination = TravelController.instance.findTravelDestination(player, source);
        if (!travelDestination.isPresent()) {
            return false;
        }
        BlockCoord destination = travelDestination.get();
        int x = destination.x;
        int y = destination.y;
        int z = destination.z;
        int powerUse = TravelController.instance.getRequiredPower(player, source, destination);
        if (powerUse < 0) {
            return false;
        }
        if (player != null && player.getCurrentEquippedItem() != null
                && player.getCurrentEquippedItem().getItem() instanceof IItemOfTravel) {
            int used = ((IItemOfTravel) player.getCurrentEquippedItem().getItem())
                    .canExtractInternal(player.getCurrentEquippedItem(), powerUse);
            if (used != -1 && used != powerUse) {
                return false;
            }
        }

        TeleportEntityEvent evt = new TeleportEntityEvent(toTp, source, x, y, z);
        if (MinecraftForge.EVENT_BUS.post(evt)) {
            return false;
        }
        x = evt.targetX;
        y = evt.targetY;
        z = evt.targetZ;

        toTp.worldObj.playSoundEffect(toTp.posX, toTp.posY, toTp.posZ, source.sound, 1.0F, 1.0F);

        toTp.playSound(source.sound, 1.0F, 1.0F);

        if (player != null) {
            player.setPositionAndUpdate(x + 0.5, y + 1.1, z + 0.5);
        } else {
            toTp.setPosition(x, y, z);
        }

        toTp.worldObj.playSoundEffect(x, y, z, source.sound, 1.0F, 1.0F);
        toTp.fallDistance = 0;

        if (player != null) {
            if (conserveMotion) {
                Vector3d velocityVex = Util.getLookVecEio(player);
                S12PacketEntityVelocity p = new S12PacketEntityVelocity(
                        toTp.getEntityId(),
                        velocityVex.x,
                        velocityVex.y,
                        velocityVex.z);
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(p);
            }

            if (powerUse > 0 && player.getCurrentEquippedItem() != null
                    && player.getCurrentEquippedItem().getItem() instanceof IItemOfTravel) {
                ItemStack item = player.getCurrentEquippedItem().copy();
                ((IItemOfTravel) item.getItem()).extractInternal(item, powerUse);
                toTp.setCurrentItemOrArmor(0, item);
            }
        }

        return true;
    }
}
