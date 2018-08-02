package crazypants.enderio.base.render;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;

public interface IHaveRenderers {

  void registerRenderers(@Nonnull IModObject modObject);

}
