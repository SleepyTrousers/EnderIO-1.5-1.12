package crazypants.enderio.render;

import javax.annotation.Nonnull;

import crazypants.enderio.IModObject;

public interface IHaveRenderers {

  void registerRenderers(@Nonnull IModObject modObject);
  
}
