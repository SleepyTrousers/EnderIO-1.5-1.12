package crazypants.enderio.conduit.me;

import java.util.EnumSet;

import net.minecraftforge.common.util.ForgeDirection;

import crazypants.enderio.conduit.IConduit;

public interface IMEConduit extends IConduit {

    MEConduitGrid getGrid();

    EnumSet<ForgeDirection> getConnections();

    boolean isDense();

    int getChannelsInUse();
}
