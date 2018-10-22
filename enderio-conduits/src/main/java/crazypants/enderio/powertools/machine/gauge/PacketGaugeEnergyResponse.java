package crazypants.enderio.powertools.machine.gauge;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGaugeEnergyResponse extends MessageTileEntity<TileGauge> {

  Map<EnumFacing, Float> data;

  public PacketGaugeEnergyResponse() {
  }

  public PacketGaugeEnergyResponse(@Nonnull TileGauge gauge) {
    super(gauge);
    gauge.collectData();
    data = gauge.data;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if (data != null) {
      for (Entry<EnumFacing, Float> entry : data.entrySet()) {
        final EnumFacing face = entry.getKey();
        final Float value = entry.getValue();
        if (face != null && value != null) {
          buf.writeByte(face.ordinal());
          buf.writeFloat(value);
        }
      }
      buf.writeByte(42);
    } else {
      buf.writeByte(87);
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    data = new EnumMap<>(EnumFacing.class);
    while (true) {
      byte b = buf.readByte();
      if (b > 5) {
        if (b == 87) {
          data = null;
        }
        return;
      }
      EnumFacing face = EnumFacing.values()[b];
      float value = buf.readFloat();
      data.put(face, value);
    }
  }

  public static class Handler implements IMessageHandler<PacketGaugeEnergyResponse, IMessage> {

    @Override
    public IMessage onMessage(PacketGaugeEnergyResponse message, MessageContext ctx) {
      TileGauge gauge = message.getTileEntity(message.getWorld(ctx));
      if (gauge != null) {
        gauge.data = message.data;
      }
      return null;
    }
  }

}
