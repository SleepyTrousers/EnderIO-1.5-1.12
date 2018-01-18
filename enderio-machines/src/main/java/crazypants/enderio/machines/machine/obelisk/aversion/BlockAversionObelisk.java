package crazypants.enderio.machines.machine.obelisk.aversion;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.machines.machine.obelisk.base.AbstractBlockRangedObelisk;
import crazypants.enderio.machines.machine.obelisk.base.SpawningObeliskController;

public class BlockAversionObelisk extends AbstractBlockRangedObelisk<TileAversionObelisk> {

  public static BlockAversionObelisk create(@Nonnull IModObject modObject) {
    BlockAversionObelisk res = new BlockAversionObelisk(modObject);
    res.init();

    // Just making sure its loaded
    SpawningObeliskController.instance.toString();

    return res;
  }

  protected BlockAversionObelisk(@Nonnull IModObject modObject) {
    super(modObject, TileAversionObelisk.class);
  }

}
