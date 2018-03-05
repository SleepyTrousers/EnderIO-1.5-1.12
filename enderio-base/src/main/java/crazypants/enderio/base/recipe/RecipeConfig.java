package crazypants.enderio.base.recipe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Nonnull;

import org.apache.commons.io.IOUtils;

import crazypants.enderio.base.Log;

public class RecipeConfig {

  // ---------------------------------------------- Loading ------------

  public static @Nonnull String readRecipes(@Nonnull File copyTo, @Nonnull String fileName, boolean replaceIfExists) throws IOException {
    if (!replaceIfExists && copyTo.exists()) {
      final FileInputStream in = new FileInputStream(copyTo);
      try {
        return readStream(in);
      } finally {
        IOUtils.closeQuietly(in);
      }
    }

    InputStream in = RecipeConfig.class.getResourceAsStream("/assets/enderio/config/" + fileName);
    try {
      if (in == null) {
        Log.error("Could load default AlloySmelter recipes.");
        throw new IOException("Could not resource /assets/enderio/config/" + fileName + " form classpath. ");
      }
      String output = readStream(in);
      BufferedWriter writer = null;
      try {
        writer = new BufferedWriter(new FileWriter(copyTo, false));
        writer.write(output.toString());
      } finally {
        IOUtils.closeQuietly(writer);
      }
      return output;
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  private static @Nonnull String readStream(@Nonnull InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder output = new StringBuilder();
    try {
      String line = reader.readLine();
      while (line != null) {
        output.append(line);
        output.append("\n");
        line = reader.readLine();
      }
    } finally {
      IOUtils.closeQuietly(reader);
    }
    return output.toString();
  }

  // ---------------------------------------------- Class ------------

  public RecipeConfig() {
  }

}
