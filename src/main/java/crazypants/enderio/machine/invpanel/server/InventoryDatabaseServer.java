package crazypants.enderio.machine.invpanel.server;

import crazypants.enderio.conduit.item.NetworkedInventory;
import crazypants.enderio.machine.invpanel.InventoryDatabase;
import crazypants.enderio.network.CompressedDataInput;
import crazypants.enderio.network.CompressedDataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

public class InventoryDatabaseServer extends InventoryDatabase<ItemEntry> {

  private AbstractInventory[] inventories;
  private int currentInventory;
  private ChangeLog changeLog;

  public InventoryDatabaseServer() {
  }

  public void addChangeLog(ChangeLog cl) {
    if(changeLog == null) {
      changeLog = cl;
    } else if(changeLog instanceof ChangeLogList) {
      ((ChangeLogList)changeLog).add(cl);
    } else if(changeLog != cl) {
      changeLog = new ChangeLogList(changeLog, cl);
    }
  }

  public void removeChangeLog(ChangeLog cl) {
    if(changeLog == cl) {
      changeLog = null;
    } else if(changeLog instanceof ChangeLogList) {
      changeLog = ((ChangeLogList)changeLog).remove(cl);
    }
  }

  public List<ItemEntry> decompressMissingItems(byte[] compressed) throws IOException {
    CompressedDataInput cdi = new CompressedDataInput(compressed);
    try {
      int numIDs = cdi.readVariable();
      ArrayList<ItemEntry> items = new ArrayList<ItemEntry>(numIDs);
      for(int i = 0; i < numIDs; i++) {
        int dbIndex = cdi.readVariable();
        if(dbIndex < complexItems.size()) {
          ItemEntry entry = complexItems.get(dbIndex);
          items.add(entry);
        }
      }
      return items;
    } finally {
      cdi.close();
    }
  }

  public byte[] compressItemInfo(List<ItemEntry> items) throws IOException{
    CompressedDataOutput cdo = new CompressedDataOutput();
    try {
      int count = items.size();
      cdo.writeVariable(generation);
      cdo.writeVariable(count);
      for(ItemEntry entry : items) {
        assert entry.dbID >= COMPLEX_DBINDEX_START;
        int code = (entry.dbID - COMPLEX_DBINDEX_START) << 1;
        if(entry.nbt != null) {
          code |= 1;
        }
        cdo.writeVariable(code);
        cdo.writeVariable(entry.itemID);
        cdo.writeVariable(entry.meta);
        if(entry.nbt != null) {
          CompressedStreamTools.write(entry.nbt, cdo);
        }
        cdo.writeVariable(entry.countItems(this));
      }
      return cdo.getCompressed();
    } finally {
      cdo.close();
    }
  }

  public byte[] compressItemList() throws IOException {
    CompressedDataOutput cdo = new CompressedDataOutput();
    try {
      cdo.writeVariable(generation);
      cdo.writeByte(0);
      for(Map.Entry<Integer, ItemEntry> entry : simpleRegsitry.entrySet()) {
        int count = entry.getValue().countItems(this);
        if(count > 0) {
          cdo.writeVariable(count);
          cdo.writeShort(entry.getKey());
        }
      }
      cdo.writeByte(0);
      int prevID = COMPLEX_DBINDEX_START;
      for(ItemEntry entry : complexItems) {
        if(entry != null) {
          int count = entry.countItems(this);
          if(count > 0) {
            cdo.writeVariable(count);
            cdo.writeVariable(entry.dbID - prevID);
            prevID = entry.dbID;
          }
        }
      }
      cdo.writeByte(0);
      return cdo.getCompressed();
    } finally {
      cdo.close();
    }
  }

  public byte[] compressChangedItems(Collection<ItemEntry> items) throws IOException {
    CompressedDataOutput cdo = new CompressedDataOutput();
    try {
      cdo.writeVariable(generation);
      cdo.writeVariable(items.size());
      for(ItemEntry entry : items) {
        cdo.writeVariable(entry.dbID);
        cdo.writeVariable(entry.countItems(this));
      }
      return cdo.getCompressed();
    } finally {
      cdo.close();
    }
  }

  public void setNetworkSources(List<NetworkedInventory> sources) {
    simpleRegsitry.clear();
    complexRegistry.clear();
    complexItems.clear();
    generation++;
    currentInventory = 0;

    if(sources == null || sources.isEmpty()) {
      this.inventories = null;
    } else {
      this.inventories = new AbstractInventory[sources.size()];
      for(int i=0; i<sources.size(); i++) {
        NetworkedInventory ni = sources.get(i);
        if(ni.getInventory() instanceof IDeepStorageUnit) {
          inventories[i] = new DSUInventory((IDeepStorageUnit) ni.getInventory());
        } else {
          inventories[i] = new NormalInventory(ni);
        }
      }
    }

    if(changeLog != null) {
      changeLog.databaseReset();
    }
  }

  public void scanNextInventory() {
    if(inventories == null) {
      return;
    }

    AbstractInventory inv = inventories[currentInventory];
    inv.scanInventory(this, currentInventory);

    currentInventory = (currentInventory+1) % inventories.length;
  }

  void entryChanged(ItemEntry entry) {
    if(changeLog != null) {
      changeLog.entryChanged(entry);
    }
  }

  public void sendChangeLogs() {
    if(changeLog != null) {
      changeLog.sendChangeLog();
    }
  }

  @Override
  protected ItemEntry createItemEntry(int dbId, int hash, int itemID, int meta, NBTTagCompound nbt) {
    return new ItemEntry(dbId, hash, itemID, meta, nbt);
  }

  AbstractInventory getInventory(int aiIndex) {
    return inventories[aiIndex];
  }

}
