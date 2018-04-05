package crazypants.enderio.base.filter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.gui.handler.IEioGuiHandler;
import crazypants.enderio.base.machine.interfaces.IClearableConfiguration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IItemFilterUpgrade extends IClearableConfiguration, IEioGuiHandler.WithPos {

  IItemFilter createFilterFromStack(@Nonnull ItemStack stack);

  @Override
  @Nullable
  default Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return new ContainerFilter<IItemFilter>(player.inventory, param1, (TileEntityBase) world.getTileEntity(pos), facing);
  }

}
