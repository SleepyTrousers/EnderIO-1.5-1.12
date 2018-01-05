package crazypants.enderio.base.config;

import javax.annotation.Nullable;

import net.minecraftforge.common.config.Configuration;

public interface IEnderIOAddon {

  @Nullable
  Configuration getConfiguration();

}
