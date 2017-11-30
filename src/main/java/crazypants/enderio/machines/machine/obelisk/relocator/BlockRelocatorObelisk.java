package crazypants.enderio.machines.machine.obelisk.relocator;

import javax.annotation.Nonnull;

import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.machines.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.machines.machine.obelisk.ContainerAbstractObelisk;
import crazypants.enderio.machines.machine.obelisk.GuiRangedObelisk;
import crazypants.enderio.machines.machine.obelisk.spawn.SpawningObeliskController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
  public Object getServerGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileRelocatorObelisk te = getTileEntity(world, pos);
    if (te != null) {
      return new ContainerAbstractObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos) {
    TileRelocatorObelisk te = getTileEntity(world, pos);
    if (te != null) {
      return new GuiRangedObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_SPAWN_RELOCATOR;
  }
}
