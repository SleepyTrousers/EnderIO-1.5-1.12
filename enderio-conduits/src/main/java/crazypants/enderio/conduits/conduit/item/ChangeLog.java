package crazypants.enderio.conduits.conduit.item;

public interface ChangeLog {

  void entryChanged(IItemEntry entry);

  void databaseReset();

  void sendChangeLog();

}
