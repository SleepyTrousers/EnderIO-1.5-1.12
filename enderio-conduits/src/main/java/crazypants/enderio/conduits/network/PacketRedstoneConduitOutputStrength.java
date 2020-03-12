package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import crazypants.enderio.conduits.conduit.redstone.IRedstoneConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRedstoneConduitOutputStrength extends AbstractConduitPacket.Sided<IRedstoneConduit> {

  private boolean isStrong;

  public PacketRedstoneConduitOutputStrength() {
  }

  public PacketRedstoneConduitOutputStrength(@Nonnull IRedstoneConduit con, @Nonnull EnumFacing dir) {
    super(con, dir);
    isStrong = con.isOutputStrong(dir);
  }

  @Override
  public void write(@Nonnull ByteBuf buf) {
    super.write(buf);
    buf.writeBoolean(isStrong);
  }

  @Override
  public void read(@Nonnull ByteBuf buf) {
    super.read(buf);
    isStrong = buf.readBoolean();
  }

  public static class Handler implements IMessageHandler<PacketRedstoneConduitOutputStrength, IMessage> {

    @Override
    public IMessage onMessage(PacketRedstoneConduitOutputStrength message, MessageContext ctx) {
      IRedstoneConduit tile = message.getConduit(ctx);
      if (tile != null) {
        tile.setOutputStrength(message.dir, message.isStrong);
        // message.getWorld(ctx).markBlockForUpdate(message.x, message.y, message.z);
      }
      return null;
    }
  }
}
