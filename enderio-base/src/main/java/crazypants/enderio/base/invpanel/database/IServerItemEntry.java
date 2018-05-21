package crazypants.enderio.base.invpanel.database;

import javax.annotation.Nonnull;

public interface IServerItemEntry extends IItemEntry {

  int countItems();

  int extractItems(@Nonnull IInventoryDatabaseServer db, int count);

  void addSlot(SlotKey slotKey);

  void removeSlot(SlotKey slotKey);

}