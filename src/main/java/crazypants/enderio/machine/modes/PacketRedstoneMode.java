package crazypants.enderio.machine.modes;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.machine.interfaces.IRedstoneModeControlable;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRedstoneMode extends MessageTileEntity<TileEntity> {

  private RedstoneControlMode mode;

  public PacketRedstoneMode() {
  }

  public <T extends TileEntity & IRedstoneModeControlable> PacketRedstoneMode(@Nonnull T cont) {
    super(cont);
    mode = cont.getRedstoneControlMode();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort((short) mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ordinal = buf.readShort();
    mode = RedstoneControlMode.values()[ordinal];
  }

  public static class Handler implements IMessageHandler<PacketRedstoneMode, IMessage> {

    @Override
    public IMessage onMessage(PacketRedstoneMode message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      TileEntity te = message.getTileEntity(player.world);
      final RedstoneControlMode mode = message.mode;
      if (mode != null && te instanceof IRedstoneModeControlable) {
        IRedstoneModeControlable me = (IRedstoneModeControlable) te;
        me.setRedstoneControlMode(mode);
      }
      return null;
    }

  }

}
