package crazypants.enderio.conduits.conduit.item;

public interface IServerItemEntry extends IItemEntry {

  int countItems();
  
  int extractItems(IInventoryDatabaseServer db, int count);

  void addSlot(SlotKey slotKey);

  void removeSlot(SlotKey slotKey);

}