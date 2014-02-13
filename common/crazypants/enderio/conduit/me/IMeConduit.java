package crazypants.enderio.conduit.me;

import appeng.api.me.util.IGridInterface;
import crazypants.enderio.conduit.IConduit;

public interface IMeConduit extends IConduit {

  void setPoweredStatus(boolean hasPower);

  boolean isPowered();

  IGridInterface getGrid();

  void setGrid(IGridInterface gi);

  boolean isMachineActive();

  void setNetworkReady(boolean isReady);

  float getPowerDrainPerTick();

}
