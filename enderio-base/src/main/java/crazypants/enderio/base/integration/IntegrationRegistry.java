package crazypants.enderio.base.integration;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

public class IntegrationRegistry {
  public static final @Nonnull NNList<IIntegration> REGISTRY = new NNList<IIntegration>();

  public static void register(IIntegration integration) {
    REGISTRY.add(integration);
  }
}
