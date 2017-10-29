package crazypants.enderio.machine.obelisk.aversion;

import crazypants.enderio.GuiID;
import crazypants.enderio.machine.MachineObject;
import crazypants.enderio.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.machine.obelisk.ContainerAbstractObelisk;
import crazypants.enderio.machine.obelisk.GuiRangedObelisk;
import crazypants.enderio.machine.obelisk.spawn.SpawningObeliskController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockAversionObelisk extends AbstractBlockObelisk<TileAversionObelisk> {

  public static BlockAversionObelisk create() {
    BlockAversionObelisk res = new BlockAversionObelisk();
    res.init();

    // Just making sure its loaded
    SpawningObeliskController.instance.toString();

    return res;
  }

  protected BlockAversionObelisk() {
    super(MachineObject.blockSpawnGuard, TileAversionObelisk.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileAversionObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerAbstractObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileAversionObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiRangedObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_SPAWN_GUARD;
  }
}
