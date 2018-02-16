package crazypants.enderio.base.filter.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.base.filter.filters.ExistingItemFilter;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;

public class ItemExistingItemFilter extends Item implements IItemFilterUpgrade, IResourceTooltipProvider {

  public static ItemExistingItemFilter create(@Nonnull IModObject modObject) {
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
        player.sendStatusMessage(
            filter.mergeSnapshot(externalInventory) ? Lang.CONDUIT_FILTER_UPDATED.toChatServer() : Lang.CONDUIT_FILTER_NOTUPDATED.toChatServer(), true);
        FilterRegistry.writeFilterToStack(filter, heldItem);
        return EnumActionResult.SUCCESS;
      }
    }

    return EnumActionResult.PASS;
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
      if (flagIn.isAdvanced()) {
        tooltip.add(Lang.CONDUIT_FILTER_CONFIGURED.get(TextFormatting.ITALIC));
        tooltip.add(Lang.CONDUIT_FILTER_CLEAR.get(TextFormatting.ITALIC));
      }
    }
  }

}
