package crazypants.enderio.conduit.gas;

import mekanism.api.gas.IGasHandler;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IExtractor;

public interface IGasConduit extends IGasHandler, IExtractor {

  boolean canOutputToDir(ForgeDirection dir);

  boolean isExtractingFromDir(ForgeDirection dir);

}
