package crazypants.enderio.powertools.machine.capbank.network;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.powertools.EnderIOPowerTools;
import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(modid = EnderIOPowerTools.MODID, value = Side.CLIENT)
public class ClientNetworkManager {

  private static final @Nonnull ClientNetworkManager instance = new ClientNetworkManager();

  public static @Nonnull ClientNetworkManager getInstance() {
    return instance;
  }

  private final @Nonnull Map<Integer, WeakReference<CapBankClientNetwork>> networks = new HashMap<Integer, WeakReference<CapBankClientNetwork>>();

  ClientNetworkManager() {
  }

  public void destroyNetwork(int id) {
    WeakReference<CapBankClientNetwork> ref = networks.remove(id);
    if (ref != null) {
      CapBankClientNetwork res = ref.get();
      if (res != null) {
        res.destroyNetwork();
      }
    }
  }

  public void updateState(@Nonnull World world, int id, @Nonnull NetworkState state) {
    CapBankClientNetwork network = getOrCreateNetwork(id);
    network.setState(world, state);
  }

  public void updateEnergy(int id, long energyStored, float avgInput, float avgOutput) {
    WeakReference<CapBankClientNetwork> ref = networks.get(id);
    if (ref == null) {
      return;
    }
    CapBankClientNetwork res = ref.get();
    if (res == null) {
      return;
    }
    res.setEnergyStored(energyStored);
    res.setAverageIOPerTick(avgInput, avgOutput);
  }

  public @Nonnull CapBankClientNetwork getOrCreateNetwork(int id) {
    WeakReference<CapBankClientNetwork> ref = networks.get(id);
    CapBankClientNetwork res = ref != null ? ref.get() : null;
    if (res == null) {
      res = new CapBankClientNetwork(id);
      networks.put(id, new WeakReference<CapBankClientNetwork>(res));
    }
    return res;
  }

  public void addToNetwork(int id, @Nonnull TileCapBank tileCapBank) {
    CapBankClientNetwork network = getOrCreateNetwork(id);
    network.addMember(tileCapBank);
  }

  @SubscribeEvent
  public static void unload(WorldEvent.Unload event) {
    if (event.getWorld() instanceof WorldClient) {
      instance.networks.clear();
    }
  }

}
