package crazypants.enderio.powertools.machine.gauge;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.power.IPowerInterface;
import crazypants.enderio.powertools.config.GaugeConfig;
import crazypants.enderio.powertools.network.PacketHandler;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.util.EnumFacing;

@Storable
public class TileGauge extends TileEntityEio {

  // Note: TE is only needed for the TESR

  // client
  protected long lastRequest = -1;

  // server
  protected long lastResponse = -1;

  // both
  // 'null' on the client means 'no data received from server yet'
  protected Map<EnumFacing, Float> data;

  protected void collectData() {
    if (!world.isRemote) {
      if (lastResponse + GaugeConfig.updateLimit.get() < EnderIO.proxy.getServerTickCount()) {
        if (data == null) {
          data = new EnumMap<>(EnumFacing.class);
        } else {
          data.clear();
        }
        for (Entry<EnumFacing, IPowerInterface> side : BlockGauge.getDisplays(world, getPos()).entrySet()) {
          IPowerInterface eh = side.getValue();
          EnumFacing face = side.getKey().getOpposite();
          int energyStored = eh.getEnergyStored();
          int maxEnergyStored = eh.getMaxEnergyStored();
          float ratio = maxEnergyStored > 0 ? (float) energyStored / (float) maxEnergyStored : 0f;
          data.put(face, ratio);
        }
        lastResponse = EnderIO.proxy.getServerTickCount();
      }
    } else {
      if (lastRequest + GaugeConfig.updates.get() < EnderIO.proxy.getTickCount()) {
        PacketHandler.INSTANCE.sendToServer(new PacketGaugeEnergyRequest(this));
        lastRequest = EnderIO.proxy.getTickCount();
      }
    }
  }

}
