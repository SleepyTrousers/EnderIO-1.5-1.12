package crazypants.enderio.teleport.telepad;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.network.MessageTileEntity;

public class PacketTeleport extends MessageTileEntity<TileTelePad> implements IMessageHandler<PacketTeleport, IMessage> {

  enum Type {
    BEGIN,
    END
  }

  public PacketTeleport() {
    super();
  }

  private int entityId;
  private Type type;

  public PacketTeleport(Type type, TileTelePad te, int entityId) {
    super(te);
    this.entityId = entityId;
    this.type = type;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(entityId);
    buf.writeInt(type.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    entityId = buf.readInt();
    type = Type.values()[buf.readInt()];
  }

  @Override
  public IMessage onMessage(PacketTeleport message, MessageContext ctx) {
    World world = ctx.side.isClient() ? EnderIO.proxy.getClientWorld() : message.getWorld(ctx);
    TileEntity te = message.getTileEntity(world);
    if(te instanceof TileTelePad) {
      if(message.type == Type.BEGIN) {
        ((TileTelePad) te).enqueueTeleport(world.getEntityByID(message.entityId), false);
      } else {
        ((TileTelePad) te).dequeueTeleport(world.getEntityByID(message.entityId), false);
      }
    }
    return null;
  }

}
