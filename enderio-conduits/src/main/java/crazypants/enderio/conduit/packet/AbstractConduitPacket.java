package crazypants.enderio.conduit.packet;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.conduit.gui.ExternalConnectionContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class AbstractConduitPacket<T extends IConduit> extends AbstractConduitBundlePacket {

  private UUID uuid;

  public AbstractConduitPacket() {
  }

  @Deprecated
  public AbstractConduitPacket(@Nonnull TileEntity tile, @Nonnull T conduit) {
    super(tile);
    this.uuid = ConduitRegistry.get(conduit).getNetworkUUID();
  }

  public AbstractConduitPacket(@Nonnull T conduit) {
    super(conduit.getBundle().getEntity());
    this.uuid = ConduitRegistry.get(conduit).getNetworkUUID();
  }

  protected Class<? extends IConduit> getConType() {
    return ConduitRegistry.get(uuid).getBaseType();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeLong(uuid.getMostSignificantBits());
    buf.writeLong(uuid.getLeastSignificantBits());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    uuid = new UUID(buf.readLong(), buf.readLong());
  }

  @SuppressWarnings("unchecked")
  public T getConduit(MessageContext ctx) {
    if (ctx.side == Side.SERVER) {
      if (ctx.getServerHandler().player.openContainer instanceof ExternalConnectionContainer) {
        final TileConduitBundle tileEntity = ((ExternalConnectionContainer) ctx.getServerHandler().player.openContainer).getTileEntity();
        if (tileEntity == null || !tileEntity.getPos().equals(getPos())) {
          Log.warn("Player " + ctx.getServerHandler().player.getName() + " tried to manipulate conduit while having another conduit's GUI open!");
          return null;
        }
      } else {
        if (BlockCoord.get(ctx.getServerHandler().player).distanceSq(getPos()) > EnderIO.proxy.getReachDistanceForPlayer(ctx.getServerHandler().player)) {
          Log.warn("Player " + ctx.getServerHandler().player.getName() + " tried to manipulate conduit without having its GUI open or being near it!");
          return null;
        }
      }
    }
    World world = getWorld(ctx);
    TileEntity tileEntity = getTileEntity(world);
    if (tileEntity instanceof IConduitBundle) {
      return (T) ((IConduitBundle) tileEntity).getConduit(getConType());
    }
    return null;
  }
}
