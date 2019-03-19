package crazypants.enderio.base.filter.redstone.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilterContainer;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.filter.gui.RedstoneSignalLogicGui;
import crazypants.enderio.base.filter.redstone.IOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.LogicOutputSignalFilter;
import crazypants.enderio.base.filter.redstone.LogicOutputSignalFilter.EnumSignalFilterType;
import crazypants.enderio.util.NbtValue;
import net.minecraft.block.Block;
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

public class ItemBasicOutputSignalFilter extends Item implements IItemOutputSignalFilterUpgrade {

  private final @Nonnull EnumSignalFilterType filterType;

  public static ItemBasicOutputSignalFilter createOr(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemBasicOutputSignalFilter(modObject, EnumSignalFilterType.OR);
  }

  public static ItemBasicOutputSignalFilter createAnd(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemBasicOutputSignalFilter(modObject, EnumSignalFilterType.AND);
  }

  public static ItemBasicOutputSignalFilter createNor(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemBasicOutputSignalFilter(modObject, EnumSignalFilterType.NOR);
  }

  public static ItemBasicOutputSignalFilter createNand(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemBasicOutputSignalFilter(modObject, EnumSignalFilterType.NAND);
  }

  public static ItemBasicOutputSignalFilter createXor(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemBasicOutputSignalFilter(modObject, EnumSignalFilterType.XOR);
  }

  public static ItemBasicOutputSignalFilter createXnor(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemBasicOutputSignalFilter(modObject, EnumSignalFilterType.XNOR);
  }

  public ItemBasicOutputSignalFilter(@Nonnull IModObject modObject, @Nonnull EnumSignalFilterType filterType) {
    this.filterType = filterType;
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IOutputSignalFilter createFilterFromStack(@Nonnull ItemStack stack) {
    LogicOutputSignalFilter filter = new LogicOutputSignalFilter(filterType);
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
      return new RedstoneSignalLogicGui(player.inventory, new ContainerFilter(player, (TileEntityBase) world.getTileEntity(pos), facing, param1),
          world.getTileEntity(pos), ((IFilterContainer<LogicOutputSignalFilter>) container).getFilter(param1));
    } else {
      return new RedstoneSignalLogicGui(player.inventory, new ContainerFilter(player, null, facing, param1), null,
          FilterRegistry.getFilterForUpgrade(player.getHeldItem(EnumHand.values()[param1])));
    }
  }

}
