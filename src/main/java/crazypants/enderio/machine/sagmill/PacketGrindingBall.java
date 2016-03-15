package crazypants.enderio.machine.sagmill;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketGrindingBall implements IMessage, IMessageHandler<PacketGrindingBall, IMessage> {

  private int x;
  private int y;
  private int z;

  int currGbUse;
  int maxGbUse;

  public PacketGrindingBall() {
  }

  public PacketGrindingBall(TileSagMill ent) {
    x = ent.getPos().getX();
    y = ent.getPos().getY();
    z = ent.getPos().getZ();
    currGbUse = ent.currGbUse;
    maxGbUse = ent.gb == null ? 0 : ent.gb.getDurationMJ();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeInt(currGbUse);
    buf.writeInt(maxGbUse);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    currGbUse = buf.readInt();
    maxGbUse = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketGrindingBall message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(new BlockPos(message.x, message.y, message.z));
    if (te instanceof TileSagMill) {
      TileSagMill me = (TileSagMill) te;
      me.currGbUse = message.currGbUse;
      me.maxGbUse = message.maxGbUse;
    }
    return null;
  }

}
