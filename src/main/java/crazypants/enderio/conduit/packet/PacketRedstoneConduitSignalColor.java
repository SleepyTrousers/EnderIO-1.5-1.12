package crazypants.enderio.conduit.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.util.DyeColor;

public class PacketRedstoneConduitSignalColor extends AbstractConduitPacket<IInsulatedRedstoneConduit> implements IMessageHandler<PacketRedstoneConduitSignalColor, IMessage> {

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
  public void toBytes(ByteBuf buf) {
    buf.writeShort(dir.ordinal());
    buf.writeShort(col.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    dir = ForgeDirection.values()[buf.readShort()];
    col = DyeColor.values()[buf.readShort()];
  }

  @Override
  public IMessage onMessage(PacketRedstoneConduitSignalColor message, MessageContext ctx) {
    getTileCasted(ctx).setSignalColor(dir, col);
    getWorld(ctx).markBlockForUpdate(x, y, z);
    return null;
  }

}
