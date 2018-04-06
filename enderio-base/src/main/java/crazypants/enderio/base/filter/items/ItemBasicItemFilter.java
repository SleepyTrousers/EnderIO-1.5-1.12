package crazypants.enderio.base.filter.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.base.filter.filters.item.ItemFilter;
import crazypants.enderio.base.filter.gui.BasicItemFilterGui;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBasicItemFilter extends Item implements IItemFilterUpgrade, IResourceTooltipProvider {

  protected BasicFilterTypes filterType;

  public static ItemBasicItemFilter createBasicItemFilter(@Nonnull IModObject modObject) {
    return new ItemBasicItemFilter(modObject, BasicFilterTypes.filterUpgradeBasic);
  }

  public static ItemBasicItemFilter createAdvancedItemFilter(@Nonnull IModObject modObject) {
    return new ItemBasicItemFilter(modObject, BasicFilterTypes.filterUpgradeAdvanced);
  }

  public static ItemBasicItemFilter createLimitedItemFilter(@Nonnull IModObject modObject) {
    return new ItemBasicItemFilter(modObject, BasicFilterTypes.filterUpgradeLimited);
  }

  public static ItemBasicItemFilter createBigItemFilter(@Nonnull IModObject modObject) {
    return new ItemBasicItemFilter(modObject, BasicFilterTypes.filterUpgradeBig);
  }

  public static ItemBasicItemFilter createBigAdvancedItemFilter(@Nonnull IModObject modObject) {
    return new ItemBasicItemFilter(modObject, BasicFilterTypes.filterUpgradeBigAdvanced);
  }

  protected ItemBasicItemFilter(@Nonnull IModObject modObject, BasicFilterTypes filterType) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setMaxDamage(0);
    setHasSubtypes(true);
    setMaxStackSize(64);
    this.filterType = filterType;
  }

  @Override
  public IItemFilter createFilterFromStack(@Nonnull ItemStack stack) {
    ItemFilter filter = new ItemFilter(filterType);
    NBTTagCompound tag = NbtValue.FILTER.getTag(stack);

    // TODO work out why this works
    // For some reason Advanced and Limited filters will have their state overridden if they run readFromNBT(),
    // however the basic filter is not saved to inventory if readFromNBT() is not run
    // ^ Response to above - need to move filters to use @Store in conduits
    if (!tag.hasNoTags() || filterType == BasicFilterTypes.filterUpgradeBasic) {
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
    return new BasicItemFilterGui(player.inventory,
        new ContainerFilter<IItemFilter>(player.inventory, param1, (TileEntityBase) world.getTileEntity(pos), facing), world.getTileEntity(pos));
  }

}
