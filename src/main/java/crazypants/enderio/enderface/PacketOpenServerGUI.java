package crazypants.enderio.enderface;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.Vec3;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.PacketHandler;
import io.netty.buffer.ByteBuf;

public class PacketOpenServerGUI implements IMessage, IMessageHandler<PacketOpenServerGUI, IMessage> {

    int x;
    int y;
    int z;
    int side;
    Vec3 hitVec;

    public PacketOpenServerGUI() {}

    public PacketOpenServerGUI(int x, int y, int z, int side, Vec3 hitVec) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.hitVec = hitVec;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(side);
        buffer.writeDouble(hitVec.xCoord);
        buffer.writeDouble(hitVec.yCoord);
        buffer.writeDouble(hitVec.zCoord);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        side = buffer.readInt();
        hitVec = Vec3.createVectorHelper(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    public IMessage onMessage(PacketOpenServerGUI message, MessageContext ctx) {
        EntityPlayerMP player = (EntityPlayerMP) ctx.getServerHandler().playerEntity;
        Container c = player.openContainer;

        PacketHandler.INSTANCE.sendTo(new PacketLockClientContainer(player.openContainer.windowId), player);
        Vec3 hitVec = message.hitVec;
        player.theItemInWorldManager.activateBlockOrUseItem(
                player,
                player.worldObj,
                null,
                message.x,
                message.y,
                message.z,
                message.side,
                (float) hitVec.xCoord,
                (float) hitVec.yCoord,
                (float) hitVec.zCoord);
        player.theItemInWorldManager.thisPlayerMP = player;
        if (c != player.openContainer) {
            EnderIOController.INSTANCE.addContainer(player, player.openContainer);
        } else {
            PacketHandler.INSTANCE.sendTo(new PacketLockClientContainer(), player);
        }
        return null;
    }
}
