package crazypants.enderio.machine.framework;

import crazypants.enderio.machine.AbstractMachineBlock;
import net.minecraftforge.fluids.Fluid;

public interface IFrameworkMachine {

  enum TankSlot {
    FRONT_LEFT, FRONT_RIGHT, BACK_RIGHT, BACK_LEFT;
  }

  boolean hasTank(TankSlot tankSlot);

  Fluid getTankFluid(TankSlot tankSlot);

  boolean hasController();

  AbstractMachineBlock getSlotMachine(TankSlot tankSlot);

  String getControllerModelName();
}
