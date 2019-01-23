package crazypants.enderio.base.block.darksteel.obsidian;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.render.IDefaultRenderers;

public class BlockReinforcedObsidian extends BlockReinforcedObsidianBase implements IDefaultRenderers {

  public static BlockReinforcedObsidian create(@Nonnull IModObject modObject) {
    BlockReinforcedObsidian result = new BlockReinforcedObsidian(modObject);
    result.init();
    return result;
  }

  protected BlockReinforcedObsidian(@Nonnull IModObject modObject) {
    super(modObject);
  }

}
