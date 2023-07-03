package crazypants.enderio.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class PacketRedstoneMode implements IMessage, IMessageHandler<PacketRedstoneMode, IMessage> {

    private int x;
    private int y;
    private int z;
    private RedstoneControlMode mode;

    public PacketRedstoneMode() {}

    public PacketRedstoneMode(IRedstoneModeControlable cont, int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        mode = cont.getRedstoneControlMode();
    }

    public PacketRedstoneMode(AbstractMachineEntity ent) {
        x = ent.xCoord;
        y = ent.yCoord;
        z = ent.zCoord;
        mode = ent.getRedstoneControlMode();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeShort((short) mode.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        short ordinal = buf.readShort();
        mode = RedstoneControlMode.values()[ordinal];
    }

    @Override
    public IMessage onMessage(PacketRedstoneMode message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (PacketUtil.isInvalidPacketForGui(ctx, te, getClass())) return null;
        if (te instanceof IRedstoneModeControlable) {
            IRedstoneModeControlable me = (IRedstoneModeControlable) te;
            me.setRedstoneControlMode(message.mode);
        }
        return null;
    }
}
