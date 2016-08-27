package crazypants.enderio.teleport.telepad.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;
import crazypants.enderio.teleport.telepad.TileTelePad;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTeleport extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketTeleport, IMessage> {

  public enum Type {
    BEGIN,
    END,
    TELEPORT
  }

  private int entityId;
  private Type type;
  private boolean wasBlocked;
  
  public PacketTeleport() {
    super();
  }

  public PacketTeleport(Type type, TileTelePad te, int entityId) {
    super(te.getTileEntity());
    this.entityId = entityId;
    this.type = type;
  }
  
  public PacketTeleport(Type type, TileTelePad te, boolean wasBlocked) {
    super(te.getTileEntity());
    this.wasBlocked = wasBlocked;
    this.type = type;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(entityId);
    buf.writeInt(type.ordinal());
    buf.writeBoolean(wasBlocked);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    entityId = buf.readInt();
    type = Type.values()[buf.readInt()];
    wasBlocked = buf.readBoolean();
  }

  @Override
  public IMessage onMessage(PacketTeleport message, MessageContext ctx) {
    World world = ctx.side.isClient() ? EnderIO.proxy.getClientWorld() : message.getWorld(ctx);
    TileEntity te = message.getTileEntity(world);
    if(te instanceof TileTelePad) {
      Entity e = world.getEntityByID(message.entityId);
        switch(message.type) {
        case BEGIN:
          ((TileTelePad) te).enqueueTeleport(e, false);
          break;
        case END:
          ((TileTelePad) te).dequeueTeleport(e, false);
          break;
        case TELEPORT:
          ((TileTelePad)te).setBlocked(message.wasBlocked);
          break;
        }
    }
    return null;
  }

}
