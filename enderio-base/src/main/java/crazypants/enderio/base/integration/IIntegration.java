package crazypants.enderio.base.integration;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.UserIdent;

import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public interface IIntegration extends IForgeRegistryEntry<IIntegration> {

  default boolean isInSameTeam(@Nonnull UserIdent identA, @Nonnull UserIdent identB) {
    return false;
  };

}
