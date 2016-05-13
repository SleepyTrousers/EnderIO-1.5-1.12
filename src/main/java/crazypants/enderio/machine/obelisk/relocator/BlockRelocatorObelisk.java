package crazypants.enderio.machine.obelisk.relocator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;
import crazypants.enderio.machine.obelisk.spawn.ContainerAbstractSpawningObelisk;
import crazypants.enderio.machine.obelisk.spawn.GuiAbstractSpawningObelisk;
import crazypants.enderio.machine.obelisk.spawn.SpawningObeliskController;

public class BlockRelocatorObelisk extends BlockObeliskAbstract<TileRelocatorObelisk> {

  public static BlockRelocatorObelisk create() {
    BlockRelocatorObelisk res = new BlockRelocatorObelisk();
    res.init();

    // Just making sure its loaded
    SpawningObeliskController.instance.toString();

    return res;
  }

  protected BlockRelocatorObelisk() {
    super(ModObject.blockSpawnRelocator, TileRelocatorObelisk.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileRelocatorObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerAbstractSpawningObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileRelocatorObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiAbstractSpawningObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_SPAWN_RELOCATOR;
  }
}
