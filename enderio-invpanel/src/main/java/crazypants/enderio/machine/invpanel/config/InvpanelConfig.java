package crazypants.enderio.machine.invpanel.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;
import crazypants.enderio.machines.config.Config;

public final class InvpanelConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "invpanel"));

  public static final IValue<Boolean> inventoryPanelFree = F.make("inventoryPanelFree", false, 
      "If true, the inv panel will not accept fluids and will be active permanently.");
  
  public static final IValue<Float> inventoryPanelPowerPerMB = F.make("inventoryPanelPowerPerMB", 800F, 
      "Internal power generated per mB. The default of 800/mB matches the RF generation of the Zombie generator. "
      + "A panel tries to refill only once every second - setting this value too low slows down the scanning speed.");
  
  public static final IValue<Float> inventoryPanelScanCostPerSlot = F.make("inventoryPanelScanCostPerSlot", 0.1F, 
      "Internal power used for scanning a slot");
  
  public static final IValue<Float> inventoryPanelExtractCostPerItem = F.make("inventoryPanelExtractCostPerItem", 12F,
      "Internal power used per item extracted (not a stack of items)");
  
  public static final IValue<Float> inventoryPanelExtractCostPerOperation = F.make("inventoryPanelExtractCostPerOperation", 32F, 
      "Internal power used per extract operation (independent of stack size)");
  
  public static final IValue<Boolean> inventoryPanelScaleText = F.make("inventoryPanelScaleText", true, 
      "If true stack sizes will be drawn at a smaller size with a little more detail.");
}
