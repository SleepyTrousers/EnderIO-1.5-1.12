package crazypants.enderio.conduits.network;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.conduits.conduit.item.IItemConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemConduitFilter extends AbstractConduitPacket<IItemConduit> {

  private EnumFacing dir;
  private boolean loopMode;
  private boolean roundRobin;
  private DyeColor colIn;
  private DyeColor colOut;
  private int priority;

  private IItemFilter inputFilter;
  private IItemFilter outputFilter;

  public PacketItemConduitFilter() {
  }

  public PacketItemConduitFilter(IItemConduit con, EnumFacing dir) {
    super(con.getBundle().getEntity(), con);
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
    super.toBytes(buf);
    if (dir == null) {
      buf.writeShort(-1);
    } else {
      buf.writeShort(dir.ordinal());
    }
    buf.writeBoolean(loopMode);
    buf.writeBoolean(roundRobin);
    buf.writeInt(priority);
    buf.writeShort(colIn.ordinal());
    buf.writeShort(colOut.ordinal());
    FilterRegistry.writeFilter(buf, inputFilter);
    FilterRegistry.writeFilter(buf, outputFilter);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ord = buf.readShort();
    if (ord < 0) {
      dir = null;
    } else {
      dir = EnumFacing.values()[ord];
    }
    loopMode = buf.readBoolean();
    roundRobin = buf.readBoolean();
    priority = buf.readInt();
    colIn = DyeColor.values()[buf.readShort()];
    colOut = DyeColor.values()[buf.readShort()];
    inputFilter = FilterRegistry.readFilter(buf);
    outputFilter = FilterRegistry.readFilter(buf);
  }

  public static class Handler implements IMessageHandler<PacketItemConduitFilter, IMessage> {

    @Override
    public IMessage onMessage(PacketItemConduitFilter message, MessageContext ctx) {
      IItemConduit conduit = message.getConduit(ctx);
      if (conduit != null) {
        conduit.setSelfFeedEnabled(message.dir, message.loopMode);
        conduit.setRoundRobinEnabled(message.dir, message.roundRobin);
        conduit.setInputColor(message.dir, message.colIn);
        conduit.setOutputColor(message.dir, message.colOut);
        conduit.setOutputPriority(message.dir, message.priority);
        applyFilter(message.dir, conduit, message.inputFilter, true);
        applyFilter(message.dir, conduit, message.outputFilter, false);

        IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
        message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      }
      return null;
    }

    private void applyFilter(EnumFacing dir, IItemConduit conduit, IItemFilter filter, boolean isInput) {
      if (isInput) {
        conduit.setInputFilter(dir, filter);
      } else {
        conduit.setOutputFilter(dir, filter);
      }
      return;
    }
  }
}
