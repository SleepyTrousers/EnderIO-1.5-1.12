package crazypants.enderio.gui.forms.actions;

import java.util.Map;
import java.util.stream.Collectors;

import crazypants.enderio.gui.forms.MainWindow;
import crazypants.enderio.gui.gamedata.GameLocation;
import crazypants.enderio.gui.gamedata.ValueRepository;

public class LoadDataFile implements Runnable {

  private MainWindow parent;

  public LoadDataFile(MainWindow parent) {
    this.parent = parent;
  }

  @Override
  public void run() {
    String error = ValueRepository.read();
    if (parent != null) {
      if (error != null) {
        parent.getLabelDataFile().setText("Error Loading Data File");
        parent.getLabelDataFile().setToolTipText(error);
      } else {
        Map<String, Integer> counts = ValueRepository.getCounts();
        if (counts.isEmpty()) {
          parent.getLabelDataFile().setText("No Data File Loaded");
          parent.getLabelDataFile().setToolTipText(null);
        } else {
          parent.getLabelDataFile().setText("Loaded " + GameLocation.getDATA().toString());
          parent.getLabelDataFile().setToolTipText(counts.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining(", ")));
        }
      }
    }
  }

}
