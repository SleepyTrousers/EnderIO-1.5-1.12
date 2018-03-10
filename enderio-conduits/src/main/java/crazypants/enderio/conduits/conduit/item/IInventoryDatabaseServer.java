package crazypants.enderio.conduits.conduit.item;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.energy.IEnergyStorage;

public interface IInventoryDatabaseServer extends IInventoryDatabase<IServerItemEntry> {

  boolean isCurrent();

  void addChangeLog(ChangeLog cl);

  void removeChangeLog(ChangeLog cl);

  List<? extends IServerItemEntry> decompressMissingItems(byte[] compressed) throws IOException;

  byte[] compressItemInfo(List<? extends IServerItemEntry> items) throws IOException;

  byte[] compressItemList() throws IOException;

  byte[] compressChangedItems(Collection<? extends IServerItemEntry> items) throws IOException;

  void resetDatabase();

  int getNumInventories();

  float getPower();

  void addPower(float power);

  boolean isOperational();

  int extractItems(IServerItemEntry entry, int count, IInventoryPanel te);

  void tick();

  void sendChangeLogs();

  /**
   * Called when a conduit that has an awareness upgrade is notified by one of its neighbors about a TE change. This will try to find a matching inventory and
   * mark it to be scanned for changes.
   * 
   * @param neighborPos
   *          The BlockPos of the neighbor
   */
  void onNeighborChange(BlockPos neighborPos);

  void entryChanged(IServerItemEntry entry);

}