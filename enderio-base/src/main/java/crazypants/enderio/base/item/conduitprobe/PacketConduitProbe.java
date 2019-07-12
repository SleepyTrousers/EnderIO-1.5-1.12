package crazypants.enderio.base.item.conduitprobe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConduitProbe implements IMessage {

  public static boolean canCreatePacket(@Nonnull World world, @Nonnull BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof IConduitBundle) {
      return true;
    }
    if (te instanceof ILegacyPoweredTile) {
      return true;
    }
    if (te instanceof IHasConduitProbeData) {
      return true;
    }
    return false;
  }

  private long pos;
  private EnumFacing side;

  public PacketConduitProbe() {
  }

  public PacketConduitProbe(@Nonnull BlockPos pos, EnumFacing side) {
    this.pos = pos.toLong();
    this.side = side;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
    if (side == null) {
      buf.writeShort(-1);
    } else {
      buf.writeShort(side.ordinal());
    }

  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    pos = buffer.readLong();
    short ord = buffer.readShort();
    if (ord < 0) {
      side = null;
    } else {
      side = EnumFacing.VALUES[ord];
    }
  }

  public static class Handler implements IMessageHandler<PacketConduitProbe, IMessage> {

    private static final @Nonnull TextComponentString NOTEXT = new TextComponentString("");

    @Override
    public IMessage onMessage(PacketConduitProbe message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      World world = player.world;
      BlockPos pos = BlockPos.fromLong(message.pos);
      if (!world.isBlockLoaded(pos)) {
        return null;
      }

      TileEntity te = world.getTileEntity(pos);
      if (te instanceof IHasConduitProbeData) {
        ((IHasConduitProbeData) te).getConduitProbeInformation(player, message.side).forEach(elem -> player.sendMessage((NullHelper.first(elem, NOTEXT))));
      }
      return null;
    }

  }

  public interface IHasConduitProbeData {

    @Nonnull
    NNList<ITextComponent> getConduitProbeInformation(@Nonnull EntityPlayer player, @Nullable EnumFacing side);

  }

}
