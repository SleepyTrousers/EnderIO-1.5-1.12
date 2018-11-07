package crazypants.enderio.base.capacitor;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.api.capacitor.Scaler;
import crazypants.enderio.base.Log;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCapacitorSync implements IMessage {

  final @Nonnull NNList<Triple<ResourceLocation, Integer, String>> data = new NNList<>();

  public PacketCapacitorSync() {
  }

  public PacketCapacitorSync(@Nonnull Map<ICapacitorKey, Triple<Integer, Scaler, String>> map) {
    for (Entry<ICapacitorKey, Triple<Integer, Scaler, String>> entry : map.entrySet()) {
      ICapacitorKey key = entry.getKey();
      Triple<Integer, Scaler, String> value = entry.getValue();
      if (key != null && value != null) {
        ResourceLocation registryName = key.getRegistryName();
        Integer baseValue = value.getLeft();
        String scaler = value.getRight();
        if (baseValue != null && scaler != null) {
          data.add(Triple.of(registryName, baseValue, scaler));
        }
      }
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(data.size());
    for (Triple<ResourceLocation, Integer, String> triple : data) {
      ByteBufUtils.writeUTF8String(buf, triple.getLeft().toString());
      buf.writeInt(triple.getMiddle());
      ByteBufUtils.writeUTF8String(buf, triple.getRight());
    }
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    int size = buf.readInt();
    for (int i = 0; i < size; i++) {
      String name = ByteBufUtils.readUTF8String(buf);
      int baseValue = buf.readInt();
      String scaler = ByteBufUtils.readUTF8String(buf);
      ResourceLocation registryName = new ResourceLocation(NullHelper.first(name, ""));
      data.add(Triple.of(registryName, baseValue, scaler));
    }
  }

  public static class Handler implements IMessageHandler<PacketCapacitorSync, IMessage> {
    @Override
    public IMessage onMessage(PacketCapacitorSync message, MessageContext ctx) {
      if (!Minecraft.getMinecraft().isIntegratedServerRunning()) {
        for (Triple<ResourceLocation, Integer, String> triple : message.data) {
          CapacitorKeyRegistry.addOverride(NullHelper.notnull(triple.getLeft(), "internal error"), triple.getMiddle(), triple.getRight());
        }
        Log.debug("Added server config overrides for capacitor keys");
      } else {
        Log.debug("Ignoring server config overrides for capacitor keys in sinple player");
      }
      return null;
    }
  }

}
