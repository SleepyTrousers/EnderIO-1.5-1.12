package crazypants.enderio.base.diagnostics;

import java.util.List;

public interface IDiagnosticsTracker {

  String getActivityDescription();

  List<String> getLines();

  void start(Object on);

  void stop();

  void discard();

}
