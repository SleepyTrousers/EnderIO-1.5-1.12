package crazypants.enderio.machine.power;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketUtil;
import io.netty.buffer.ByteBuf;

public class PacketClientState implements IMessage, IMessageHandler<PacketClientState, IMessage> {

    private int x;
    private int y;
    private int z;
    private RedstoneControlMode inputMode;
    private RedstoneControlMode outputMode;
    private int maxInput;
    private int maxOutput;

    public PacketClientState() {}

    public PacketClientState(TileCapacitorBank capBank) {
        x = capBank.xCoord;
        y = capBank.yCoord;
        z = capBank.zCoord;

        inputMode = capBank.getInputControlMode();
        outputMode = capBank.getOutputControlMode();

        maxInput = capBank.getMaxInput();
        maxOutput = capBank.getMaxOutput();
    }

    @Override
    public void toBytes(ByteBuf dos) {
        dos.writeInt(x);
        dos.writeInt(y);
        dos.writeInt(z);
        dos.writeShort((short) inputMode.ordinal());
        dos.writeShort((short) outputMode.ordinal());
        dos.writeInt(maxInput);
        dos.writeInt(maxOutput);
    }

    @Override
    public void fromBytes(ByteBuf data) {
        x = data.readInt();
        y = data.readInt();
        z = data.readInt();
        short inputOrdinal = data.readShort();
        short outputOrdinal = data.readShort();
        outputMode = RedstoneControlMode.values()[outputOrdinal];
        inputMode = RedstoneControlMode.values()[inputOrdinal];

        maxInput = data.readInt();
        maxOutput = data.readInt();
    }

    @Override
    public IMessage onMessage(PacketClientState message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (PacketUtil.isInvalidPacketForGui(ctx, te, getClass())) return null;
        if (te instanceof TileCapacitorBank) {
            TileCapacitorBank cb = (TileCapacitorBank) te;
            cb.setInputControlMode(message.inputMode);
            cb.setOutputControlMode(message.outputMode);
            cb.setMaxInput(message.maxInput);
            cb.setMaxOutput(message.maxOutput);
        }
        return null;
    }
}
