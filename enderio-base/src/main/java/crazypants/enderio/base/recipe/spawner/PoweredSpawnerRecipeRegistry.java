package crazypants.enderio.base.recipe.spawner;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.util.ResourceLocation;

public class PoweredSpawnerRecipeRegistry {

  private static final PoweredSpawnerRecipeRegistry instance = new PoweredSpawnerRecipeRegistry();

  private double defaultCostMultiplier = 1f;
  private boolean allowUnconfiguredMobs = true;

  public static PoweredSpawnerRecipeRegistry getInstance() {
    return instance;
  }

  private final @Nonnull Map<ResourceLocation, Double> costs = new HashMap<ResourceLocation, Double>();

  private final @Nonnull NNList<ResourceLocation> blackList = new NNList<ResourceLocation>();

  public double getCostMultiplierFor(@Nonnull ResourceLocation entity) {
    Double val = costs.get(entity);
    if (val == null) {
      return defaultCostMultiplier;
    }
    return val.doubleValue();
  }

  public boolean isBlackListed(@Nonnull ResourceLocation entity) {
    return blackList.contains(entity) || (!allowUnconfiguredMobs && !costs.containsKey(entity));
  }

  private PoweredSpawnerRecipeRegistry() {
  }

  public void setDefaultCostMultiplier(double defaultCostMultiplier) {
    this.defaultCostMultiplier = defaultCostMultiplier;
  }

  public void setAllowUnconfiguredMobs(boolean allowUnconfiguredMobs) {
    this.allowUnconfiguredMobs = allowUnconfiguredMobs;
  }

  public void addToBlacklist(@Nonnull ResourceLocation value) {
    blackList.add(value);
  }

  public void addEntityCost(@Nonnull ResourceLocation entityName, double costMultiplier) {
    if (costMultiplier > 0) {
      costs.put(entityName, costMultiplier);
    }
  }

}
