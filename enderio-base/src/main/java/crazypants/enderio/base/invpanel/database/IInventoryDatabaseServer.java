package crazypants.enderio.base.invpanel.database;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.util.math.BlockPos;

public interface IInventoryDatabaseServer extends IInventoryDatabase<IServerItemEntry> {

  boolean isCurrent();

  void addChangeLog(IChangeLog cl);

  void removeChangeLog(IChangeLog cl);

  List<? extends IServerItemEntry> decompressMissingItems(byte[] compressed) throws IOException;

  byte[] compressItemInfo(List<? extends IServerItemEntry> items) throws IOException;

  byte[] compressItemList() throws IOException;

  byte[] compressChangedItems(Collection<? extends IServerItemEntry> items) throws IOException;

  void resetDatabase();

  int getNumInventories();

  float getPower();

  void addPower(float power);

  boolean isOperational();

  int extractItems(IServerItemEntry entry, int count, @Nonnull IInventoryPanel te);

  void tick();

  void sendChangeLogs();

  /**
   * Called when a conduit that has an awareness upgrade is notified by one of its neighbors about a TE change. This will try to find a matching inventory and
   * mark it to be scanned for changes.
   * 
   * @param neighborPos
   *          The BlockPos of the neighbor
   */
  void onNeighborChange(@Nonnull BlockPos neighborPos);

  void entryChanged(IServerItemEntry entry);

}