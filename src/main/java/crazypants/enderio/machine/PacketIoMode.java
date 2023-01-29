package crazypants.enderio.machine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketIoMode implements IMessage, IMessageHandler<PacketIoMode, IMessage> {

    private int x;
    private int y;
    private int z;
    private IoMode mode;
    private ForgeDirection face;

    public PacketIoMode() {}

    public PacketIoMode(IIoConfigurable cont) {
        BlockCoord location = cont.getLocation();
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
        this.mode = IoMode.NONE;
        this.face = ForgeDirection.UNKNOWN;
    }

    public PacketIoMode(IIoConfigurable cont, ForgeDirection face) {
        BlockCoord location = cont.getLocation();
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
        this.face = face;
        mode = cont.getIoMode(face);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeShort((short) mode.ordinal());
        buf.writeShort((short) face.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        mode = IoMode.values()[buf.readShort()];
        face = ForgeDirection.values()[buf.readShort()];
    }

    @Override
    public IMessage onMessage(PacketIoMode message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof IIoConfigurable) {
            IIoConfigurable me = (IIoConfigurable) te;
            if (message.face == ForgeDirection.UNKNOWN) {
                me.clearAllIoModes();
            } else {
                me.setIoMode(message.face, message.mode);
            }
        }
        return null;
    }
}
