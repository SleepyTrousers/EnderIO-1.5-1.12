package crazypants.enderio.teleport.packet;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by CrazyPants on 27/02/14.
 */
public class PacketAccessMode implements IMessage, IMessageHandler<PacketAccessMode, IMessage> {

  int x;
  int y;
  int z;
  TileTravelAnchor.AccessMode mode;

  public PacketAccessMode() {
  }

  public PacketAccessMode(int x, int y, int z, TileTravelAnchor.AccessMode mode) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.mode = mode;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeShort(mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    mode = TileTravelAnchor.AccessMode.values()[buf.readShort()];
  }

  @Override
  public IMessage onMessage(PacketAccessMode message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.world.getTileEntity(new BlockPos(message.x, message.y, message.z));
    if(te instanceof ITravelAccessable) {
      ((ITravelAccessable) te).setAccessMode(message.mode);
      BlockPos pos = new BlockPos(message.x, message.y, message.z);      
      IBlockState bs = te.getWorld().getBlockState(pos);
      te.getWorld().notifyBlockUpdate(pos, bs, bs, 3);      
      player.world.markChunkDirty(new BlockPos(message.x, message.y, message.z), te);
    }
    return null;
  }
}
