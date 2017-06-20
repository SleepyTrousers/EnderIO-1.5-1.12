package crazypants.enderio.item.conduitprobe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.ChatUtil;

import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.power.ILegacyPoweredTile;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
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
    @Override
    public IMessage onMessage(PacketConduitProbe message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      World world = player.world;
      BlockPos pos = BlockPos.fromLong(message.pos);
      if (!world.isBlockLoaded(pos)) {
        return null;
      }

      TileEntity te = world.getTileEntity(pos);
      // } else if (te instanceof ILegacyPowerReceiver) {
      // TODO 1.11 migrate this branch to IHasConduitProbeData on the machines/capBank
      if (te instanceof IHasConduitProbeData) {
        ChatUtil.sendNoSpam(player, ((IHasConduitProbeData) te).getConduitProbeData(player, message.side));
      }
      return null;
    }

  }

  public static interface IHasConduitProbeData {

    @Nonnull
    String[] getConduitProbeData(@Nonnull EntityPlayer player, @Nullable EnumFacing side);

  }

}
