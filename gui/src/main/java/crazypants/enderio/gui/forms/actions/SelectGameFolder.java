package crazypants.enderio.gui.forms.actions;

import java.io.File;

import crazypants.enderio.gui.forms.MainWindow;
import crazypants.enderio.gui.gamedata.GameLocation;

public class SelectGameFolder implements Runnable {

  private String datafile;
  private MainWindow parent;

  public SelectGameFolder(MainWindow parent, String datafile) {
    this.parent = parent;
    this.datafile = datafile;
  }

  @Override
  public void run() {
    GameLocation.setFile(new File(datafile));
    if (parent != null) {
      if (GameLocation.isValid()) {
        parent.getLabelInstallationFolder().setText(GameLocation.getGAME().toString());
      } else {
        parent.getLabelInstallationFolder().setText(datafile + " is not a valid Minecraft installation");
      }
    }
  }

}
