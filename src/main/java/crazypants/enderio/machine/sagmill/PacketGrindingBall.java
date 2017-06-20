package crazypants.enderio.machine.sagmill;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGrindingBall implements IMessage, IMessageHandler<PacketGrindingBall, IMessage> {

  private long pos;
  int currGbUse;
  int maxGbUse;

  public PacketGrindingBall() {
  }

  public PacketGrindingBall(TileSagMill ent) {
    pos = ent.getPos().toLong();
    currGbUse = ent.currGbUse;
    maxGbUse = ent.gb == null ? 0 : ent.gb.getDurationMJ();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
    buf.writeInt(currGbUse);
    buf.writeInt(maxGbUse);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    pos = buf.readLong();
    currGbUse = buf.readInt();
    maxGbUse = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketGrindingBall message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.world.getTileEntity(BlockPos.fromLong(message.pos));
    if (te instanceof TileSagMill) {
      TileSagMill me = (TileSagMill) te;
      me.currGbUse = message.currGbUse;
      me.maxGbUse = message.maxGbUse;
    }
    return null;
  }

}
