package crazypants.enderio.invpanel.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.CompressedDataInput;
import com.enderio.core.common.network.CompressedDataOutput;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.invpanel.capability.IDatabaseHandler;
import crazypants.enderio.base.invpanel.capability.InventoryDatabaseSource;
import crazypants.enderio.base.invpanel.database.AbstractInventory;
import crazypants.enderio.base.invpanel.database.IChangeLog;
import crazypants.enderio.base.invpanel.database.IInventoryDatabaseServer;
import crazypants.enderio.base.invpanel.database.IInventoryPanel;
import crazypants.enderio.base.invpanel.database.IServerItemEntry;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.render.util.CompositeList;
import crazypants.enderio.invpanel.config.InvpanelConfig;
import crazypants.enderio.invpanel.database.InventoryDatabase;
import crazypants.enderio.invpanel.network.PacketDatabaseReset;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class InventoryDatabaseServer extends InventoryDatabase<IServerItemEntry> implements IInventoryDatabaseServer {

  private static final AtomicInteger nextGeneration = new AtomicInteger((int) (Math.random() * 1000));

  private final IDatabaseHandler[] dbHandlers;
  private final int[] networkChangeCounts;

  private AbstractInventory[] inventories;
  private int currentInventory;
  private IChangeLog changeLog;
  private boolean sentToClient;
  private int tickPause;
  //private float power;

  public InventoryDatabaseServer(IDatabaseHandler... dbHandlers) {
    this.dbHandlers = dbHandlers;
    this.networkChangeCounts = new int[dbHandlers.length];
  }

  @Override
  public boolean isCurrent() {
    for (int i = 0; i < dbHandlers.length; i++) {
      if (networkChangeCounts[i] != dbHandlers[i].getChangeCount()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void addChangeLog(IChangeLog cl) {
    if (changeLog == null) {
      changeLog = cl;
    } else if (changeLog instanceof ChangeLogList) {
      ((ChangeLogList) changeLog).add(cl);
    } else if (changeLog != cl) {
      changeLog = new ChangeLogList(changeLog, cl);
    }
  }

  @Override
  public void removeChangeLog(IChangeLog cl) {
    if (changeLog == cl) {
      changeLog = null;
    } else if (changeLog instanceof ChangeLogList) {
      changeLog = ((ChangeLogList) changeLog).remove(cl);
    }
  }

  @Override
  public List<IServerItemEntry> decompressMissingItems(byte[] compressed) throws IOException {
    CompressedDataInput cdi = new CompressedDataInput(compressed);
    try {
      int pktGeneration = cdi.readVariable();
      if (pktGeneration != generation) {
        return Collections.emptyList();
      }
      int numIDs = cdi.readVariable();
      ArrayList<IServerItemEntry> items = new ArrayList<>(numIDs);
      for (int i = 0; i < numIDs; i++) {
        int dbIndex = cdi.readVariable();
        if (dbIndex < complexItems.size()) {
          IServerItemEntry entry = complexItems.get(dbIndex);
          items.add(entry);
        }
      }
      return items;
    } finally {
      cdi.close();
    }
  }

  @Override
  public byte[] compressItemInfo(List<? extends IServerItemEntry> items) throws IOException {
    CompressedDataOutput cdo = new CompressedDataOutput();
    try {
      int count = items.size();
      cdo.writeVariable(count);
      for (IServerItemEntry entry : items) {
        assert entry.getDbID() >= COMPLEX_DBINDEX_START;
        int code = (entry.getDbID() - COMPLEX_DBINDEX_START) << 1;
        if (entry.getNbt() != null) {
          code |= 1;
        }
        cdo.writeVariable(code);
        cdo.writeVariable(entry.getItemID());
        cdo.writeVariable(entry.getMeta());
        if (entry.getNbt() != null) {
          CompressedStreamTools.write(entry.getNbt(), cdo);
        }
        cdo.writeVariable(entry.countItems());
      }
      return cdo.getCompressed();
    } finally {
      cdo.close();
    }
  }

  @Override
  public byte[] compressItemList() throws IOException {
    CompressedDataOutput cdo = new CompressedDataOutput();
    try {
      cdo.writeByte(0);
      for (Map.Entry<Integer, IServerItemEntry> entry : simpleRegsitry.entrySet()) {
        int count = entry.getValue().countItems();
        if (count > 0) {
          cdo.writeVariable(count);
          cdo.writeShort(entry.getKey());
        }
      }
      cdo.writeByte(0);
      int prevID = COMPLEX_DBINDEX_START;
      for (IServerItemEntry entry : complexItems) {
        if (entry != null) {
          int count = entry.countItems();
          if (count > 0) {
            cdo.writeVariable(count);
            cdo.writeVariable(entry.getDbID() - prevID);
            prevID = entry.getDbID();
          }
        }
      }
      cdo.writeByte(0);
      sentToClient = true;
      return cdo.getCompressed();
    } finally {
      cdo.close();
    }
  }

  @Override
  public byte[] compressChangedItems(Collection<? extends IServerItemEntry> items) throws IOException {
    CompressedDataOutput cdo = new CompressedDataOutput();
    try {
      cdo.writeVariable(items.size());
      for (IServerItemEntry entry : items) {
        cdo.writeVariable(entry.getDbID());
        cdo.writeVariable(entry.countItems());
      }
      return cdo.getCompressed();
    } finally {
      cdo.close();
    }
  }

  @Override
  public void resetDatabase() {
    simpleRegsitry.clear();
    complexRegistry.clear();
    complexItems.clear();
    currentInventory = 0;
    if (sentToClient) {
      PacketHandler.INSTANCE.sendToAll(new PacketDatabaseReset(generation));
      sentToClient = false;
    }
  }

  @Override
  public void updateNetworkSources() {
    resetDatabase();
    generation = nextGeneration.incrementAndGet();
    List<InventoryDatabaseSource> sources = null;
    for (int i = 0; i < dbHandlers.length; i++) {
      networkChangeCounts[i] = dbHandlers[i].getChangeCount();
      sources = CompositeList.create(sources, dbHandlers[i].getSources());
    }

    if (sources == null || sources.isEmpty()) {
      this.inventories = null;
    } else {
      this.inventories = new AbstractInventory[sources.size()];
      for (int i = 0; i < sources.size(); i++) {
        InventoryDatabaseSource inv = sources.get(i);
        inventories[i] = InventoryFactory.createInventory(inv);
      }
    }

    if (changeLog != null) {
      changeLog.databaseReset();
    }
  }

  @Override
  public int getNumInventories() {
    return (inventories == null) ? 0 : inventories.length;
  }

  @Override
  public boolean isOperational(IInventoryPanel te) {
    return te.getPowerLevel() > 0 && inventories != null;
  }

  @Override
  public int extractItems(IServerItemEntry entry, int count, @Nonnull IInventoryPanel te) {
    float availablePower = te.getPowerLevel() + te.getAvailablePower();
    availablePower -= InvpanelConfig.inventoryPanelExtractCostPerOperation.get();
    if (availablePower <= 0) {
      return 0;
    }
    if (InvpanelConfig.inventoryPanelExtractCostPerOperation.get() > 0) {
      long maxCount = Math.round(Math.floor(availablePower / InvpanelConfig.inventoryPanelExtractCostPerOperation.get()));
      count = (int) Math.min(maxCount, count);
    }
    if (count > 0) {
      int extracted = entry.extractItems(this, count);
      te.usePower(InvpanelConfig.inventoryPanelExtractCostPerOperation.get() + extracted * InvpanelConfig.inventoryPanelExtractCostPerOperation.get());
      te.refuelPower(this);
      return extracted;
    }
    return 0;
  }

  private void scanNextInventory(IInventoryPanel te) {
    if (!isOperational(te)) {
      tickPause = 20;
      return;
    }

    int currentInventoryIn = currentInventory;
    long now = EnderIO.proxy.getServerTickCount();

    do {
      AbstractInventory inv = inventories[currentInventory];
      currentInventory = (currentInventory + 1) % inventories.length;
      if (inv.shouldBeScannedNow(now)) {
        int slots = inv.scanInventory(this);
        inv.markScanned();
        tickPause += Math.min(1 + (slots + 8) / 9, 20);
        te.usePower(slots * InvpanelConfig.inventoryPanelScanCostPerSlot.get());
        return;
      }
    } while (currentInventoryIn != currentInventory);
    tickPause += 10;
  }

  /*
   * (non-Javadoc)
   * 
   * @see crazypants.enderio.machine.invpanel.server.IInventoryDatabaseServer#tick()
   */
  @Override
  public void tick(IInventoryPanel te) {
    if (--tickPause <= 0) {
      scanNextInventory(te);
    }
  }

  @Override
  public void entryChanged(IServerItemEntry entry) {
    if (changeLog != null) {
      changeLog.entryChanged(entry);
    }
  }

  @Override
  public void sendChangeLogs() {
    if (changeLog != null) {
      changeLog.sendChangeLog();
    }
  }

  @Override
  protected IServerItemEntry createItemEntry(int dbId, int hash, int itemID, int meta, NBTTagCompound nbt) {
    return new ItemEntry(dbId, hash, itemID, meta, nbt);
  }

  AbstractInventory getInventory(int aiIndex) {
    return inventories[aiIndex];
  }

  @Override
  public void onNeighborChange(@Nonnull BlockPos neighborPos) {
    if (inventories == null) {
      return;
    }
    for (AbstractInventory abstractInventory : inventories) {
      abstractInventory.markForScanning(neighborPos);
    }
  }

}
