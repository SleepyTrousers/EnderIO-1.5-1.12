package crazypants.enderio.powertools.machine.capbank.packet;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class PacketCapBank<T extends PacketCapBank<?, ?>, Q extends IMessage> extends MessageTileEntity<TileCapBank> {

  public PacketCapBank() {
    super();
  }

  public PacketCapBank(@Nonnull TileCapBank capBank) {
    super(capBank);
  }

  protected abstract Q handleMessage(TileCapBank te, T message, MessageContext ctx);

  public static final class Handler<T extends PacketCapBank<?, ?>> implements IMessageHandler<T, IMessage> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public IMessage onMessage(T message, MessageContext ctx) {
      TileCapBank te = message.getTileEntity(message.getWorld(ctx));
      if (te == null) {
        return null;
      }
      return ((PacketCapBank) message).handleMessage(te, message, ctx);
    }
  }
}
