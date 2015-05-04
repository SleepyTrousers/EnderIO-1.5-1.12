package crazypants.enderio.machine.framework;

import crazypants.enderio.machine.AbstractPowerConsumerEntity;
import crazypants.enderio.machine.SlotDefinition;

public abstract class AbstractTileFramework extends AbstractPowerConsumerEntity {

  public AbstractTileFramework(SlotDefinition slotDefinition) {
    super(slotDefinition);
  }

}