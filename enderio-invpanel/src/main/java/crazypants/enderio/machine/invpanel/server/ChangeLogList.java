package crazypants.enderio.machine.invpanel.server;

import java.util.ArrayList;

import crazypants.enderio.base.invpanel.database.IChangeLog;
import crazypants.enderio.base.invpanel.database.IServerItemEntry;

final class ChangeLogList implements IChangeLog {
  final ArrayList<IChangeLog> clList;

  public ChangeLogList(IChangeLog cl0, IChangeLog cl1) {
    clList = new ArrayList<IChangeLog>(2);
    clList.add(cl0);
    clList.add(cl1);
  }

  @Override
  public void entryChanged(IServerItemEntry entry) {
    for (IChangeLog cl : clList) {
      cl.entryChanged(entry);
    }
  }

  @Override
  public void databaseReset() {
    for (IChangeLog cl : clList) {
      cl.databaseReset();
    }
  }

  @Override
  public void sendChangeLog() {
    for (IChangeLog cl : clList) {
      cl.sendChangeLog();
    }
  }

  IChangeLog remove(IChangeLog cl) {
    clList.remove(cl);
    if (clList.size() == 1) {
      return clList.get(0);
    }
    return this;
  }

  void add(IChangeLog cl) {
    if (!clList.contains(cl)) {
      clList.add(cl);
    }
  }

}
