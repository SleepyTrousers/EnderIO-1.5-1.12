package crazypants.enderio.machine.capbank.packet;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.machine.capbank.TileCapBank;
import crazypants.enderio.machine.capbank.network.ICapBankNetwork;

public class PacketNetworkStateRequest extends PacketCapBank<PacketNetworkStateRequest, PacketNetworkStateResponse> {

    public PacketNetworkStateRequest() {}

    public PacketNetworkStateRequest(TileCapBank capBank) {
        super(capBank);
    }

    @Override
    protected PacketNetworkStateResponse handleMessage(TileCapBank te, PacketNetworkStateRequest message,
            MessageContext ctx) {
        ICapBankNetwork network = te.getNetwork();
        return network == null ? null : new PacketNetworkStateResponse(network);
    }
}
