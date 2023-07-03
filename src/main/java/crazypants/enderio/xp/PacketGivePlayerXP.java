package crazypants.enderio.xp;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class PacketGivePlayerXP extends MessageTileEntity<TileEntity>
        implements IMessageHandler<PacketGivePlayerXP, IMessage> {

    private static boolean isRegistered = false;

    public static void register() {
        if (!isRegistered) {
            PacketHandler.INSTANCE.registerMessage(
                    PacketGivePlayerXP.class,
                    PacketGivePlayerXP.class,
                    PacketHandler.nextID(),
                    Side.SERVER);
            isRegistered = true;
        }
    }

    int levels;

    public PacketGivePlayerXP() {}

    public PacketGivePlayerXP(TileEntity tile, int levels) {
        super(tile);
        this.levels = levels;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeShort((short) levels);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        levels = buf.readShort();
    }

    @Override
    public IMessage onMessage(PacketGivePlayerXP message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity tile = message.getTileEntity(player.worldObj);
        if (PacketUtil.isInvalidPacketForGui(ctx, tile, getClass())) return null;
        if (tile instanceof IHaveExperience) {
            IHaveExperience xpTile = (IHaveExperience) tile;
            xpTile.getContainer().givePlayerXp(player, message.levels);
        }
        return null;
    }
}
