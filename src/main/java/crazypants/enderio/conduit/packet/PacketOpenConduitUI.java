package crazypants.enderio.conduit.packet;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.GuiID;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenConduitUI extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketOpenConduitUI, IMessage> {

  private EnumFacing dir;

  public PacketOpenConduitUI() {
  }

  public PacketOpenConduitUI(TileEntity tile, EnumFacing dir) {
    super(tile);
    this.dir = dir;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if(dir != null) {
      buf.writeShort(dir.ordinal());
    } else {
      buf.writeShort(-1);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ord = buf.readShort();
    if(ord < 0) {
      dir = null;
    } else {
      dir = EnumFacing.values()[ord];
    }
  }

  @Override
  public IMessage onMessage(PacketOpenConduitUI message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity tile = message.getWorld(ctx).getTileEntity(message.getPos());
    GuiID.facing2guiid(message.dir).openGui(player.worldObj, tile.getPos(), player, message.dir);
    return null;
  }

}
