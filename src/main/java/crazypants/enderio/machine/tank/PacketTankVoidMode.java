package crazypants.enderio.machine.tank;

import com.enderio.core.common.network.MessageTileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.machine.tank.GuiTank.VoidMode;
import io.netty.buffer.ByteBuf;

public class PacketTankVoidMode extends MessageTileEntity<TileTank>
        implements IMessageHandler<PacketTankVoidMode, IMessage> {

    public PacketTankVoidMode() {}

    private VoidMode mode;

    public PacketTankVoidMode(TileTank tank) {
        super(tank);
        this.mode = tank.getVoidMode();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(mode.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        mode = VoidMode.values()[buf.readByte()];
    }

    @Override
    public IMessage onMessage(PacketTankVoidMode message, MessageContext ctx) {
        TileTank te = message.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
        if (te != null) {
            te.setVoidMode(message.mode);
        }
        return null;
    }
}
