package crazypants.enderio.machine;

import com.enderio.core.common.util.BlockCoord;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.power.IPowerContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class PacketPowerStorage implements IMessage, IMessageHandler<PacketPowerStorage, IMessage> {

    private int x;
    private int y;
    private int z;
    private int storedEnergy;

    public PacketPowerStorage() {}

    public PacketPowerStorage(IPowerContainer ent) {
        BlockCoord bc = ent.getLocation();
        x = bc.x;
        y = bc.y;
        z = bc.z;
        storedEnergy = ent.getEnergyStored();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(storedEnergy);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        storedEnergy = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketPowerStorage message, MessageContext ctx) {
        EntityPlayer player = EnderIO.proxy.getClientPlayer();
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof IPowerContainer) {
            IPowerContainer me = (IPowerContainer) te;
            me.setEnergyStored(message.storedEnergy);
        }
        return null;
    }
}
