package crazypants.enderio.conduit.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.util.DyeColor;

public class PacketRedstoneConduitSignalColor extends AbstractConduitPacket<IInsulatedRedstoneConduit> {

  private ForgeDirection dir;
  private DyeColor col;

  public PacketRedstoneConduitSignalColor() {
  }

  public PacketRedstoneConduitSignalColor(IInsulatedRedstoneConduit con, ForgeDirection dir) {
    super(con.getBundle().getEntity(), ConTypeEnum.REDSTONE);
    this.dir = dir;
    col = con.getSignalColor(dir);
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    buf.writeShort(dir.ordinal());
    buf.writeShort(col.ordinal());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    dir = ForgeDirection.values()[buf.readShort()];
    col = DyeColor.values()[buf.readShort()];
  }

  @Override
  protected void handleServerSide(EntityPlayer player, World worldObj, IConduitBundle tile, IInsulatedRedstoneConduit conduit) {
    conduit.setSignalColor(dir, col);
    worldObj.markBlockForUpdate(x, y, z);
  }

}
