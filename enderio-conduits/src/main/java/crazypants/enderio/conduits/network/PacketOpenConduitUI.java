package crazypants.enderio.conduits.network;

import com.enderio.core.common.network.MessageTileEntity;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;


public class PacketOpenConduitUI extends MessageTileEntity<TileEntity> {

  private @Nonnull EnumFacing dir = EnumFacing.DOWN;

  public PacketOpenConduitUI() {
  }

  public PacketOpenConduitUI(@Nonnull TileEntity tile, @Nonnull EnumFacing dir) {
    super(tile);
    this.dir = dir;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(dir.ordinal());
  }

  @SuppressWarnings("null")
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ord = buf.readShort();
    if (ord >= 0 && ord < EnumFacing.values().length) {
      dir = EnumFacing.values()[ord];
    }
  }

  public static class Handler implements IMessageHandler<PacketOpenConduitUI, IMessage> {

    @Override
    public IMessage onMessage(PacketOpenConduitUI message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      ConduitRegistry.getConduitModObjectNN().openGui(player.world, message.getPos(), player, message.dir, message.dir.ordinal());
      return null;
    }
  }
}
