package crazypants.enderio.base.filter.item.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IFilterContainer;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.filter.gui.ExistingItemFilterGui;
import crazypants.enderio.base.filter.item.ExistingItemFilter;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
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
import net.minecraftforge.items.IItemHandler;

public class ItemExistingItemFilter extends Item implements IItemFilterItemUpgrade, IResourceTooltipProvider {

  public static ItemExistingItemFilter create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemExistingItemFilter(modObject);
  }

  protected ItemExistingItemFilter(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IItemFilter createFilterFromStack(@Nonnull ItemStack stack) {
    IItemFilter filter = new ExistingItemFilter();
    NBTTagCompound tag = NbtValue.FILTER.getTag(stack);
    filter.readFromNBT(tag);
    return filter;
  }

  @Override
  public @Nonnull EnumActionResult onItemUse(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumHand hand,
      @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (world.isRemote) {
      return EnumActionResult.SUCCESS;
    }

    if (player.isSneaking()) {
      IItemHandler externalInventory = ItemTools.getExternalInventory(world, pos, side);
      if (externalInventory != null) {
        ItemStack heldItem = player.getHeldItem(hand);
        ExistingItemFilter filter = (ExistingItemFilter) createFilterFromStack(heldItem);
        player.sendStatusMessage(filter.mergeSnapshot(externalInventory) ? Lang.ITEM_FILTER_UPDATED.toChatServer() : Lang.ITEM_FILTER_NOTUPDATED.toChatServer(),
            true);
        FilterRegistry.writeFilterToStack(filter, heldItem);
        return EnumActionResult.SUCCESS;
      }
    }

    return EnumActionResult.PASS;
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(@Nonnull World worldIn, @Nonnull EntityPlayer playerIn, @Nonnull EnumHand handIn) {
    if (!worldIn.isRemote && playerIn.isSneaking()) {
      ModObject.itemExistingItemFilter.openGui(worldIn, playerIn.getPosition(), playerIn, null, handIn.ordinal());
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
    return super.onItemRightClick(worldIn, playerIn, handIn);
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
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
    Container container = player.openContainer;
    if (container instanceof IFilterContainer) {
      return new ExistingItemFilterGui(player.inventory, new ContainerFilter(player, (TileEntityBase) world.getTileEntity(pos), facing, param1),
          world.getTileEntity(pos), ((IFilterContainer<IItemFilter>) container).getFilter(param1));
    } else {
      return new ExistingItemFilterGui(player.inventory, new ContainerFilter(player, null, facing, param1), null,
          FilterRegistry.getFilterForUpgrade(player.getHeldItem(EnumHand.values()[param1])));
    }
  }

}
