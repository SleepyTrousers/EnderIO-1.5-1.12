package crazypants.enderio.conduit.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;

public class PacketConnectionMode extends AbstractConduitPacket<IConduit> {

  private ForgeDirection dir;
  private ConnectionMode mode;

  public PacketConnectionMode() {
  }

  public PacketConnectionMode(IConduit con, ForgeDirection dir) {
    super(con.getBundle().getEntity(), ConTypeEnum.get(con));
    this.dir = dir;
    mode = con.getConectionMode(dir);
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    buf.writeShort(dir.ordinal());
    buf.writeShort(mode.ordinal());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    dir = ForgeDirection.values()[buf.readShort()];
    mode = ConnectionMode.values()[buf.readShort()];

  }

  @Override
  protected void handle(EntityPlayer player, World worldObj, IConduitBundle tile, IConduit conduit) {
    conduit.setConnectionMode(dir, mode);
    worldObj.markBlockForUpdate(x, y, z);
  }

}
