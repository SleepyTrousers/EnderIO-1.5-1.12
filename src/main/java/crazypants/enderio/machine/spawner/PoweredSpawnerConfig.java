package crazypants.enderio.machine.spawner;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.enderio.Log;
import crazypants.util.IoUtil;

public class PoweredSpawnerConfig {

  static final PoweredSpawnerConfig instance = new PoweredSpawnerConfig();
  private static final String FILE_NAME = "PoweredSpawnerConfig.json";

  public static PoweredSpawnerConfig getInstance() {
    return instance;
  }

  private final Map<String, Integer> costs = new HashMap<String, Integer>();
  private final int defaultCost;
  
  private final List<String> blackList = new ArrayList<String>(); 
  
  public int getCostToSpawn(String entity) {
    Integer val = costs.get(entity);
    if(val == null) {
      return defaultCost;
    }
    return val.intValue();
  }
  
  public boolean isBlackListed(String entity) {
    if(entity == null) {
      return true;
    }
    return blackList.contains(entity);
  }
  
  private PoweredSpawnerConfig() {

    try {
      String configText = IoUtil.copyConfigFromJar(FILE_NAME, false);

      JsonParser p = new JsonParser();
      JsonElement root = p.parse(configText);
      JsonObject rootObj = root.getAsJsonObject();
      
      JsonObject costsObj = rootObj.getAsJsonObject("costs");
      Set<Entry<String, JsonElement>> entries = costsObj.entrySet();
      for (Entry<String, JsonElement> entry : entries) {
        costs.put(entry.getKey(), Integer.valueOf(entry.getValue().getAsInt()));
      }

      JsonArray blkList = rootObj.getAsJsonArray("blackList");
      if(blkList != null) {
        for (int i = 0; i < blkList.size(); i++) {
          String s = blkList.get(i).getAsString();
          blackList.add(s);
        }
      } else {
        Log.warn("No black list for powered spawner found in " + IoUtil.getConfigFile(FILE_NAME).getAbsolutePath());
      }

    } catch (Exception e) {
      Log.error("Could not load Powered Spawner costs from " + IoUtil.getConfigFile(FILE_NAME).getAbsolutePath());
      e.printStackTrace();
    }

    Integer def = costs.get("default");
    if(def != null) {
      defaultCost = def.intValue();
    } else {
      defaultCost = 100;
    }
  }

}
