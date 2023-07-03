package crazypants.enderio.machine.hypercube;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.util.PlayerUtil;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.machine.hypercube.TileHyperCube.IoMode;
import crazypants.enderio.machine.hypercube.TileHyperCube.SubChannel;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class PacketClientState implements IMessage, IMessageHandler<PacketClientState, IMessage> {

    private int x;
    private int y;
    private int z;
    private List<IoMode> modes;
    private Channel selectedChannel;

    public PacketClientState() {}

    public PacketClientState(TileHyperCube te) {
        x = te.xCoord;
        y = te.yCoord;
        z = te.zCoord;
        modes = new ArrayList<TileHyperCube.IoMode>(SubChannel.values().length);
        for (SubChannel sc : SubChannel.values()) {
            modes.add(te.getModeForChannel(sc));
        }
        selectedChannel = te.getChannel();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);

        if (selectedChannel == null) {
            ByteBufUtils.writeUTF8String(buf, "");
            ByteBufUtils.writeUTF8String(buf, "");
        } else {
            ByteBufUtils.writeUTF8String(buf, selectedChannel.name);
            if (selectedChannel.isPublic()) {
                ByteBufUtils.writeUTF8String(buf, "");
            } else {
                ByteBufUtils.writeUTF8String(buf, selectedChannel.user.toString());
            }
        }

        for (IoMode mode : modes) {
            buf.writeShort(mode.ordinal());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();

        String name = ByteBufUtils.readUTF8String(buf);
        String user = ByteBufUtils.readUTF8String(buf);
        selectedChannel = null;
        if (name != null && name.trim().length() > 0) {
            if (user != null && user.trim().length() > 0) {
                selectedChannel = new Channel(name, PlayerUtil.getPlayerUIDUnstable(user));
            } else {
                selectedChannel = new Channel(name, null);
            }
        }

        modes = new ArrayList<TileHyperCube.IoMode>(SubChannel.values().length);
        for (SubChannel sc : SubChannel.values()) {
            short ordinal = buf.readShort();
            modes.add(IoMode.values()[ordinal]);
        }
    }

    @Override
    public IMessage onMessage(PacketClientState message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (PacketUtil.isInvalidPacketForGui(ctx, te, getClass())) return null;
        if (te instanceof TileHyperCube) {
            TileHyperCube hc = (TileHyperCube) te;

            SubChannel[] vals = SubChannel.values();
            for (int i = 0; i < vals.length; i++) {
                SubChannel sc = vals[i];
                IoMode mode = message.modes.get(i);
                hc.setModeForChannel(sc, mode);
            }

            hc.setChannel(message.selectedChannel);

            player.worldObj.markBlockForUpdate(message.x, message.y, message.z);
        }
        return null;
    }
}
