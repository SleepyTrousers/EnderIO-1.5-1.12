package crazypants.enderio.machine.capbank.packet;

import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNetworkStateRequest extends PacketCapBank<PacketNetworkStateRequest, PacketNetworkStateResponse> {

  public PacketNetworkStateRequest() {
  }

  public PacketNetworkStateRequest(TileCapBank capBank) {
    super(capBank);
  }

  @Override
  protected PacketNetworkStateResponse handleMessage(TileCapBank te, PacketNetworkStateRequest message, MessageContext ctx) {
    ICapBankNetwork network = te.getNetwork();
    return network == null ? null : new PacketNetworkStateResponse(network);
  }

}
