package crazypants.enderio.base.conduit.registry;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import net.minecraft.util.ResourceLocation;

public class ConduitBuilder {

  private enum State {
    EMPTY(true, false, false, false, false),
    NETWORK(true, true, false, false, false),
    PRE_CONDUIT(false, false, true, false, false),
    CONDUIT(false, false, true, true, false),
    POST_CONDUIT(false, false, true, false, true),

    ;

    private final boolean acceptNetworkData, acceptNetworkBuild, acceptConduitData, acceptConduitBuild, acceptFinalize;

    private State(boolean acceptNetworkData, boolean acceptNetworkBuild, boolean acceptConduitData, boolean acceptConduitBuild, boolean acceptFinalize) {
      this.acceptNetworkData = acceptNetworkData;
      this.acceptNetworkBuild = acceptNetworkBuild;
      this.acceptConduitData = acceptConduitData;
      this.acceptConduitBuild = acceptConduitBuild;
      this.acceptFinalize = acceptFinalize;
    }
  }

  private @Nonnull State state = State.EMPTY;

  private ConduitBuilder() {
  }

  public static ConduitBuilder start() {
    return new ConduitBuilder();
  }

  ConduitTypeDefinition network;

  // network data

  private UUID networkUUID;
  private final @Nonnull NNList<UUID> networkAliases = new NNList<>();
  private Class<? extends IConduit> baseType;
  private Offset none = Offset.NONE, x = Offset.NONE, y = Offset.NONE, z = Offset.NONE;
  private boolean canConnectToAnything;

  // conduit data

  private UUID conduitUUID;
  private final @Nonnull NNList<UUID> conduitAliases = new NNList<>();
  private Class<? extends IConduit> serverClass, clientClass;

  // END data

  // UUID

  public ConduitBuilder setUUID(@Nonnull UUID uuid) {
    checkState(state.acceptNetworkData || state.acceptConduitData);
    if (state.acceptNetworkData) {
      networkUUID = uuid;
      state = State.NETWORK;
    } else {
      conduitUUID = uuid;
      state = State.CONDUIT;
    }
    return this;
  }

  public ConduitBuilder setUUID(@Nonnull String uuid) {
    return setUUID(UUID.nameUUIDFromBytes(uuid.getBytes()));
  }

  public ConduitBuilder setUUID(@Nonnull Class<? extends IConduit> uuid) {
    return setUUID(uuid.getName());
  }

  public ConduitBuilder setUUID(@Nonnull ResourceLocation uuid) {
    return setUUID(uuid.toString());
  }

  // ALIAS

  public ConduitBuilder addAlias(@Nonnull UUID uuid) {
    checkState(state.acceptNetworkData || state.acceptConduitData);
    if (state.acceptNetworkData) {
      networkAliases.add(uuid);
      state = State.NETWORK;
    } else {
      conduitAliases.add(uuid);
      state = State.CONDUIT;
    }
    return this;
  }

  public ConduitBuilder addAlias(@Nonnull String uuid) {
    return addAlias(UUID.nameUUIDFromBytes(uuid.getBytes()));
  }

  public ConduitBuilder addAlias(@Nonnull Class<? extends IConduit> uuid) {
    return addAlias(uuid.getName());
  }

  public ConduitBuilder addAlias(@Nonnull ResourceLocation uuid) {
    return addAlias(uuid.toString());
  }

  // CLASSES

  public ConduitBuilder setClass(@Nonnull Class<? extends IConduit> clazz) {
    checkState(state.acceptNetworkData || state.acceptConduitData);
    if (state.acceptNetworkData) {
      baseType = clazz;
      state = State.NETWORK;
    } else {
      serverClass = clazz;
      if (clientClass == null) {
        clientClass = clazz;
      }
      state = State.CONDUIT;
    }
    return this;
  }

  public ConduitBuilder setClientClass(@Nonnull Class<? extends IConduit> clazz) {
    checkState(state.acceptConduitData);
    clientClass = clazz;
    state = State.CONDUIT;
    return this;
  }

  // OFFSETS

  public ConduitBuilder setOffsets(@Nonnull Offset none, @Nonnull Offset x, @Nonnull Offset y, @Nonnull Offset z) {
    checkState(state.acceptNetworkData);
    this.none = none;
    this.x = x;
    this.y = y;
    this.z = z;
    state = State.NETWORK;
    return this;
  }

  // ANYTHING

  public ConduitBuilder setCanConnectToAnything() {
    checkState(state.acceptNetworkData);
    this.canConnectToAnything = true;
    state = State.NETWORK;
    return this;
  }

  // BUILD NETWORK

  @SuppressWarnings("unused")
  public ConduitBuilder build() {
    checkState(state.acceptNetworkBuild || state.acceptConduitBuild);
    if (state.acceptNetworkBuild) {
      final UUID networkUUID2 = networkUUID;
      if (networkUUID2 != null) {
        final Class<? extends IConduit> baseType2 = baseType;
        if (baseType2 != null) {
          final Offset none2 = none;
          if (none2 != null) {
            final Offset x2 = x;
            if (x2 != null) {
              final Offset y2 = y;
              if (y2 != null) {
                final Offset z2 = z;
                if (z2 != null) {
                  network = new ConduitTypeDefinition(networkUUID2, baseType2, none2, x2, y2, z2, canConnectToAnything);
                  network.getAliases().addAll(networkAliases);
                  state = State.PRE_CONDUIT;
                  return this;
                }
              }
            }
          }
        }
      }
    } else {
      final ConduitTypeDefinition network2 = network;
      if (network2 != null) {
        final UUID conduitUUID2 = conduitUUID;
        if (conduitUUID2 != null) {
          final Class<? extends IConduit> serverClass2 = serverClass;
          if (serverClass2 != null) {
            final Class<? extends IConduit> clientClass2 = clientClass;
            if (clientClass2 != null) {
              new ConduitDefinition(network2, conduitUUID2, serverClass2, clientClass2).getAliases().addAll(conduitAliases);
              state = State.POST_CONDUIT;
              return this;
            }
          }
        }
      }
    }
    throw new RuntimeException("State error in Conduit Builder---data missing");
  }

  public @Nonnull ConduitTypeDefinition finish() {
    checkState(state.acceptFinalize);
    final ConduitTypeDefinition network2 = network;
    if (network2 != null) {
      return network2;
    } else {
      throw new RuntimeException("State error in Conduit Builder---data missing");
    }
  }

  // tools

  private void checkState(boolean ok) {
    if (!ok) {
      throw new RuntimeException("State error in Conduit Builder (" + state + ")");
    }
  }
}
