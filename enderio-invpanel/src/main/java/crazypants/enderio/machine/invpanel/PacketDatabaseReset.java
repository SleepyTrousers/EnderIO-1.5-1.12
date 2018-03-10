package crazypants.enderio.machine.invpanel;

import crazypants.enderio.machine.invpanel.client.ClientDatabaseManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDatabaseReset implements IMessage {

  private int generation;

  public PacketDatabaseReset() {
  }

  public PacketDatabaseReset(int generation) {
    this.generation = generation;
  }

  @Override
  public void fromBytes(ByteBuf bb) {
    this.generation = bb.readInt();
  }

  @Override
  public void toBytes(ByteBuf bb) {
    bb.writeInt(generation);
  }

  public static class Handler implements IMessageHandler<PacketDatabaseReset, IMessage> {

    @Override
    public IMessage onMessage(PacketDatabaseReset message, MessageContext ctx) {
      ClientDatabaseManager.INSTANCE.destroyDatabase(message.generation);
      return null;
    }
  }
}
