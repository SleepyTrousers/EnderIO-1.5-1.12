package crazypants.enderio.integration.ftbl;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.UserIdent;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.IForgeTeam;
import com.feed_the_beast.ftbl.api_impl.ForgePlayer;
import com.feed_the_beast.ftbl.api_impl.Universe;

import crazypants.enderio.base.integration.IIntegration;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class FtblIntegration extends IForgeRegistryEntry.Impl<IIntegration> implements IIntegration {

  @Override
  public boolean isInSameTeam(@Nonnull UserIdent identA, @Nonnull UserIdent identB) {
    Universe universe = Universe.INSTANCE;
    ForgePlayer playerA = universe.getPlayer(identA.getUUID());
    if (playerA != null) {
      IForgeTeam team = playerA.getTeam();
      if (team != null) {
        for (IForgePlayer player : team.getMembers()) {
          if (player.getId().equals(identB.getUUID())) {
            return true;
          }
        }
      }
    }
    return false;
  };

}
