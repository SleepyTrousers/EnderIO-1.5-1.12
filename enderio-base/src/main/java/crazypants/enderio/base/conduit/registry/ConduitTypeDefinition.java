package crazypants.enderio.base.conduit.registry;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.geom.Offset;

/**
 * Use the {@link ConduitBuilder}
 *
 */
public class ConduitTypeDefinition {

  private final @Nonnull UUID networkUUID;
  private final @Nonnull NNList<UUID> aliases = new NNList<>();
  private final @Nonnull Class<? extends IConduit> baseType;
  private final @Nonnull Offset none, x, y, z;
  private final boolean canConnectToAnything;
  private final @Nonnull NNList<ConduitDefinition> members = new NNList<>();

  public ConduitTypeDefinition(@Nonnull UUID networkUUID, @Nonnull Class<? extends IConduit> baseType, @Nonnull Offset none, @Nonnull Offset x,
      @Nonnull Offset y, @Nonnull Offset z, boolean canConnectToAnything) {
    this.networkUUID = networkUUID;
    this.baseType = baseType;
    this.none = none;
    this.x = x;
    this.y = y;
    this.z = z;
    this.canConnectToAnything = canConnectToAnything;
    aliases.add(UUID.nameUUIDFromBytes(baseType.getName().getBytes())); // compatibility with early 1.12
  }

  public ConduitTypeDefinition(@Nonnull UUID networkUUID, @Nonnull Class<? extends IConduit> baseType, @Nonnull Offset none, @Nonnull Offset x,
      @Nonnull Offset y, @Nonnull Offset z) {
    this(networkUUID, baseType, none, x, y, z, false);
  }

  public ConduitTypeDefinition(@Nonnull UUID networkUUID, @Nonnull Class<? extends IConduit> baseType, boolean canConnectToAnything) {
    this(networkUUID, baseType, Offset.NONE, Offset.NONE, Offset.NONE, Offset.NONE, canConnectToAnything);
  }

  public ConduitTypeDefinition(@Nonnull UUID networkUUID, @Nonnull Class<? extends IConduit> baseType) {
    this(networkUUID, baseType, Offset.NONE, Offset.NONE, Offset.NONE, Offset.NONE, false);
  }

  void addMember(@Nonnull ConduitDefinition member) {
    members.add(member);
  }

  public @Nonnull NNList<ConduitDefinition> getMembers() {
    return members;
  }

  public @Nonnull UUID getUUID() {
    return networkUUID;
  }

  public @Nonnull NNList<UUID> getAliases() {
    return aliases;
  }

  public @Nonnull Class<? extends IConduit> getBaseType() {
    return baseType;
  }

  public @Nonnull Offset getPreferedOffsetForNone() {
    return none;
  }

  public @Nonnull Offset getPreferedOffsetForX() {
    return x;
  }

  public @Nonnull Offset getPreferedOffsetForY() {
    return y;
  }

  public @Nonnull Offset getPreferedOffsetForZ() {
    return z;
  }

  public boolean canConnectToAnything() {
    return canConnectToAnything;
  }

  @Override
  public int hashCode() {
    return networkUUID.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConduitTypeDefinition other = (ConduitTypeDefinition) obj;
    if (!networkUUID.equals(other.networkUUID)) {
      return false;
    }
    return true;
  }

}
