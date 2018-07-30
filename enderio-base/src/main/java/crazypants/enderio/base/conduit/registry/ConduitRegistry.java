package crazypants.enderio.base.conduit.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.geom.Offsets;
import net.minecraft.block.Block;

public class ConduitRegistry {

  private static final Map<UUID, ConduitTypeDefinition> UUID_TO_NETWORK = new HashMap<>();
  private static final Map<UUID, ConduitDefinition> UUID_TO_CONDUIT = new HashMap<>();

  private static final Map<Class<? extends IConduit>, UUID> CLASS_TO_UUID = new IdentityHashMap<Class<? extends IConduit>, UUID>();

  /**
   * Register a new conduit type.
   * <p>
   * Will throw a RuntimeException if no location in the bundle could be found for the conduit.
   */
  public static void register(ConduitTypeDefinition info) {

    UUID_TO_NETWORK.put(info.getUUID(), info);
    for (UUID uuid : info.getAliases()) {
      UUID_TO_NETWORK.put(uuid, info);
    }
    CLASS_TO_UUID.put(info.getBaseType(), info.getUUID());

    for (ConduitDefinition member : info.getMembers()) {
      UUID_TO_CONDUIT.put(member.getUUID(), member);
      for (UUID uuid : member.getAliases()) {
        UUID_TO_CONDUIT.put(uuid, member);
      }
      CLASS_TO_UUID.put(member.getServerClass(), member.getUUID());
      CLASS_TO_UUID.put(member.getClientClass(), member.getUUID());

      // pre-classload the instances
      getServerInstance(member.getUUID());
      if (!EnderIO.proxy.isDedicatedServer()) {
        getClientInstance(member.getUUID());
      }
    }

    Offset none = info.getPreferedOffsetForNone(), x = info.getPreferedOffsetForX(), y = info.getPreferedOffsetForY(), z = info.getPreferedOffsetForZ();
    while (!Offsets.registerOffsets(info.getBaseType(), none, x, y, z)) {
      z = z.next();
      if (z == null) {
        z = Offset.first();
      }
      if (z == info.getPreferedOffsetForZ()) {
        y = y.next();
        if (y == null) {
          y = Offset.first();
        }
        if (y == info.getPreferedOffsetForY()) {
          x = x.next();
          if (x == null) {
            x = Offset.first();
          }
          if (x == info.getPreferedOffsetForX()) {
            none = none.next();
            if (none == null) {
              none = Offset.first();
            }
          }
        }
      }
      if (z == info.getPreferedOffsetForZ() && y == info.getPreferedOffsetForY() && x == info.getPreferedOffsetForX()
          && none == info.getPreferedOffsetForNone()) {
        throw new RuntimeException("Failed to find free offsets for " + info.getBaseType());
      }
    }
  }

  /**
   * Returns the ConduitDefinition for the given conduit instance (member).
   */
  public static ConduitDefinition get(IConduit conduit) {
    return UUID_TO_CONDUIT.get(CLASS_TO_UUID.get(conduit.getClass()));
  }

  /**
   * Returns the ConduitDefinition for the given conduit UUID.
   */
  public static ConduitDefinition get(UUID uuid) {
    return UUID_TO_CONDUIT.get(uuid);
  }

  /**
   * Returns the ConduitTypeDefinition for the given conduit instance (member).
   */
  public static ConduitTypeDefinition getNetwork(IConduit conduit) {
    return getNetwork(CLASS_TO_UUID.get(conduit.getClass()));
  }

  /**
   * Returns the ConduitTypeDefinition for the given conduit or network/type UUID.
   */
  public static ConduitTypeDefinition getNetwork(UUID uuid) {
    final ConduitTypeDefinition network = UUID_TO_NETWORK.get(uuid);
    return network != null ? network : get(uuid).getNetwork();
  }

  /**
   * Returns all registered ConduitDefinitions.
   */
  public static Collection<ConduitDefinition> getAll() {
    return UUID_TO_CONDUIT.values();
  }

  /**
   * Returns a new conduit instance (member) for the given member UUID (<em>not</em> network/type UUID).
   */
  public static IServerConduit getServerInstance(UUID uuid) {
    try {
      return UUID_TO_CONDUIT.get(uuid).getServerClass().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Could not create an instance of the conduit of type " + uuid, e);
    }
  }

  /**
   * Returns a new conduit client proxy instance (member) for the given member UUID (<em>not</em> network/type UUID).
   */
  public static IClientConduit getClientInstance(UUID uuid) {
    try {
      return UUID_TO_CONDUIT.get(uuid).getClientClass().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Could not create an instance of the conduit of type " + uuid, e);
    }
  }

  private static boolean sortingSupported = true;

  public static void sort(List<IConduit> conduits) {
    if (sortingSupported) {
      try {
        Collections.sort(conduits, CONDUIT_COMPERATOR);
      } catch (UnsupportedOperationException e) {
        // On older versions of Java this is not supported. We don't care, the list is only sorted to optimize our model cache.
        sortingSupported = false;
      }
    }
  }

  private static final Comparator<IConduit> CONDUIT_COMPERATOR = new Comparator<IConduit>() {
    @Override
    public int compare(IConduit o1, IConduit o2) {
      return getNetwork(o1).getUUID().compareTo(getNetwork(o2).getUUID());
    }
  };

  private static IModObject conduitBlock = null;

  /**
   * Returns the conduit block if it exists.
   */
  public static @Nullable Block getConduitBlock() {
    return conduitBlock == null ? null : conduitBlock.getBlock();
  }

  /**
   * Returns the conduit block if it exists.
   */
  public static @Nullable IModObject getConduitModObject() {
    return conduitBlock;
  }

  public static @Nonnull IModObject getConduitModObjectNN() {
    return NullHelper.notnull(conduitBlock, "Cannot use conduits unless conduits submod is installed");
  }

  /**
   * Sets the conduit block. For internal use by the conduit module only!
   */
  public static void registerConduitBlock(@Nonnull IModObject block) {
    conduitBlock = block;
  }

}
