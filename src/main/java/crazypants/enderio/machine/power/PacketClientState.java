package crazypants.enderio.machine.power;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.IPacketEio;

public class PacketClientState implements IPacketEio {

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
    x = capBank.xCoord;
    y = capBank.yCoord;
    z = capBank.zCoord;

    inputMode = capBank.getInputControlMode();
    outputMode = capBank.getOutputControlMode();

    maxInput = capBank.getMaxInput();
    maxOutput = capBank.getMaxOutput();
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf dos) {
    dos.writeInt(x);
    dos.writeInt(y);
    dos.writeInt(z);
    dos.writeShort((short) inputMode.ordinal());
    dos.writeShort((short) outputMode.ordinal());
    dos.writeInt(maxInput);
    dos.writeInt(maxOutput);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf data) {
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
  public void handleClientSide(EntityPlayer player) {
    handle(player);
  }

  @Override
  public void handleServerSide(EntityPlayer player) {
    handle(player);
  }

  private void handle(EntityPlayer player) {

    TileEntity te = player.worldObj.getTileEntity(x, y, z);
    if(te instanceof TileCapacitorBank) {
      TileCapacitorBank cb = (TileCapacitorBank) te;
      cb.setInputControlMode(inputMode);
      cb.setOutputControlMode(outputMode);
      cb.setMaxInput(maxInput);
      cb.setMaxOutput(maxOutput);
      player.worldObj.markBlockForUpdate(x, y, z);
    }

  }

}
