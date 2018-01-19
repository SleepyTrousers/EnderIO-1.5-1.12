package crazypants.enderio.integration.ftbl;

import com.enderio.core.common.util.UserIdent;
import com.feed_the_beast.ftbl.api_impl.ForgePlayer;
import com.feed_the_beast.ftbl.api_impl.ForgeTeam;
import com.feed_the_beast.ftbl.api_impl.Universe;
import com.feed_the_beast.ftbl.client.teamsgui.MyTeamData;
import com.feed_the_beast.ftbl.client.teamsgui.MyTeamPlayerData;

import crazypants.enderio.base.integration.IIntegration;

public class FtblIntegration implements IIntegration {
  public boolean isInSameTeam(UserIdent identA, UserIdent identB) {

    Universe universe = Universe.INSTANCE;
    ForgePlayer playerA = universe.getPlayer(identA.getUUID());

    ForgeTeam team = (ForgeTeam) playerA.getTeam();
    MyTeamData teamData = new MyTeamData(universe, team, playerA);

    for (MyTeamPlayerData pd : teamData.players) {
      if (pd.playerId.equals(identB.getUUID())) {
        return true;
      }
    }
    return false;
  };
}
