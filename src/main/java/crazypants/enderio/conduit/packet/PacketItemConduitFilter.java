package crazypants.enderio.conduit.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemFilter;
import crazypants.util.DyeColor;

public class PacketItemConduitFilter extends AbstractConduitPacket<IItemConduit> implements IMessageHandler<PacketItemConduitFilter, IMessage> {

  private ForgeDirection dir;
  private boolean loopMode;
  private boolean roundRobin;
  private DyeColor colIn;
  private DyeColor colOut;
  private int priority;

  private ItemFilter inputFilter;
  private ItemFilter outputFilter;

  public PacketItemConduitFilter() {
  }

  public PacketItemConduitFilter(IItemConduit con, ForgeDirection dir) {
    super(con.getBundle().getEntity(), ConTypeEnum.ITEM);
    this.dir = dir;
    loopMode = con.isSelfFeedEnabled(dir);
    roundRobin = con.isRoundRobinEnabled(dir);
    colIn = con.getInputColor(dir);
    colOut = con.getOutputColor(dir);
    priority = con.getOutputPriority(dir);

    inputFilter = con.getInputFilter(dir);
    outputFilter = con.getOutputFilter(dir);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeShort(dir.ordinal());
    buf.writeBoolean(loopMode);
    buf.writeBoolean(roundRobin);
    buf.writeInt(priority);
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
  public void fromBytes(ByteBuf buf) {
    dir = ForgeDirection.values()[buf.readShort()];
    loopMode = buf.readBoolean();
    roundRobin = buf.readBoolean();
    priority = buf.readInt();
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
  public IMessage onMessage(PacketItemConduitFilter message, MessageContext ctx) {
    IItemConduit conduit = message.getTileCasted(ctx);
    conduit.setSelfFeedEnabled(message.dir, message.loopMode);
    conduit.setRoundRobinEnabled(message.dir, message.roundRobin);
    conduit.setInputColor(message.dir, message.colIn);
    conduit.setOutputColor(message.dir, message.colOut);
    conduit.setOutputPriority(message.dir, message.priority);
    applyFilter(message.dir, conduit, message.inputFilter, true);
    applyFilter(message.dir, conduit, message.outputFilter, false);

    message.getWorld(ctx).markBlockForUpdate(message.x, message.y, message.z);
    return null;
  }

  private void applyFilter(ForgeDirection dir, IItemConduit conduit, ItemFilter filter, boolean isInput) {
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
