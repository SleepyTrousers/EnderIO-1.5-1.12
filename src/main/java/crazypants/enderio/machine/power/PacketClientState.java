package crazypants.enderio.machine.power;

import crazypants.enderio.machine.RedstoneControlMode;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketClientState implements IMessage, IMessageHandler<PacketClientState, IMessage>  {

  private int x;
  private int y;
  private int z;
  private RedstoneControlMode inputMode;
  private RedstoneControlMode outputMode;
  private int maxInput;
  private int maxOutput;

  public PacketClientState() {
  }

  public PacketClientState(TileCapacitorBank capBank) {
    BlockPos p = capBank.getPos();
    x = p.getX();
    y = p.getY();
    z = p.getZ();

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
    TileEntity te = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
    if(te instanceof TileCapacitorBank) {
      TileCapacitorBank cb = (TileCapacitorBank) te;
      cb.setInputControlMode(message.inputMode);
      cb.setOutputControlMode(message.outputMode);
      cb.setMaxInput(message.maxInput);
      cb.setMaxOutput(message.maxOutput);
    }
    return null;
  }

}
