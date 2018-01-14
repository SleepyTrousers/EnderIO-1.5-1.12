package crazypants.enderio.powertools.machine.capbank.packet;

import javax.annotation.Nonnull;

import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import crazypants.enderio.powertools.machine.capbank.network.ICapBankNetwork;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketNetworkStateRequest extends PacketCapBank<PacketNetworkStateRequest, PacketNetworkStateResponse> {

  public PacketNetworkStateRequest() {
  }

  public PacketNetworkStateRequest(@Nonnull TileCapBank capBank) {
    super(capBank);
  }

  @Override
  protected PacketNetworkStateResponse handleMessage(TileCapBank te, PacketNetworkStateRequest message, MessageContext ctx) {
    ICapBankNetwork network = te.getNetwork();
    return network == null ? null : new PacketNetworkStateResponse(network);
  }

}
