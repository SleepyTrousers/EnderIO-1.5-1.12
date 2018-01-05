package crazypants.enderio.machines.machine.obelisk.xp;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.machines.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.machines.machine.obelisk.AbstractRangedTileEntity;
import crazypants.enderio.machines.machine.obelisk.ContainerRangedObelisk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractBlockRangedObelisk<T extends AbstractRangedTileEntity> extends AbstractBlockObelisk<T> {

  protected AbstractBlockRangedObelisk(@Nonnull IModObject mo, Class<T> teClass) {
    super(mo, teClass);
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull T te) {
    return new ContainerRangedObelisk(player.inventory, te);
  }


}
