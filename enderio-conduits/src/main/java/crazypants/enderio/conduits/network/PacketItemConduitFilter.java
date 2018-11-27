package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.conduits.conduit.item.IItemConduit;
import info.loenwind.autoconfig.util.NullHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemConduitFilter extends AbstractConduitPacket<IItemConduit> {

  private @Nonnull EnumFacing dir = EnumFacing.DOWN;
  private boolean loopMode;
  private boolean roundRobin;
  private DyeColor colIn;
  private DyeColor colOut;
  private int priority;

  private IItemFilter inputFilter;
  private IItemFilter outputFilter;

  public PacketItemConduitFilter() {
  }

  public PacketItemConduitFilter(@Nonnull IItemConduit con, @Nonnull EnumFacing dir) {
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
    buf.writeShort(dir.ordinal());
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
    dir = NullHelper.first(EnumFacing.values()[MathHelper.clamp(buf.readShort(), 0, 5)], EnumFacing.DOWN);
    loopMode = buf.readBoolean();
    roundRobin = buf.readBoolean();
    priority = buf.readInt();
    colIn = DyeColor.fromIndex(buf.readShort());
    colOut = DyeColor.fromIndex(buf.readShort());
    inputFilter = (IItemFilter) FilterRegistry.readFilter(buf);
    outputFilter = (IItemFilter) FilterRegistry.readFilter(buf);
  }

  public static class Handler implements IMessageHandler<PacketItemConduitFilter, IMessage> {

    @Override
    public IMessage onMessage(PacketItemConduitFilter message, MessageContext ctx) {
      IItemConduit conduit = message.getConduit(ctx);
      if (conduit != null) {
        conduit.setSelfFeedEnabled(message.dir, message.loopMode);
        conduit.setRoundRobinEnabled(message.dir, message.roundRobin);
        conduit.setInputColor(message.dir, NullHelper.first(message.colIn, DyeColor.BLACK));
        conduit.setOutputColor(message.dir, NullHelper.first(message.colOut, DyeColor.BLACK));
        conduit.setOutputPriority(message.dir, message.priority);
        applyFilter(message.dir, conduit, message.inputFilter, true);
        applyFilter(message.dir, conduit, message.outputFilter, false);

        IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
        message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      }
      return null;
    }

    private void applyFilter(@Nonnull EnumFacing dir, @Nonnull IItemConduit conduit, IItemFilter filter, boolean isInput) {
      if (isInput) {
        conduit.setInputFilter(dir, filter);
      } else {
        conduit.setOutputFilter(dir, filter);
      }
      return;
    }
  }
}
