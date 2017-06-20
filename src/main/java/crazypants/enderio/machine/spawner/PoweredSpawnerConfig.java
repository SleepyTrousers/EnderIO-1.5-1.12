package crazypants.enderio.machine.spawner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import crazypants.enderio.Log;
import crazypants.util.IoUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PoweredSpawnerConfig {

  static final PoweredSpawnerConfig instance = new PoweredSpawnerConfig();
  private static final String CORE_FILE_NAME = "PoweredSpawnerConfig_Core.json";
  private static final String USER_FILE_NAME = "PoweredSpawnerConfig_User.json";

  private static final String KEY_ENTITY_NAME = "entityName";
  private static final String KEY_COST_MULTIPLIER = "costMultiplier";

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

      blkList = rootObj.getAsJsonArray("blackList");
      if(blkList != null) {
        for (int i = 0; i < blkList.size(); i++) {
          String s = blkList.get(i).getAsString();
          blackList.add(s);
        }
      } else {
        Log.warn("No black list for powered spawner found in " + IoUtil.getConfigFile(CORE_FILE_NAME).getAbsolutePath());
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
        blackList.clear();
        Log.info("Replacing default Powered Spawner blacklist with user supplied values.");
        for (int i = 0; i < blkList.size(); i++) {
          String s = blkList.get(i).getAsString();
          blackList.add(s);
        }
      }

    } catch (Exception e) {
      Log.error("Could not load user defined Powered Spawner costs from " + IoUtil.getConfigFile(USER_FILE_NAME).getAbsolutePath());
      e.printStackTrace();
    }
    
  }

  public void addToBlacklist(ResourceLocation value) {
    if (value != null) {
      blackList.add(value);
    }
  }

  public void addEntityCost(String entityName, double costMultiplier) {
    if(entityName != null && costMultiplier > 0) {
      costs.put(entityName, costMultiplier);
    }
  }

  public void addEntityCostFromNBT(NBTTagCompound tag) {
    if(tag == null) {
      return;
    }
    if(!tag.hasKey(KEY_ENTITY_NAME)) {
      return;
    }
    if(!tag.hasKey(KEY_COST_MULTIPLIER)) {
      return;
    }
    addEntityCost(tag.getString(KEY_ENTITY_NAME), tag.getDouble(KEY_COST_MULTIPLIER));
  }

}
