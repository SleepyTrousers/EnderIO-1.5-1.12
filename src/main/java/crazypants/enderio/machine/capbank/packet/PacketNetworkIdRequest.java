package crazypants.enderio.machine.capbank.packet;

import crazypants.enderio.machine.capbank.TileCapBank;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNetworkIdRequest extends PacketCapBank<PacketNetworkIdRequest, PacketNetworkIdResponse> {

  public PacketNetworkIdRequest() {
  }

  public PacketNetworkIdRequest(TileCapBank capBank) {
    super(capBank);
  }

  @Override
  protected PacketNetworkIdResponse handleMessage(TileCapBank te, PacketNetworkIdRequest message, MessageContext ctx) {
    if(te.getNetwork() != null) {
      return new PacketNetworkIdResponse(te);
    }
    return null;
  }

}
