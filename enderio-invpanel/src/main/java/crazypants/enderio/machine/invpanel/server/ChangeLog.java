package crazypants.enderio.machine.invpanel.server;

public interface ChangeLog {

  void entryChanged(ItemEntry entry);

  void databaseReset();

  void sendChangeLog();

}
