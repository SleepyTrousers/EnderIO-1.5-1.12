package crazypants.enderio.conduits.network;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.IExtractor;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.util.EnumReader;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExtractMode extends AbstractConduitPacket.Sided<IExtractor> {

  private @Nonnull RedstoneControlMode mode = RedstoneControlMode.OFF;
  private @Nonnull DyeColor color = DyeColor.BLACK;

  public PacketExtractMode() {
  }

  public PacketExtractMode(@Nonnull IExtractor con, @Nonnull EnumFacing dir) {
    super(con, dir);
    mode = con.getExtractionRedstoneMode(dir);
    color = con.getExtractionSignalColor(dir);
  }

  @Override
  public void write(@Nonnull ByteBuf buf) {
    super.write(buf);
    buf.writeShort(mode.ordinal());
    buf.writeShort(color.ordinal());
  }

  @Override
  public void read(@Nonnull ByteBuf buf) {
    super.read(buf);
    mode = EnumReader.get(RedstoneControlMode.class, buf.readShort());
    color = EnumReader.get(DyeColor.class, buf.readShort());
  }

  public static class Handler implements IMessageHandler<PacketExtractMode, IMessage> {
    @Override
    public IMessage onMessage(PacketExtractMode message, MessageContext ctx) {
      final IExtractor conduit = message.getConduit(ctx);
      if (conduit != null) {
        conduit.setExtractionRedstoneMode(message.mode, message.dir);
        conduit.setExtractionSignalColor(message.dir, message.color);
        IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
        message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);
      }
      return null;
    }
  }
}
