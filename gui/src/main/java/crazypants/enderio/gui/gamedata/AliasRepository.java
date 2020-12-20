package crazypants.enderio.gui.gamedata;

import java.util.LinkedHashSet;
import java.util.Set;

public class AliasRepository {

  private static final Set<String> CORE = new LinkedHashSet<>();
  private static final Set<String> USER = new LinkedHashSet<>();

  private static boolean doingCore = true;

  public static void startDoingCore() {
    doingCore = true;
    CORE.clear();
  }

  public static void startDoingUser() {
    doingCore = false;
    USER.clear();
  }

  public static void addValue(String value) {
    if (doingCore) {
      CORE.add(value);
    } else {
      USER.add(value);
    }
  }

  public static Set<String> getCore() {
    return CORE;
  }

  public static Set<String> getUser() {
    return USER;
  }

}
