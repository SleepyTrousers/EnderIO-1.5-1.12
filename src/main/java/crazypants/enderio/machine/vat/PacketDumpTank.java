package crazypants.enderio.machine.vat;

import net.minecraftforge.fluids.FluidTank;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class PacketDumpTank extends MessageTileEntity<TileVat> implements IMessageHandler<PacketDumpTank, IMessage> {

    private int tank;

    public PacketDumpTank() {
        super();
    }

    public PacketDumpTank(TileVat te, int tank) {
        super(te);
        this.tank = tank;
        getTank(te).setFluid(null);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeInt(tank);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        tank = buf.readInt();
    }

    private FluidTank getTank(TileVat te) {
        if (tank == 1) {
            return te.inputTank;
        } else if (tank == 2) {
            return te.outputTank;
        }
        return null;
    }

    @Override
    public IMessage onMessage(PacketDumpTank message, MessageContext ctx) {
        TileVat te = message.getTileEntity(ctx.getServerHandler().playerEntity.worldObj);
        if (PacketUtil.isInvalidPacketForGui(ctx, te, getClass())) return null;
        message.getTank(te).setFluid(null);
        return null;
    }
}
