package crazypants.enderio.base.conduit.registry;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.conduit.IConduit;

/**
 * Use the {@link ConduitBuilder}
 *
 */
public class ConduitDefinition {

  private final @Nonnull ConduitTypeDefinition network;
  private final @Nonnull UUID conduitUUID;
  private final @Nonnull NNList<UUID> aliases = new NNList<>();
  private final @Nonnull Class<? extends IConduit> serverClass, clientClass;

  public ConduitDefinition(@Nonnull ConduitTypeDefinition network, @Nonnull UUID conduitUUID, @Nonnull Class<? extends IConduit> serverClass,
      @Nonnull Class<? extends IConduit> clientClass) {
    this.network = network;
    this.conduitUUID = conduitUUID;
    this.serverClass = serverClass;
    this.clientClass = clientClass;
    aliases.add(UUID.nameUUIDFromBytes(serverClass.getName().getBytes())); // compatibility with early 1.12
    if (serverClass != clientClass) {
      aliases.add(UUID.nameUUIDFromBytes(clientClass.getName().getBytes())); // compatibility with early 1.12
    }
    network.addMember(this);
  }

  public ConduitDefinition(@Nonnull ConduitTypeDefinition network, @Nonnull UUID conduitUUID, @Nonnull Class<? extends IConduit> serverClass) {
    this(network, conduitUUID, serverClass, serverClass);
  }

  public @Nonnull ConduitTypeDefinition getNetwork() {
    return network;
  }

  public @Nonnull UUID getUUID() {
    return conduitUUID;
  }

  public @Nonnull NNList<UUID> getAliases() {
    return aliases;
  }

  public @Nonnull Class<? extends IConduit> getServerClass() {
    return serverClass;
  }

  public @Nonnull Class<? extends IConduit> getClientClass() {
    return clientClass;
  }

  @Override
  public int hashCode() {
    return conduitUUID.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConduitDefinition other = (ConduitDefinition) obj;
    if (!conduitUUID.equals(other.conduitUUID))
      return false;
    return true;
  }

}
