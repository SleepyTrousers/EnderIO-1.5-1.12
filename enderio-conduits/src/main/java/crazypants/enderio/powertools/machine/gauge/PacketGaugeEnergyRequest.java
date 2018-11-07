package crazypants.enderio.powertools.machine.gauge;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGaugeEnergyRequest extends MessageTileEntity<TileGauge> {

  public PacketGaugeEnergyRequest() {
  }

  public PacketGaugeEnergyRequest(@Nonnull TileGauge gauge) {
    super(gauge);
  }

  public static class Handler implements IMessageHandler<PacketGaugeEnergyRequest, PacketGaugeEnergyResponse> {

    @Override
    public PacketGaugeEnergyResponse onMessage(PacketGaugeEnergyRequest message, MessageContext ctx) {
      final TileGauge gauge = message.getTileEntity(message.getWorld(ctx));
      return gauge != null ? new PacketGaugeEnergyResponse(gauge) : null;
    }

  }
}
