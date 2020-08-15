package crazypants.enderio.invpanel.conduit.data;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.invpanel.capability.IDatabaseHandler;
import crazypants.enderio.base.invpanel.capability.InventoryDatabaseSource;
import crazypants.enderio.base.invpanel.database.IInventoryDatabaseServer;
import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import crazypants.enderio.invpanel.server.InventoryDatabaseServer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class DataConduitNetwork extends AbstractConduitNetwork<IDataConduit, IDataConduit> implements IDatabaseHandler {

  private final @Nonnull NNList<InventoryDatabaseSource> inventories = new NNList<InventoryDatabaseSource>();

  private @Nullable IInventoryDatabaseServer database = null;

  private int changeCount = 0;

  public DataConduitNetwork() {
    super(IDataConduit.class, IDataConduit.class);
  }

  @SuppressWarnings("null")
  @Override
  public void addConduit(@Nonnull IDataConduit con) {
    super.addConduit(con);

    for (EnumFacing dir : con.getExternalConnections()) {
      con.checkConnections(dir);
    }
  }

  public void addSource(@Nonnull InventoryDatabaseSource source) {
    if (!inventories.contains(source)) {
      inventories.add(source);
      changeCount++;
    }
  }

  public void removeSource(InventoryDatabaseSource source) {
    if (source != null) {
      inventories.remove(source);
      changeCount++;
    }
  }

  @SuppressWarnings("null")
  @Override
  @Nonnull
  public IInventoryDatabaseServer getDatabase() {
    check: {
      if (database == null) {
        database = new InventoryDatabaseServer(this);
      } else if (database.isCurrent()) {
        break check;
      }
      database.updateNetworkSources();
    }
    return database;
  }

  @Override
  public int getChangeCount() {
    return changeCount;
  }

  @Override
  @Nonnull
  public List<InventoryDatabaseSource> getSources() {
    return inventories;
  }

  @Override
  public void destroyNetwork() {
    super.destroyNetwork();
    if (database != null) {
      database.resetDatabase();
      database = null;
    }
  }

  @Override
  public void tickStart(ServerTickEvent event, @Nullable Profiler profiler) {
    super.tickStart(event, profiler);

  }

}
