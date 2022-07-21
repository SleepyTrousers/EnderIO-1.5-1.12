package crazypants.enderio.machine.invpanel.server;

import java.util.ArrayList;

final class ChangeLogList implements ChangeLog {
    final ArrayList<ChangeLog> clList;

    public ChangeLogList(ChangeLog cl0, ChangeLog cl1) {
        clList = new ArrayList<ChangeLog>(2);
        clList.add(cl0);
        clList.add(cl1);
    }

    @Override
    public void entryChanged(ItemEntry entry) {
        for (ChangeLog cl : clList) {
            cl.entryChanged(entry);
        }
    }

    @Override
    public void databaseReset() {
        for (ChangeLog cl : clList) {
            cl.databaseReset();
        }
    }

    @Override
    public void sendChangeLog() {
        for (ChangeLog cl : clList) {
            cl.sendChangeLog();
        }
    }

    ChangeLog remove(ChangeLog cl) {
        clList.remove(cl);
        if (clList.size() == 1) {
            return clList.get(0);
        }
        return this;
    }

    void add(ChangeLog cl) {
        if (!clList.contains(cl)) {
            clList.add(cl);
        }
    }
}
