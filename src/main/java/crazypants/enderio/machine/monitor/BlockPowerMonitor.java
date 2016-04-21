package crazypants.enderio.machine.monitor;

import net.minecraft.block.Block;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.monitor.v2.BlockPMon;

public class BlockPowerMonitor {

  public static Block create() {
    BlockPMon result = new BlockPMon(ModObject.blockPowerMonitor);
    result.init();
    return result;
  }

}
