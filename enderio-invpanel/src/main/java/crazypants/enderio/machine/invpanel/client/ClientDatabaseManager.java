package crazypants.enderio.machine.invpanel.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

import java.util.HashMap;

import crazypants.enderio.base.Log;

public class ClientDatabaseManager {

  public static final ClientDatabaseManager INSTANCE = new ClientDatabaseManager();

  private final HashMap<Integer, InventoryDatabaseClient> dbRegistry = new HashMap<Integer, InventoryDatabaseClient>();

  private ClientDatabaseManager() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  public InventoryDatabaseClient getOrCreateDatabase(int generation) {
    InventoryDatabaseClient db = dbRegistry.get(generation);
    if(db == null) {
      db = new InventoryDatabaseClient(generation);
      dbRegistry.put(generation, db);
    }
    return db;
  }

  public void destroyDatabase(int generation) {
    dbRegistry.remove(generation);
  }

  @SubscribeEvent
  public void on(ClientDisconnectionFromServerEvent event) {
    Log.info("Clearing Inventory Panel Client Database");
    dbRegistry.clear();
  }
}
