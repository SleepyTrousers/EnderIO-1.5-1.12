package crazypants.enderio.conduit.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.IItemFilter;
import crazypants.enderio.conduit.item.ItemFilter;
import crazypants.enderio.network.NetworkUtil;
import crazypants.util.DyeColor;
import crazypants.util.ItemUtil;

public class PacketItemConduitFilter extends AbstractConduitPacket<IItemConduit> {

  private ForgeDirection dir;
  private boolean loopMode;
  private boolean roundRobin;
  private DyeColor colIn;
  private DyeColor colOut;
  private int priority;

  private IItemFilter inputFilter;
  private IItemFilter outputFilter;

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
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    buf.writeShort(dir.ordinal());
    buf.writeBoolean(loopMode);
    buf.writeBoolean(roundRobin);
    buf.writeInt(priority);
    buf.writeShort(colIn.ordinal());
    buf.writeShort(colOut.ordinal());
    FilterRegister.writeFilter(buf, inputFilter);
    FilterRegister.writeFilter(buf, outputFilter);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    dir = ForgeDirection.values()[buf.readShort()];
    loopMode = buf.readBoolean();
    roundRobin = buf.readBoolean();
    priority = buf.readInt();
    colIn = DyeColor.values()[buf.readShort()];
    colOut = DyeColor.values()[buf.readShort()];
    inputFilter = FilterRegister.readFilter(buf);
    outputFilter = FilterRegister.readFilter(buf);
  }

  @Override
  protected void handleServerSide(EntityPlayer player, World worldObj, IConduitBundle tile, IItemConduit conduit) {
    conduit.setSelfFeedEnabled(dir, loopMode);
    conduit.setRoundRobinEnabled(dir, roundRobin);
    conduit.setInputColor(dir, colIn);
    conduit.setOutputColor(dir, colOut);
    conduit.setOutputPriority(dir, priority);
    applyFilter(conduit, inputFilter, true);
    applyFilter(conduit, outputFilter, false);

    worldObj.markBlockForUpdate(x, y, z);
  }

  private void applyFilter(IItemConduit conduit, IItemFilter filter, boolean isInput) {
//    if(filter == null) {
      if(isInput) {
        conduit.setInputFilter(dir, filter);
      } else {
        conduit.setOutputFilter(dir, filter);
      }
      return;
//    }

//    ItemFilter itemFilter = isInput ? conduit.getInputFilter(dir) : conduit.getOutputFilter(dir);
//    itemFilter.setBlacklist(filter.isBlacklist());
//    itemFilter.setMatchMeta(filter.isMatchMeta());
//    itemFilter.setMatchNBT(filter.isMatchNBT());
//    itemFilter.setUseOreDict(filter.isUseOreDict());
//    itemFilter.setSticky(filter.isSticky());

  }

}
