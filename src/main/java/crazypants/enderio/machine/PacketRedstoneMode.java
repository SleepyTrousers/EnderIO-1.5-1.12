package crazypants.enderio.machine;

import com.enderio.core.common.network.MessageTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRedstoneMode extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketRedstoneMode, IMessage> {

  private RedstoneControlMode mode;

  public PacketRedstoneMode() {
  }

  public <T extends TileEntity & IRedstoneModeControlable> PacketRedstoneMode(T cont) {
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

  @Override
  public IMessage onMessage(PacketRedstoneMode message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = message.getTileEntity(player.worldObj);
    if (te instanceof IRedstoneModeControlable) {
      IRedstoneModeControlable me = (IRedstoneModeControlable) te;
      me.setRedstoneControlMode(message.mode);
    }
    return null;
  }

}
