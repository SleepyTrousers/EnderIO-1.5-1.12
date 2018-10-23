package crazypants.enderio.machines.autosave;

import crazypants.enderio.base.autosave.BaseHandlers;
import crazypants.enderio.machines.machine.transceiver.HandleChannelList;

public class MachineHandlers extends BaseHandlers {

  public static void register() {
    REGISTRY.register(new HandleChannelList());
  }
}