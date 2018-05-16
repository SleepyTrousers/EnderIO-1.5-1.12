package crazypants.enderio.conduits.me.conduit;

import java.util.EnumSet;

import appeng.api.networking.IGridNode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import net.minecraft.util.EnumFacing;

public interface IMEConduit extends IServerConduit, IClientConduit {

  MEConduitGrid getGrid();

  EnumSet<EnumFacing> getConnections();

  boolean isDense();

  int getChannelsInUse();

  IGridNode getGridNode();

}
