package crazypants.enderio.machines.integration.ftblib;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.UserIdent;
import com.feed_the_beast.ftblib.lib.data.FTBLibAPI;

import net.minecraftforge.fml.common.Loader;

public class FtblIntegration {
  
  public static boolean isInSameTeam(@Nonnull UserIdent identA, @Nonnull UserIdent identB) {
    if (Loader.isModLoaded("ftblib")) {
      return isInSameTeamUnsafe(identA, identB);
    }
    return false;
  }

  private static boolean isInSameTeamUnsafe(@Nonnull UserIdent identA, @Nonnull UserIdent identB) {
    return FTBLibAPI.arePlayersInSameTeam(identA.getUUID(), identB.getUUID()); 
  }
}
