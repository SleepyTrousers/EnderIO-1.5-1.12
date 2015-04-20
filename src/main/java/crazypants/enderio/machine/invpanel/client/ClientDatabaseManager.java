package crazypants.enderio.machine.invpanel.client;

import java.util.HashMap;

public class ClientDatabaseManager {

  public static final ClientDatabaseManager INSTANCE = new ClientDatabaseManager();

  private final HashMap<Integer, InventoryDatabaseClient> dbRegistry = new HashMap<Integer, InventoryDatabaseClient>();

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
}
