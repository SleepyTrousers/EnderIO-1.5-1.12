package crazypants.enderio.conduit.me;

import crazypants.enderio.conduit.IConduit;
import java.util.EnumSet;
import net.minecraftforge.common.util.ForgeDirection;

public interface IMEConduit extends IConduit {

    MEConduitGrid getGrid();

    EnumSet<ForgeDirection> getConnections();

    boolean isDense();

    int getChannelsInUse();
}
