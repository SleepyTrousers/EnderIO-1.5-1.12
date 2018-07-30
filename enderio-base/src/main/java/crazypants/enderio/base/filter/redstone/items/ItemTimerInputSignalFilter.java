package crazypants.enderio.base.filter.redstone.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilterContainer;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.filter.gui.IncrementingValueFilterGui;
import crazypants.enderio.base.filter.redstone.IInputSignalFilter;
import crazypants.enderio.base.filter.redstone.TimerInputSignalFilter;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTimerInputSignalFilter extends Item implements IItemInputSignalFilterUpgrade {

  public static ItemTimerInputSignalFilter create(@Nonnull IModObject modObject) {
    return new ItemTimerInputSignalFilter(modObject);
  }

  public ItemTimerInputSignalFilter(@Nonnull IModObject modObject) {
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IInputSignalFilter createFilterFromStack(@Nonnull ItemStack stack) {
    TimerInputSignalFilter filter = new TimerInputSignalFilter();
    if (NbtValue.FILTER.hasTag(stack)) {
      filter.readFromNBT(NbtValue.FILTER.getTag(stack));
    }
    return filter;
  }

  @SideOnly(Side.CLIENT)
  @Override
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    Container container = player.openContainer;
    if (container instanceof IFilterContainer) {
      return new IncrementingValueFilterGui(player.inventory, new ContainerFilter(player, (TileEntityBase) world.getTileEntity(pos), facing, param1),
          world.getTileEntity(pos), ((IFilterContainer<TimerInputSignalFilter>) container).getFilter(param1));
    } else {
      return new IncrementingValueFilterGui(player.inventory, new ContainerFilter(player, null, facing, param1), null,
          FilterRegistry.getFilterForUpgrade(player.getHeldItem(EnumHand.values()[param1])));
    }
  }

}
