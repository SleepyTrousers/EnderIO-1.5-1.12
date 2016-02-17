package crazypants.enderio.machine.capbank.packet;

import crazypants.enderio.machine.capbank.TileCapBank;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNetworkEnergyRequest extends PacketCapBank<PacketNetworkEnergyRequest, PacketNetworkEnergyResponse> {

  public PacketNetworkEnergyRequest() {
  }

  public PacketNetworkEnergyRequest(TileCapBank capBank) {
    super(capBank);
  }

  @Override
  protected PacketNetworkEnergyResponse handleMessage(TileCapBank te, PacketNetworkEnergyRequest message, MessageContext ctx) {
    if(te.getNetwork() != null) {
      return new PacketNetworkEnergyResponse(te.getNetwork());
    }
    return null;
  }

}
