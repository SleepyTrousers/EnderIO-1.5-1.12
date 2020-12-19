package crazypants.enderio.gui.gamedata;

import java.io.File;
import java.io.IOException;

import javax.annotation.Nonnull;

public class GameLocation {

  private static File GAME;
  private static File DATA;
  private static File RECIPES;

  private static final @Nonnull String[] FOLDERS = { ".", "..", "enderio", "config/enderio", "../enderio", "../config/enderio",
      /* Dev Env paths: */ "run/config/enderio", "../run/config/enderio", "../../run/config/enderio" };

  /**
   * Try to find a valid Minecraft installation with Ender IO that has generated a data file.
   * 
   * @param data
   *          Any folder that's inside an installation (but not too deep in...)
   * @return <code>true</code> if the folder was valid
   */
  public static boolean setFile(@Nonnull File data) {
    for (String folder : FOLDERS) {
      try {
        DATA = new File(new File(data, folder), "mcobjdta.bin").getCanonicalFile();
        if (DATA.exists()) {
          RECIPES = new File(new File(data, folder), "recipes").getCanonicalFile();
          GAME = new File(new File(data, folder), "../..").getCanonicalFile();
          return true;
        }
      } catch (IOException e) {
      }
    }
    DATA = RECIPES = GAME = null;
    return false;
  }

  public static File getGAME() {
    return GAME;
  }

  public static File getDATA() {
    return DATA;
  }

  public static File getRECIPES() {
    return RECIPES;
  }

  public static boolean isValid() {
    return GAME != null;
  }
}
