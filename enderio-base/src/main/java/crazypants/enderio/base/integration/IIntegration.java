package crazypants.enderio.base.integration;

import com.enderio.core.common.util.UserIdent;

public interface IIntegration {

  default boolean isInSameTeam(UserIdent identA, UserIdent identB) {
    return false;
  };
}
