package crazypants.enderio.machine.capbank.packet;

import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.ClientNetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNetworkIdResponse extends PacketCapBank<PacketNetworkIdResponse, IMessage> {

  private int id;
  
  public PacketNetworkIdResponse() {
  }

  PacketNetworkIdResponse(TileCapBank capBank) {
    super(capBank);
    if(capBank != null && capBank.getNetwork() != null) {
      id = capBank.getNetwork().getId();
    } else {
      id = -1;
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(id);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    id = buf.readInt();
  }

  @Override
  protected IMessage handleMessage(TileCapBank te, PacketNetworkIdResponse message, MessageContext ctx) {
    te.setNetworkId(message.id);
    te.setNetwork(ClientNetworkManager.getInstance().getOrCreateNetwork(message.id));
    return null;
  }

}
