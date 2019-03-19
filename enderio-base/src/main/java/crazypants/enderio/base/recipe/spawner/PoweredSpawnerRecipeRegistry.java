package crazypants.enderio.base.recipe.spawner;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import net.minecraft.util.ResourceLocation;

public class PoweredSpawnerRecipeRegistry {

  private static final PoweredSpawnerRecipeRegistry instance = new PoweredSpawnerRecipeRegistry();

  private static class Entry {
    final @Nullable Object identity;
    final @Nonnull Predicate<ResourceLocation> selector;
    final double cost;
    final boolean blacklisted;

    Entry(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> selector, double cost, boolean blacklisted) {
      this.identity = identity;
      this.selector = selector;
      this.cost = cost;
      this.blacklisted = blacklisted;
    }
  }

  private double defaultCostMultiplier = 1f;
  private boolean allowUnconfiguredMobs = true;

  public static PoweredSpawnerRecipeRegistry getInstance() {
    return instance;
  }

  private final @Nonnull NNList<Entry> entries = new NNList<>();

  private @Nullable Entry getByIdentity(@Nonnull Object identity) {
    return entries.stream().filter(elem -> identity.equals(elem.identity)).findFirst().orElse(null);
  }

  @SuppressWarnings("null")
  public double getCostMultiplierFor(@Nonnull ResourceLocation entity) {
    return entries.stream().filter(elem -> elem.selector.test(entity) && elem.cost > 0).map(elem -> elem.cost).reduce(Double::max)
        .orElse(defaultCostMultiplier);
  }

  @SuppressWarnings("null")
  public boolean isBlackListed(@Nonnull ResourceLocation entity) {
    return entries.stream().filter(elem -> elem.selector.test(entity)).map(elem -> elem.blacklisted).reduce(Boolean::logicalOr).orElse(allowUnconfiguredMobs);
  }

  private PoweredSpawnerRecipeRegistry() {
  }

  public void setDefaultCostMultiplier(double defaultCostMultiplier) {
    this.defaultCostMultiplier = defaultCostMultiplier;
  }

  public void setAllowUnconfiguredMobs(boolean allowUnconfiguredMobs) {
    this.allowUnconfiguredMobs = allowUnconfiguredMobs;
  }

  /**
   * Adds or replaces a blacklist entry. If an entry for the same ResourceLocation already exists it is replaced. Otherwise a new entry is created.
   * 
   * @param value
   *          The ResourceLocation of the entity this record is for.
   */
  @Deprecated
  public void addToBlacklist(@Nonnull ResourceLocation value) {
    addToBlacklist(value, in -> value.equals(in));
  }

  /**
   * Adds a new blacklist entry that cannot be replaced later.
   * 
   * @param entityFilter
   *          A predicate to determine which entities this entry is for.
   */
  @Deprecated
  public void addToBlackList(@Nonnull Predicate<ResourceLocation> entityFilter) {
    addToBlacklist(null, entityFilter);
  }

  /**
   * Adds or replaces a blacklist entry. If an identity object is given, this tries to replace an existing entry with the same identity object. Otherwise a new
   * entry that cannot be replaced is created always.
   * 
   * @param identity
   *          An optional identity object that is needed to make entries replaceable.
   * @param entityFilter
   *          A predicate to determine which entities this entry is for.
   */
  public void addToBlacklist(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> entityFilter) {
    Entry entry = identity != null ? getByIdentity(identity) : null;
    if (entry != null) {
      entries.remove(entry);
      entries.add(new Entry(identity, entityFilter, entry.cost, true));
    } else {
      entries.add(new Entry(identity, entityFilter, 0, true));
    }
  }

  /**
   * Adds or replaces a cost entry. If an entry for the same ResourceLocation already exists it is replaced. Otherwise a new entry is created.
   * 
   * @param value
   *          The ResourceLocation of the entity this record is for.
   * @param costMultiplier
   *          The cost multiplier to be recorded. Must not be 0 or negative.
   */
  @Deprecated
  public void addEntityCost(@Nonnull ResourceLocation value, double costMultiplier) {
    addEntityCost(value, in -> value.equals(in), costMultiplier);
  }

  /**
   * Adds a new cost entry that cannot be replaced later.
   * 
   * @param entityFilter
   *          A predicate to determine which entities this entry is for.
   * @param costMultiplier
   *          The cost multiplier to be recorded. Must not be 0 or negative.
   */
  @Deprecated
  public void addEntityCost(@Nonnull Predicate<ResourceLocation> entityFilter, double costMultiplier) {
    addEntityCost(null, entityFilter, costMultiplier);
  }

  /**
   * Adds or replaces a cost entry. If an identity object is given, this tries to replace an existing entry with the same identity object. Otherwise a new entry
   * that cannot be replaced is created always.
   * 
   * @param identity
   *          An optional identity object that is needed to make entries replaceable.
   * @param entityFilter
   *          A predicate to determine which entities this entry is for.
   * @param costMultiplier
   *          The cost multiplier to be recorded. Must not be 0 or negative.
   */
  public void addEntityCost(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> entityFilter, double costMultiplier) {
    if (costMultiplier > 0) {
      Entry entry = identity != null ? getByIdentity(identity) : null;
      if (entry != null) {
        entries.remove(entry);
        entries.add(new Entry(identity, entityFilter, costMultiplier, entry.blacklisted));
      } else {
        entries.add(new Entry(identity, entityFilter, costMultiplier, false));
      }
    }
  }

  /**
   * Adds or replaces an entry. If an identity object is given, this tries to replace an existing entry with the same identity object. Otherwise a new entry
   * that cannot be replaced is created always.
   * 
   * @param identity
   *          An optional identity object that is needed to make entries replaceable.
   * @param entityFilter
   *          A predicate to determine which entities this entry is for.
   * @param costMultiplier
   *          The cost multiplier to be recorded. Must not be 0 or negative.
   * @param isBlacklisted
   *          True if the entry should be blacklisted, false otherwise.
   */
  public void addEntityData(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> entityFilter, double costMultiplier, boolean isBlacklisted) {
    Entry entry = identity != null ? getByIdentity(identity) : null;
    if (entry != null) {
      entries.remove(entry);
    }
    entries.add(new Entry(identity, entityFilter, costMultiplier, isBlacklisted));
  }

}
