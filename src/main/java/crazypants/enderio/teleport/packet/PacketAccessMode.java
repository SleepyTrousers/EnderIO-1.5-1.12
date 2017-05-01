package crazypants.enderio.teleport.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by CrazyPants on 27/02/14.
 */
public class PacketAccessMode extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketAccessMode, IMessage> {

  private TileTravelAnchor.AccessMode mode;

  public PacketAccessMode() {
  }

  public <T extends TileEntity & ITravelAccessable> PacketAccessMode(T te, TileTravelAnchor.AccessMode mode) {
    super(te);
    this.mode = mode;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeShort(mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    mode = TileTravelAnchor.AccessMode.values()[buf.readShort()];
  }

  @Override
  public IMessage onMessage(PacketAccessMode message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = message.getTileEntity(player.worldObj);
    if(te instanceof ITravelAccessable) {
      ((ITravelAccessable) te).setAccessMode(message.mode);
      IBlockState bs = te.getWorld().getBlockState(message.getPos());
      te.getWorld().notifyBlockUpdate(message.getPos(), bs, bs, 3);
      player.worldObj.markChunkDirty(message.getPos(), te);
    }
    return null;
  }
}
