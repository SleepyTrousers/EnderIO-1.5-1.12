package crazypants.enderio.teleport.telepad;

import com.enderio.core.common.network.MessageTileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PacketOpenServerGui extends MessageTileEntity<TileTelePad>
        implements IMessageHandler<PacketOpenServerGui, IMessage> {

    public PacketOpenServerGui() {}

    int id;

    public PacketOpenServerGui(TileTelePad te, int guiId) {
        super(te);
        this.id = guiId;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(id);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        this.id = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketOpenServerGui message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        World world = message.getWorld(ctx);
        TileTelePad te = message.getTileEntity(world);

        if (te != null) {
            player.openGui(EnderIO.instance, message.id, world, te.xCoord, te.yCoord, te.zCoord);
        }

        return null;
    }
}
