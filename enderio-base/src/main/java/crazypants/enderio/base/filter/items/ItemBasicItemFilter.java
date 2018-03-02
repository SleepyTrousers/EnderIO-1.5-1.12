package crazypants.enderio.base.filter.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.base.filter.filters.ItemFilter;
import crazypants.enderio.base.filter.gui.BasicItemFilterGui;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.ClientUtil;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBasicItemFilter extends Item implements IItemFilterUpgrade, IHaveRenderers {

  public static ItemBasicItemFilter create(@Nonnull IModObject modObject) {
    return new ItemBasicItemFilter(modObject);
  }

  protected ItemBasicItemFilter(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IItemFilter createFilterFromStack(@Nonnull ItemStack stack) {
    int damage = MathHelper.clamp(stack.getItemDamage(), 0, BasicFilterTypes.values().length);
    ItemFilter filter = new ItemFilter(damage);
    NBTTagCompound tag = NbtValue.FILTER.getTag(stack);
    filter.readFromNBT(tag);
    return filter;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    for (BasicFilterTypes filterType : BasicFilterTypes.values()) {
      ClientUtil.regRenderer(this, filterType.ordinal(), filterType.getBaseName());
    }
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    return getUnlocalizedName() + "_" + BasicFilterTypes.getTypeFromMeta(par1ItemStack.getMetadata());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      for (BasicFilterTypes filterType : BasicFilterTypes.values()) {
        list.add(new ItemStack(this, 1, filterType.ordinal()));
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (FilterRegistry.isFilterSet(stack)) {
      if (!SpecialTooltipHandler.showAdvancedTooltips()) {
        tooltip.add(Lang.CONDUIT_FILTER.get());
        SpecialTooltipHandler.addShowDetailsTooltip(tooltip);
      } else {
        tooltip.add(Lang.CONDUIT_FILTER_CONFIGURED.get(TextFormatting.ITALIC));
        tooltip.add(Lang.CONDUIT_FILTER_CLEAR.get(TextFormatting.ITALIC));
      }
    } else {
      tooltip.add(Lang.CONDUIT_FILTER.get());
    }
  }

  @Override
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return new BasicItemFilterGui(player.inventory, new ContainerFilter(player.inventory, param1, (TileEntityBase) world.getTileEntity(pos), facing), false,
        world.getTileEntity(pos));
  }

}
