package crazypants.enderio.conduit.me;

import java.util.EnumSet;

import crazypants.enderio.conduit.IConduit;
import net.minecraft.util.EnumFacing;

public interface IMEConduit extends IConduit {

  MEConduitGrid getGrid();
  
  EnumSet<EnumFacing> getConnections();
  
  boolean isDense();

  int getChannelsInUse();

}
