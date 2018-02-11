package crazypants.enderio.integration.ftbl;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.UserIdent;
import com.feed_the_beast.ftbl.api_impl.ForgePlayer;
import com.feed_the_beast.ftbl.api_impl.ForgeTeam;
import com.feed_the_beast.ftbl.api_impl.Universe;
import com.feed_the_beast.ftbl.client.teamsgui.MyTeamData;
import com.feed_the_beast.ftbl.client.teamsgui.MyTeamPlayerData;

import crazypants.enderio.base.integration.IIntegration;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class FtblIntegration extends IForgeRegistryEntry.Impl<IIntegration> implements IIntegration {

  @Override
  public boolean isInSameTeam(@Nonnull UserIdent identA, @Nonnull UserIdent identB) {
    Universe universe = Universe.INSTANCE;
    ForgePlayer playerA = universe.getPlayer(identA.getUUID());
    if (playerA != null) {
      ForgeTeam team = playerA.getTeam();
      if (team != null) {
        MyTeamData teamData = new MyTeamData(universe, team, playerA);

        for (MyTeamPlayerData pd : teamData.players) {
          if (pd.playerId.equals(identB.getUUID())) {
            return true;
          }
        }
      }
    }
    return false;
  };

}
