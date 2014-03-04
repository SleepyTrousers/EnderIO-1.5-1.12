package crazypants.enderio.conduit.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemFilter;
import crazypants.util.DyeColor;

public class PacketItemConduitFilter extends AbstractConduitPacket<IItemConduit> {

  private ForgeDirection dir;
  private boolean loopMode;
  private DyeColor colIn;
  private DyeColor colOut;

  private ItemFilter inputFilter;
  private ItemFilter outputFilter;

  public PacketItemConduitFilter() {
  }

  public PacketItemConduitFilter(IItemConduit con, ForgeDirection dir) {
    super(con.getBundle().getEntity(), ConTypeEnum.ITEM);
    this.dir = dir;
    loopMode = con.isSelfFeedEnabled(dir);
    colIn = con.getInputColor(dir);
    colOut = con.getOutputColor(dir);

    inputFilter = con.getInputFilter(dir);
    outputFilter = con.getOutputFilter(dir);
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    buf.writeShort(dir.ordinal());
    buf.writeBoolean(loopMode);
    buf.writeShort(colIn.ordinal());
    buf.writeShort(colOut.ordinal());
    writeFilter(buf, inputFilter);
    writeFilter(buf, outputFilter);
  }

  private void writeFilter(ByteBuf buf, ItemFilter filter) {
    buf.writeBoolean(filter.isBlacklist());
    buf.writeBoolean(filter.isMatchMeta());
    buf.writeBoolean(filter.isMatchNBT());
    buf.writeBoolean(filter.isUseOreDict());
    buf.writeBoolean(filter.isSticky());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    dir = ForgeDirection.values()[buf.readShort()];
    loopMode = buf.readBoolean();
    colIn = DyeColor.values()[buf.readShort()];
    colOut = DyeColor.values()[buf.readShort()];
    inputFilter = readFilter(buf);
    outputFilter = readFilter(buf);
  }

  private ItemFilter readFilter(ByteBuf data) {

    ItemFilter itemFilter = new ItemFilter();
    itemFilter.setBlacklist(data.readBoolean());
    itemFilter.setMatchMeta(data.readBoolean());
    itemFilter.setMatchNBT(data.readBoolean());
    itemFilter.setUseOreDict(data.readBoolean());
    itemFilter.setSticky(data.readBoolean());
    return itemFilter;
  }

  @Override
  protected void handleServerSide(EntityPlayer player, World worldObj, IConduitBundle tile, IItemConduit conduit) {
    conduit.setSelfFeedEnabled(dir, loopMode);
    conduit.setInputColor(dir, colIn);
    conduit.setOutputColor(dir, colOut);

    applyFilter(conduit, inputFilter, true);
    applyFilter(conduit, outputFilter, false);

    worldObj.markBlockForUpdate(x, y, z);
  }

  private void applyFilter(IItemConduit conduit, ItemFilter filter, boolean isInput) {
    if(filter == null) {
      if(isInput) {
        conduit.setInputFilter(dir, filter);
      } else {
        conduit.setOutputFilter(dir, filter);
      }
      return;
    }

    ItemFilter itemFilter = isInput ? conduit.getInputFilter(dir) : conduit.getOutputFilter(dir);
    itemFilter.setBlacklist(filter.isBlacklist());
    itemFilter.setMatchMeta(filter.isMatchMeta());
    itemFilter.setMatchNBT(filter.isMatchNBT());
    itemFilter.setUseOreDict(filter.isUseOreDict());
    itemFilter.setSticky(filter.isSticky());

  }

}
