package crazypants.enderio.machine.alloy;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketClientState implements IMessage, IMessageHandler<PacketClientState, IMessage> {

  private long pos;

  private TileAlloySmelter.Mode mode;

  public PacketClientState() {

  }

  public PacketClientState(TileAlloySmelter tile) {
    pos = tile.getPos().toLong();
    mode = tile.getMode();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
    buf.writeShort(mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    pos = buf.readLong();
    short ordinal = buf.readShort();
    mode = TileAlloySmelter.Mode.values()[ordinal];

  }

  public BlockPos getPos() {
    return BlockPos.fromLong(pos);
  }

  @Override
  public IMessage onMessage(PacketClientState message, MessageContext ctx) {
    TileEntity te = ctx.getServerHandler().playerEntity.world.getTileEntity(message.getPos());
    if (te instanceof TileAlloySmelter) {
      TileAlloySmelter me = (TileAlloySmelter) te;
      me.setMode(message.mode);
            
      IBlockState bs = ctx.getServerHandler().playerEntity.world.getBlockState(message.getPos());
      ctx.getServerHandler().playerEntity.world.notifyBlockUpdate(message.getPos(), bs, bs, 3);
    }
    return null;
  }
}
