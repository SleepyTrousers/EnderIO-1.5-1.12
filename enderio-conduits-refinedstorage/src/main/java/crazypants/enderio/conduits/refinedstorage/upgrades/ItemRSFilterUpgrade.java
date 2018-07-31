package crazypants.enderio.conduits.refinedstorage.upgrades;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.base.conduit.item.FunctionUpgrade;
import crazypants.enderio.base.conduit.item.ItemFunctionUpgrade;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilterContainer;
import crazypants.enderio.base.filter.gui.BasicItemFilterGui;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.filter.item.ItemFilter;
import crazypants.enderio.base.filter.item.items.BasicFilterTypes;
import crazypants.enderio.base.filter.item.items.IItemFilterItemUpgrade;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRSFilterUpgrade extends ItemFunctionUpgrade implements IItemFilterItemUpgrade, IResourceTooltipProvider {

  protected @Nonnull BasicFilterTypes filterType;

  public static ItemRSFilterUpgrade create_export(@Nonnull IModObject modObject) {
    return new ItemRSFilterUpgrade(modObject, BasicFilterTypes.filterUpgradeBasic, FunctionUpgrade.RS_EXPORT_UPGRADE);
  }

  public static ItemRSFilterUpgrade create_import(@Nonnull IModObject modObject) {
    return new ItemRSFilterUpgrade(modObject, BasicFilterTypes.filterUpgradeBasic, FunctionUpgrade.RS_IMPORT_UPGRADE);
  }

  protected ItemRSFilterUpgrade(@Nonnull IModObject modObject, @Nonnull BasicFilterTypes filterType, @Nonnull FunctionUpgrade upgrade) {
    super(modObject, upgrade);
    this.filterType = filterType;
  }

  @Override
  public IItemFilter createFilterFromStack(@Nonnull ItemStack stack) {
    ItemFilter filter = new ItemFilter(filterType);
    NBTTagCompound tag = NbtValue.FILTER.getTag(stack);
    if (!tag.hasNoTags()) {
      filter.readFromNBT(tag);
    }
    return filter;
  }

  @Override
  @Nonnull
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull EnumHand hand) {
    if (!world.isRemote && player.isSneaking()) {
      ModObject.itemBasicItemFilter.openGui(world, player.getPosition(), player, null, hand.ordinal());
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

  @Override
  @Nullable
  @SideOnly(Side.CLIENT)
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    Container container = player.openContainer;
    if (container != null && container instanceof IFilterContainer) {
      return new BasicItemFilterGui(player.inventory, new ContainerFilter(player, (TileEntityBase) world.getTileEntity(pos), facing, param1),
          world.getTileEntity(pos), ((IFilterContainer<IItemFilter>) container).getFilter(param1));
    } else {
      return new BasicItemFilterGui(player.inventory, new ContainerFilter(player, null, facing, param1), null,
          FilterRegistry.getFilterForUpgrade(player.getHeldItem(EnumHand.values()[param1])));
    }
  }

}
