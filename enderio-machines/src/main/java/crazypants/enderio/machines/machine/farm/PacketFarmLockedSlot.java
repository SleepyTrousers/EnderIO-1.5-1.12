package crazypants.enderio.machines.machine.farm;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFarmLockedSlot extends MessageTileEntity<TileFarmStation> {

  public PacketFarmLockedSlot() {
  }

  private int buttonID;

  public PacketFarmLockedSlot(@Nonnull TileFarmStation tile, int buttonID) {
    super(tile);
    this.buttonID = buttonID;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(buttonID);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    buttonID = buf.readInt();
  }

  public static class Handler implements IMessageHandler<PacketFarmLockedSlot, IMessage> {

    @Override
    public IMessage onMessage(PacketFarmLockedSlot message, MessageContext ctx) {
      TileFarmStation te = message.getTileEntity(message.getWorld(ctx));
      if (te != null) {
        te.toggleLockedState(message.buttonID);
      }
      return null;
    }
  }

}
