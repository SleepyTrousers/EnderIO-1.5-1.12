package crazypants.enderio.teleport.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.api.teleport.ITravelAccessable;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLabel extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketLabel, IMessage> {

  boolean labelNull;
  String label;

  public PacketLabel() {
  }

  public <T extends TileEntity & ITravelAccessable> PacketLabel(T te) {
    super(te);
    this.label = te.getLabel();
    labelNull = label == null || label.length() == 0;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(labelNull);
    if (!labelNull) {
      ByteBufUtils.writeUTF8String(buf, label);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    labelNull = buf.readBoolean();
    if (labelNull) {
      label = null;
    } else {
      label = ByteBufUtils.readUTF8String(buf);
    }
  }

  @Override
  public IMessage onMessage(PacketLabel message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = message.getTileEntity(player.worldObj);
    if (te instanceof ITravelAccessable) {
      ((ITravelAccessable) te).setLabel(message.label);
      IBlockState bs = te.getWorld().getBlockState(message.getPos());
      te.getWorld().notifyBlockUpdate(message.getPos(), bs, bs, 3);
      player.worldObj.markChunkDirty(message.getPos(), te);
    }
    return null;
  }

}
