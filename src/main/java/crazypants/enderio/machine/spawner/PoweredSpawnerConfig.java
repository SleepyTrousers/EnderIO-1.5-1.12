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
  private static final String CORE_FILE_NAME = "PoweredSpawnerConfig_Core.json";
  private static final String USER_FILE_NAME = "PoweredSpawnerConfig_User.json";

  public static PoweredSpawnerConfig getInstance() {
    return instance;
  }

  private final Map<String, Double> costs = new HashMap<String, Double>();

  private final List<String> blackList = new ArrayList<String>();
  
  public double getCostMultiplierFor(String entity) {
    Double val = costs.get(entity);
    if(val == null) {
      return 1;
    }
    return val.doubleValue();
  }

  public boolean isBlackListed(String entity) {
    if(entity == null) {
      return true;
    }
    return blackList.contains(entity);
  }

  private PoweredSpawnerConfig() {

    String configText;
    JsonElement root;
    JsonObject rootObj;
    JsonObject costsObj;
    JsonArray blkList;
    try {
      //Core
      configText = IoUtil.copyConfigFromJar(CORE_FILE_NAME, true);
      root = new JsonParser().parse(configText);
      rootObj = root.getAsJsonObject();
      costsObj = rootObj.getAsJsonObject("costMultiplier");      
      for (Entry<String, JsonElement> entry : costsObj.entrySet()) {
        costs.put(entry.getKey(), Double.valueOf(entry.getValue().getAsDouble()));
      }

    } catch (Exception e) {
      Log.error("Could not load Powered Spawner costs from " + IoUtil.getConfigFile(CORE_FILE_NAME).getAbsolutePath());
      e.printStackTrace();
    }
    
    try {
      //User
      configText = IoUtil.copyConfigFromJar(USER_FILE_NAME, false);
      root = new JsonParser().parse(configText);
      rootObj = root.getAsJsonObject();
      costsObj = rootObj.getAsJsonObject("costMultiplier");
      for (Entry<String, JsonElement> entry : costsObj.entrySet()) {
        double val = Double.valueOf(entry.getValue().getAsDouble());
        costs.put(entry.getKey(), val);
      }

      blkList = rootObj.getAsJsonArray("blackList");
      if(blkList != null) {
        Log.info("Using user supplied values for Powered Spawner blacklist.");
        for (int i = 0; i < blkList.size(); i++) {
          String s = blkList.get(i).getAsString();
          Log.info(s);
          blackList.add(s);
        }
      }

    } catch (Exception e) {
      Log.error("Could not load user defined Powered Spawner costs from " + IoUtil.getConfigFile(USER_FILE_NAME).getAbsolutePath());
      e.printStackTrace();
    }
    
  }

}
