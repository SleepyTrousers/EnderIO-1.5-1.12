package crazypants.enderio.xp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.network.PacketHandler;
import io.netty.buffer.ByteBuf;

public class PacketDrainPlayerXP extends MessageTileEntity<TileEntity>
        implements IMessageHandler<PacketDrainPlayerXP, IMessage> {

    private static boolean isRegistered = false;

    public static void register() {
        if (!isRegistered) {
            PacketHandler.INSTANCE.registerMessage(
                    PacketDrainPlayerXP.class,
                    PacketDrainPlayerXP.class,
                    PacketHandler.nextID(),
                    Side.SERVER);
            isRegistered = true;
        }
    }

    int targetLevel;
    boolean isContainerLevel;

    public PacketDrainPlayerXP() {}

    public PacketDrainPlayerXP(TileEntity tile, int targetLevel, boolean isContainerLevel) {
        super(tile);
        this.targetLevel = targetLevel;
        this.isContainerLevel = isContainerLevel;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort((short) targetLevel);
        buf.writeBoolean(isContainerLevel);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        targetLevel = buf.readShort();
        isContainerLevel = buf.readBoolean();
    }

    @Override
    public IMessage onMessage(PacketDrainPlayerXP message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity tile = message.getTileEntity(player.worldObj);
        if (tile instanceof IHaveExperience) {
            IHaveExperience xpTile = (IHaveExperience) tile;
            if (player.capabilities.isCreativeMode && message.isContainerLevel) {
                xpTile.getContainer().addExperience(XpUtil.getExperienceForLevel(message.targetLevel));
            } else {
                if (message.isContainerLevel) {
                    xpTile.getContainer().drainPlayerXpToReachContainerLevel(player, message.targetLevel);
                } else {
                    xpTile.getContainer().drainPlayerXpToReachPlayerLevel(player, message.targetLevel);
                }
            }
        }
        return null;
    }
}
