package crazypants.enderio.machines.machine.teleport.telepad.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTeleport extends MessageTileEntity<TileEntity> {

  public enum Type {
    BEGIN,
    END,
    TELEPORT
  }

  private int entityId;
  private String playerName;
  private Type type;
  private boolean wasBlocked;

  public PacketTeleport() {
    super();
  }

  public PacketTeleport(Type type, TileTelePad te, Entity entity) {
    super(te.getTileEntity());

    if (entity instanceof EntityPlayer) {
      EntityPlayer ep = (EntityPlayer) entity;
      playerName = ep.getName();
      this.entityId = -1;
    } else {
      this.entityId = entity.getEntityId();
      playerName = null;
    }

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
    if (playerName != null) {
      ByteBufUtils.writeUTF8String(buf, playerName);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    entityId = buf.readInt();
    type = Type.values()[buf.readInt()];
    wasBlocked = buf.readBoolean();
    if (entityId == -1) {
      playerName = ByteBufUtils.readUTF8String(buf);
    }
  }

  public static class Handler implements IMessageHandler<PacketTeleport, IMessage> {

    @Override
    public IMessage onMessage(PacketTeleport message, MessageContext ctx) {
      World world = message.getWorld(ctx);
      TileEntity te = message.getTileEntity(world);
      if (te instanceof TileTelePad) {

        Entity e;
        if (message.playerName != null) {
          e = world.getPlayerEntityByName(message.playerName);
        } else {
          e = world.getEntityByID(message.entityId);
        }
        switch (message.type) {
        case BEGIN:
          ((TileTelePad) te).enqueueTeleport(e, false);
          break;
        case END:
          ((TileTelePad) te).dequeueTeleport(e, false);
          break;
        case TELEPORT:
          ((TileTelePad) te).setBlocked(message.wasBlocked);
          break;
        }
      }
      return null;
    }
  }

}
