package crazypants.enderio.machines.machine.spawner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import crazypants.enderio.base.Log;
import crazypants.enderio.util.IoUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class PoweredSpawnerConfig {

  static final PoweredSpawnerConfig instance = new PoweredSpawnerConfig();
  private static final String CORE_FILE_NAME = "PoweredSpawnerConfig_Core.json";
  private static final String USER_FILE_NAME = "PoweredSpawnerConfig_User.json";

  private static final String KEY_ENTITY_NAME = "entityName";
  private static final String KEY_COST_MULTIPLIER = "costMultiplier";

  public static PoweredSpawnerConfig getInstance() {
    return instance;
  }

  private final Map<ResourceLocation, Double> costs = new HashMap<>();

  private final List<ResourceLocation> blackList = new ArrayList<>();
  
  public double getCostMultiplierFor(ResourceLocation entity) {
    Double val = costs.get(entity);
    if(val == null) {
      return 1;
    }
    return val.doubleValue();
  }

  public boolean isBlackListed(ResourceLocation entity) {
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
        costs.put(new ResourceLocation(entry.getKey()), Double.valueOf(entry.getValue().getAsDouble()));
      }

      blkList = rootObj.getAsJsonArray("blackList");
      if(blkList != null) {
        for (int i = 0; i < blkList.size(); i++) {
          String s = blkList.get(i).getAsString();
          blackList.add(new ResourceLocation(s));
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
        costs.put(new ResourceLocation(entry.getKey()), val);
      }

      blkList = rootObj.getAsJsonArray("blackList");
      if(blkList != null) {
        blackList.clear();
        Log.info("Replacing default Powered Spawner blacklist with user supplied values.");
        for (int i = 0; i < blkList.size(); i++) {
          String s = blkList.get(i).getAsString();
          blackList.add(new ResourceLocation(s));
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
      costs.put(new ResourceLocation(entityName), costMultiplier);
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
