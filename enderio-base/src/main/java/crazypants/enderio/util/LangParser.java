package crazypants.enderio.util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class LangParser {

  private static final String[] EIO_OLD_PATHS = { "F:/github/manual/EnderIO/resources/assets/enderio/lang/",
      "C:/github/EnderIO_110/resources/assets/enderio/lang/", "C:/github/EnderIO_1710/resources/assets/enderio/lang/",
      "C:/github/EnderZoo/resources/assets/enderzoo/lang/", "C:/github/EnderZoo_1710/resources/assets/enderzoo/lang/" };

  private static String[] submods = { "enderio-base", "enderio-conduits", "enderio-integration-forestry",
      // "enderio-integration-ftbl",
      "enderio-integration-tic", "enderio-invpanel", "enderio-machines" };

  public static void main(String[] args) {
    // public static void curesewontallowmainsinthejar() {
    for (String submod : submods) {
      System.out.println("Looking at " + submod);
      try {
        Map<String, Map<String, String>> byKey = new HashMap<>(); // key, lang, val
        Map<String, Map<String, Set<String>>> byVal = new HashMap<>(); // lang, val, key
        Map<String, Map<String, String>> result = new HashMap<>(); // lang, key, val

        int langs = 0, vals = 0;

        for (String path : EIO_OLD_PATHS) {

          File f = new File(path);

          if (f.exists()) {

            File[] files = f.listFiles();

            for (File file : files) {
              if (file.getName().endsWith(".lang")) {
                // System.out.println(file);
                String lang = file.getName().replaceAll("\\..*$", "").toLowerCase(Locale.ENGLISH);

                if (!byVal.containsKey(lang)) {
                  byVal.put(lang, new HashMap<>());
                  result.put(lang, new HashMap<>());
                }
                List<String> lines = FileUtils.readLines(file, "UTF-8");
                langs++;
                for (String line : lines) {
                  line = line.trim();
                  if (!line.startsWith("/") && !line.startsWith("#") && !line.startsWith(";") && line.contains("=")) {
                    String[] split = line.split("=", 2);
                    String key = split[0].trim();
                    String val = split[1].trim();
                    if (!val.isEmpty()) {
                      // System.out.println("-->" + key + "<-->" + val + "<--");
                      if (!byKey.containsKey(key)) {
                        byKey.put(key, new HashMap<>());
                      }
                      byKey.get(key).put(lang, val);
                      if (!byVal.get(lang).containsKey(val)) {
                        byVal.get(lang).put(val, new HashSet<>());
                      }
                      byVal.get(lang).get(val).add(key);
                      vals++;
                    }
                  }
                }

              }
            }

          }
        }

        System.out.println("Read " + langs + " langs with " + vals + " values");

        for (File file : new File(submod + "/src/main/resources/assets/enderio/lang/").listFiles()) {
          if (file.getName().endsWith(".lang")) {
            String lang = file.getName().replaceAll("\\..*$", "").toLowerCase(Locale.ENGLISH);

            if (!result.containsKey(lang)) {
              result.put(lang, new HashMap<>());
            }
          }
        }

        System.out.println(byVal.get("en_us").get("Withering Dust"));

        File f2 = new File(submod + "/src/main/resources/assets/enderio/lang/en_us.lang");

        List<String> lines = FileUtils.readLines(f2, "UTF-8");
        for (String line : lines) {
          line = line.trim();
          if (!line.startsWith("/") && !line.startsWith("#") && !line.startsWith(";") && line.contains("=")) {
            String[] split = line.split("=", 2);
            String key = split[0].trim();
            String val = split[1].trim();
            String offset = "                                                                                                              ".substring(0,
                key.length() - 4);

            for (String lang : result.keySet()) {

              // System.out.println(byKey.get(byVal.get("en_us").get("Withering Dust").iterator().next()).get(lang));

              Set<String> guessValue = new HashSet<>();
              String comment = "#" + offset + "en: " + val;
              // (1) we have a direct key match from the old file
              if (byKey.containsKey(key) && byKey.get(key).containsKey(lang)) {
                guessValue.add(byKey.get(key).get(lang));
              }

              // (2) we have a value match
              if (byVal.get("en_us").containsKey(val)) {
                for (String possibleKey : byVal.get("en_us").get(val)) {
                  if (byKey.containsKey(possibleKey) && byKey.get(possibleKey).containsKey(lang)) {
                    guessValue.add(byKey.get(possibleKey).get(lang));
                  }
                }
              }

              // (3) No matches at all
              if (guessValue.isEmpty()) {
                result.get(lang).put(key, "" + "\n" + comment);
              } else {
                result.get(lang).put(key, String.join("\n#" + offset + "OR: ", guessValue) + "\n" + comment);
              }
            }
          }
        }

        for (File file : new File(submod + "/src/main/resources/assets/enderio/lang/").listFiles()) {
          if (file.getName().endsWith(".lang")) {
            String lang = file.getName().replaceAll("\\..*$", "").toLowerCase(Locale.ENGLISH);

            for (String line : FileUtils.readLines(file, "UTF-8")) {
              line = line.trim();
              if (!line.startsWith("/") && !line.startsWith("#") && !line.startsWith(";") && line.contains("=")) {
                String[] split = line.split("=", 2);
                String key = split[0].trim();
                if (result.get(lang).remove(key) == null) {
                  result.get(lang).put("# " + key, " <-- extra unused key");
                }
              }
            }

          }
        }

        result.remove("en_us");

        for (String lang : result.keySet()) {
          lines.clear();
          for (String key : result.get(lang).keySet()) {
            lines.add(key + "=" + result.get(lang).get(key));
          }
          File f3 = new File(submod + "/src/main/resources/assets/enderio/lang/" + lang + ".guess");
          Collections.sort(lines);
          lines.add(0, "# This file has been auto-generated by the 'LangParser' tool. Do NOT edit!");
          lines.add(1, "#");
          lines.add(2, "# Edit the .lang file instead. You may have to create it first.");
          lines.add(3, "#");
          System.out.println("Writing guess file for " + lang + " with " + lines.size() + " lines");
          FileUtils.writeLines(f3, "UTF-8", lines);
        }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

}
