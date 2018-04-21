package crazypants.enderio.base.filter.fluid.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilterContainer;
import crazypants.enderio.base.filter.fluid.FluidFilter;
import crazypants.enderio.base.filter.fluid.IFluidFilter;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.filter.gui.FluidFilterGui;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFluidFilter extends Item implements IItemFilterFluidUpgrade, IResourceTooltipProvider {

  public static ItemFluidFilter create(@Nonnull IModObject modObject) {
    return new ItemFluidFilter(modObject);
  }

  protected ItemFluidFilter(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IFluidFilter createFilterFromStack(@Nonnull ItemStack stack) {
    IFluidFilter filter = new FluidFilter();
    if (NbtValue.FILTER.hasTag(stack)) {
      filter.readFromNBT(NbtValue.FILTER.getTag(stack));
    }
    return filter;
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    if (!world.isRemote && player.isSneaking()) {
      ModObject.itemFluidFilter.openGui(world, player.getPosition(), player, null, hand.ordinal());
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
    return super.onItemRightClick(world, player, hand);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (FilterRegistry.isFilterSet(stack)) {
      if (SpecialTooltipHandler.showAdvancedTooltips()) {
        tooltip.add(Lang.ITEM_FILTER_CONFIGURED.get(TextFormatting.ITALIC));
        tooltip.add(Lang.ITEM_FILTER_CLEAR.get(TextFormatting.ITALIC));
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  @Nullable
  @SideOnly(Side.CLIENT)
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    Container container = player.openContainer;
    if (container instanceof IFilterContainer) {
      return new FluidFilterGui(player.inventory, new ContainerFilter(player, (TileEntityBase) world.getTileEntity(pos), facing, param1),
          world.getTileEntity(pos), ((IFilterContainer<IFluidFilter>) container).getFilter(param1));
    } else {
      return new FluidFilterGui(player.inventory, new ContainerFilter(player, null, facing, param1), null,
          FilterRegistry.getFilterForUpgrade(player.getHeldItem(EnumHand.values()[param1])));
    }
  }

}
