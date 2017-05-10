package crazypants.util;

import crazypants.enderio.config.Config;

@Deprecated
public class Things extends com.enderio.core.common.util.stackable.Things { // FIXME remove later

  public static final Things TRAVEL_BLACKLIST = new Things(Config.travelStaffBlinkBlackList);

  public Things(String... names) {
    super(names);
  }

}
