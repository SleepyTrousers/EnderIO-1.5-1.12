package crazypants.enderio.machines.machine.obelisk.relocator;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.machines.machine.obelisk.ContainerAbstractObelisk;
import crazypants.enderio.machines.machine.obelisk.GuiRangedObelisk;
import crazypants.enderio.machines.machine.obelisk.spawn.SpawningObeliskController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockRelocatorObelisk extends AbstractBlockObelisk<TileRelocatorObelisk> {

  public static BlockRelocatorObelisk create(@Nonnull IModObject modObject) {
    BlockRelocatorObelisk res = new BlockRelocatorObelisk(modObject);
    res.init();

    // Just making sure its loaded
    SpawningObeliskController.instance.toString();

    return res;
  }

  protected BlockRelocatorObelisk(@Nonnull IModObject modObject) {
    super(modObject, TileRelocatorObelisk.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileRelocatorObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerAbstractObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileRelocatorObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiRangedObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_SPAWN_RELOCATOR;
  }
}
