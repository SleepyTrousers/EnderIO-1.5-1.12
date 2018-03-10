package crazypants.enderio.conduits.conduit.item;

public interface ChangeLog {

  void entryChanged(IServerItemEntry entry);

  void databaseReset();

  void sendChangeLog();

}
