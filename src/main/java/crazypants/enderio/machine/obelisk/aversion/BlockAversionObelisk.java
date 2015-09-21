package crazypants.enderio.machine.obelisk.aversion;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;

public class BlockAversionObelisk extends BlockObeliskAbstract<TileAversionObelisk> {

  public static BlockAversionObelisk create() {
    BlockAversionObelisk res = new BlockAversionObelisk();
    res.init();

    //Just making sure its loaded
    AversionObeliskController.instance.toString();

    return res;
  }

  protected BlockAversionObelisk() {
    super(ModObject.blockSpawnGuard, TileAversionObelisk.class);
    setGuiClasses(ContainerAversionObelisk.class, GuiAversionObelisk.class);
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_SPAWN_GUARD;
  }
}
