package crazypants.enderio.base.invpanel.capability;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.invpanel.database.IInventoryDatabaseServer;

public interface IDatabaseHandler {

  @Nonnull
  IInventoryDatabaseServer getDatabase();

  int getChangeCount();

  @Nonnull
  List<InventoryDatabaseSource> getSources();

}
