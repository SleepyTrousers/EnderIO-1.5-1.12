package crazypants.enderio.conduit.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.IExtractor;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.util.DyeColor;

public class PacketExtractMode extends AbstractConduitPacket<IExtractor> {

  private ForgeDirection dir;
  private RedstoneControlMode mode;
  private DyeColor color;

  public PacketExtractMode() {
  }

  public PacketExtractMode(IExtractor con, ForgeDirection dir) {
    super(con.getBundle().getEntity(), ConTypeEnum.get(con));
    this.dir = dir;
    mode = con.getExtractionRedstoneMode(dir);
    color = con.getExtractionSignalColor(dir);
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    buf.writeShort(dir.ordinal());
    buf.writeShort(mode.ordinal());
    buf.writeShort(color.ordinal());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    dir = ForgeDirection.values()[buf.readShort()];
    mode = RedstoneControlMode.values()[buf.readShort()];
    color = DyeColor.values()[buf.readShort()];
  }

  @Override
  protected void handle(EntityPlayer player, World worldObj, IConduitBundle tile, IExtractor conduit) {
    conduit.setExtractionRedstoneMode(mode, dir);
    conduit.setExtractionSignalColor(dir, color);
    worldObj.markBlockForUpdate(x, y, z);
  }

}
