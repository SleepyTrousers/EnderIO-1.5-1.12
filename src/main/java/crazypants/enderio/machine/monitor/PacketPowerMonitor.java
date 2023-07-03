package crazypants.enderio.machine.monitor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.Log;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class PacketPowerMonitor implements IMessage, IMessageHandler<PacketPowerMonitor, IMessage> {

    int x;
    int y;
    int z;
    boolean engineControlEnabled;
    float startLevel;
    float stopLevel;

    public PacketPowerMonitor() {}

    public PacketPowerMonitor(TilePowerMonitor pm) {
        x = pm.xCoord;
        y = pm.yCoord;
        z = pm.zCoord;
        engineControlEnabled = pm.engineControlEnabled;
        startLevel = pm.startLevel;
        stopLevel = pm.stopLevel;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeBoolean(engineControlEnabled);
        buf.writeFloat(startLevel);
        buf.writeFloat(stopLevel);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        engineControlEnabled = buffer.readBoolean();
        startLevel = buffer.readFloat();
        stopLevel = buffer.readFloat();
    }

    @Override
    public IMessage onMessage(PacketPowerMonitor message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (!(te instanceof TilePowerMonitor)) {
            Log.warn("createPowerMonitotPacket: Could not handle packet as TileEntity was not a TilePowerMonitor.");
            return null;
        }
        TilePowerMonitor pm = (TilePowerMonitor) te;
        if (PacketUtil.isInvalidPacketForGui(ctx, te, getClass())) return null;
        pm.engineControlEnabled = message.engineControlEnabled;
        pm.startLevel = message.startLevel;
        pm.stopLevel = message.stopLevel;
        player.worldObj.markBlockForUpdate(x, y, z);
        return null;
    }
}
