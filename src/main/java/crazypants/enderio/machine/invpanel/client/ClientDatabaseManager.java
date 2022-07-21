package crazypants.enderio.machine.invpanel.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import crazypants.enderio.Log;
import java.util.HashMap;

public class ClientDatabaseManager {

    public static final ClientDatabaseManager INSTANCE = new ClientDatabaseManager();

    public ClientDatabaseManager() {
        FMLCommonHandler.instance().bus().register(this);
    }

    private final HashMap<Integer, InventoryDatabaseClient> dbRegistry =
            new HashMap<Integer, InventoryDatabaseClient>();

    public InventoryDatabaseClient getOrCreateDatabase(int generation) {
        InventoryDatabaseClient db = dbRegistry.get(generation);
        if (db == null) {
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
