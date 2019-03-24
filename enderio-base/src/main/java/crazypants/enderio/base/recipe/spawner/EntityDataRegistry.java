package crazypants.enderio.base.recipe.spawner;

import java.util.function.Predicate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import net.minecraft.util.ResourceLocation;

public final class EntityDataRegistry {

  private static final EntityDataRegistry instance = new EntityDataRegistry();

  private static class Entry {
    final @Nullable Object identity;
    final @Nonnull Predicate<ResourceLocation> selector;
    final double cost;
    final boolean blacklistedSpawning, blacklistedSoulvial, needsCloning;

    Entry(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> selector) {
      this(identity, selector, 0, false, false, false);
    }

    Entry(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> selector, double cost, boolean blacklistedSpawning, boolean blacklistedSoulvial,
        boolean needsCloning) {
      this.identity = identity;
      this.selector = selector;
      this.cost = cost;
      this.blacklistedSpawning = blacklistedSpawning;
      this.blacklistedSoulvial = blacklistedSoulvial;
      this.needsCloning = needsCloning;
    }

    @Nonnull
    Entry withCost(@SuppressWarnings("hiding") double cost) {
      return new Entry(identity, selector, cost, blacklistedSpawning, blacklistedSoulvial, needsCloning);
    }

    @Nonnull
    Entry withBlacklistedSpawning(@SuppressWarnings("hiding") boolean blacklistedSpawning) {
      return new Entry(identity, selector, cost, blacklistedSpawning, blacklistedSoulvial, needsCloning);
    }

    @Nonnull
    Entry withBlacklistedSoulvial(@SuppressWarnings("hiding") boolean blacklistedSoulvial) {
      return new Entry(identity, selector, cost, blacklistedSpawning, blacklistedSoulvial, needsCloning);
    }

    @Nonnull
    Entry withNeedsCloning(@SuppressWarnings("hiding") boolean needsCloning) {
      return new Entry(identity, selector, cost, blacklistedSpawning, blacklistedSoulvial, needsCloning);
    }
  }

  private double defaultCostMultiplier = 1f;
  private boolean defaultBlacklistedSpawning = false;
  private boolean defaultBlacklistedSoulvial = false;
  private boolean defaultNeedsCloning = false;

  public static EntityDataRegistry getInstance() {
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
  public boolean isBlackListedForSpawning(@Nonnull ResourceLocation entity) {
    return entries.stream().filter(elem -> elem.selector.test(entity)).map(elem -> elem.blacklistedSpawning).reduce(Boolean::logicalOr)
        .orElse(defaultBlacklistedSpawning);
  }

  @SuppressWarnings("null")
  public boolean isBlackListedForSoulVial(@Nonnull ResourceLocation entity) {
    return entries.stream().filter(elem -> elem.selector.test(entity)).map(elem -> elem.blacklistedSoulvial).reduce(Boolean::logicalOr)
        .orElse(defaultBlacklistedSoulvial);
  }

  @SuppressWarnings("null")
  public boolean needsCloning(@Nonnull ResourceLocation entity) {
    return entries.stream().filter(elem -> elem.selector.test(entity)).map(elem -> elem.needsCloning).reduce(Boolean::logicalOr).orElse(defaultNeedsCloning);
  }

  private EntityDataRegistry() {
    final @Nonnull ResourceLocation DRAGON = new ResourceLocation("minecraft", "ender_dragon");
    addEntityData(null, in -> DRAGON.equals(in), Double.MAX_VALUE, true, true, true);
  }

  public void setDefaults(double costMultiplier, boolean blacklistedSpawning, boolean blacklistedSoulvial, boolean needsCloning) {
    defaultCostMultiplier = costMultiplier;
    defaultBlacklistedSpawning = blacklistedSpawning;
    defaultBlacklistedSoulvial = blacklistedSoulvial;
    defaultNeedsCloning = needsCloning;
  }

  /**
   * Adds or replaces a blacklist entry. If an entry for the same ResourceLocation already exists it is replaced. Otherwise a new entry is created.
   * 
   * @param value
   *          The ResourceLocation of the entity this record is for.
   */
  @Deprecated
  public void addToBlacklistSpawning(@Nonnull ResourceLocation value) {
    addToBlacklistSpawning(value, in -> value.equals(in));
  }

  /**
   * Adds a new blacklist entry that cannot be replaced later.
   * 
   * @param entityFilter
   *          A predicate to determine which entities this entry is for.
   */
  @Deprecated
  public void addToBlacklistSpawning(@Nonnull Predicate<ResourceLocation> entityFilter) {
    addToBlacklistSpawning(null, entityFilter);
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
  public void addToBlacklistSpawning(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> entityFilter) {
    Entry entry = identity != null ? getByIdentity(identity) : null;
    if (entry != null) {
      entries.remove(entry);
    } else {
      entry = new Entry(identity, entityFilter);
    }
    entries.add(entry.withBlacklistedSpawning(true));
  }

  public void addToBlacklistSoulVial(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> entityFilter) {
    Entry entry = identity != null ? getByIdentity(identity) : null;
    if (entry != null) {
      entries.remove(entry);
    } else {
      entry = new Entry(identity, entityFilter);
    }
    entries.add(entry.withBlacklistedSoulvial(true));
  }

  public void setNeedsCloning(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> entityFilter) {
    Entry entry = identity != null ? getByIdentity(identity) : null;
    if (entry != null) {
      entries.remove(entry);
    } else {
      entry = new Entry(identity, entityFilter);
    }
    entries.add(entry.withNeedsCloning(true));
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
      } else {
        entry = new Entry(identity, entityFilter);
      }
      entries.add(entry.withCost(costMultiplier));
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
   * @param blacklistedSpawning
   *          True if the entry should be blacklisted, false otherwise.
   */
  public void addEntityData(@Nullable Object identity, @Nonnull Predicate<ResourceLocation> entityFilter, double costMultiplier, boolean blacklistedSpawning,
      boolean blacklistedSoulvial, boolean needsCloning) {
    Entry entry = identity != null ? getByIdentity(identity) : null;
    if (entry != null) {
      entries.remove(entry);
    }
    entries.add(new Entry(identity, entityFilter, costMultiplier, blacklistedSpawning, blacklistedSoulvial, needsCloning));
  }

}
