package crazypants.enderio.teleport.packet;

import crazypants.enderio.api.teleport.ITravelAccessable;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLabel implements IMessage, IMessageHandler<PacketLabel, IMessage> {

  int x;
  int y;
  int z;
  boolean labelNull;
  String label;
  

  public PacketLabel() {
  }

  public PacketLabel(int x, int y, int z, String label) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.label = label;
    labelNull = label == null || label.length() == 0;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeBoolean(labelNull);
    if(!labelNull) {
      ByteBufUtils.writeUTF8String(buf, label);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    labelNull = buf.readBoolean();
    if(labelNull) {
      label = null;
    } else {
      label = ByteBufUtils.readUTF8String(buf);
    }
  }

  @Override
  public IMessage onMessage(PacketLabel message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.world.getTileEntity(new BlockPos(message.x, message.y, message.z));
    if(te instanceof ITravelAccessable) {
      ((ITravelAccessable) te).setLabel(message.label);      
      BlockPos pos = new BlockPos(message.x, message.y, message.z);
      IBlockState bs = te.getWorld().getBlockState(pos);
      te.getWorld().notifyBlockUpdate(pos, bs, bs, 3);
      player.world.markChunkDirty(new BlockPos(message.x, message.y, message.z), te);
    }
    return null;
  }
}
