package crazypants.enderio.base.invpanel.database;

public interface IChangeLog {

  void entryChanged(IServerItemEntry entry);

  void databaseReset();

  void sendChangeLog();

}
