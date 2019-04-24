package crazypants.enderio.conduit.me;

import appeng.api.AEApi;

/**
 * Second util because this may not be API-safe
 *
 */
public class MeUtil2 {

  public static boolean isFluixEnabled() {
    return (AEApi.instance().definitions().materials().purifiedFluixCrystal().isEnabled()
        || AEApi.instance().definitions().materials().fluixCrystal().isEnabled()) && AEApi.instance().definitions().parts().quartzFiber().isEnabled();
  }

}
