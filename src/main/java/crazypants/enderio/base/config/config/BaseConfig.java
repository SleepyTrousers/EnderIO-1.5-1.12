package crazypants.enderio.base.config.config;

import java.util.function.Supplier;

import com.enderio.core.common.network.ThreadedNetworkWrapper;

import crazypants.enderio.base.config.ValueFactory;
import crazypants.enderio.base.network.PacketHandler;

public final class BaseConfig {

  public static final ValueFactory F = new ValueFactory(new Supplier<ThreadedNetworkWrapper>() {
    @Override
    public ThreadedNetworkWrapper get() {
      return PacketHandler.INSTANCE;
    }
  });

  public static void load() {
    ChargerConfig.F.getClass();
    EnchanterConfig.F.getClass();
    SpawnerConfig.F.getClass();
  }

}
