package crazypants.enderio.conduit.packet;

import com.enderio.core.common.BlockEnder;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IConduitBundle;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.conduit.TileConduitBundle;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class AbstractConduitPacket<T extends IConduit> extends AbstractConduitBundlePacket {

  private UUID uuid;

  public AbstractConduitPacket() {
  }

  public AbstractConduitPacket(TileEntity tile, T conduit) {
    super(tile);
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
  public T getTileCasted(MessageContext ctx) {
    World world = getWorld(ctx);
    if (world == null) {
      return null;
    }
    IConduitBundle te = BlockEnder.getAnyTileEntitySafe(world, getPos(), TileConduitBundle.class);
    if (te == null) {
      return null;
    }
    return (T) te.getConduit(getConType());
  }
}
