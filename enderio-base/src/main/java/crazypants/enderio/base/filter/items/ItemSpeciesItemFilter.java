package crazypants.enderio.base.filter.items;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.common.TileEntityBase;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.base.filter.filters.SpeciesItemFilter;
import crazypants.enderio.base.filter.gui.ContainerFilter;
import crazypants.enderio.base.filter.gui.SpeciesItemFilterGui;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.ClientUtil;
import crazypants.enderio.util.NbtValue;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//TODO: Move to integration-forestry after big conduit merge

public class ItemSpeciesItemFilter extends Item implements IItemFilterUpgrade, IHaveRenderers {

  public static ItemSpeciesItemFilter create(@Nonnull IModObject modObject) {
    return new ItemSpeciesItemFilter(modObject);
  }

  protected ItemSpeciesItemFilter(@Nonnull IModObject modObject) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  @Override
  public IItemFilter createFilterFromStack(@Nonnull ItemStack stack) {
    IItemFilter filter = new SpeciesItemFilter();
    if (NbtValue.FILTER.hasTag(stack)) {
      filter.readFromNBT(NbtValue.FILTER.getTag(stack));
    }
    return filter;
  }

  @Override
  public void registerRenderers(@Nonnull IModObject modObject) {
    ClientUtil.regRenderer(this, 0, "filter_upgrade_species");
  }

  @Override
  @Nonnull
  public String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    return "enderio.filterUpgradeSpecies";
  }

  @Override
  public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
    if (isInCreativeTab(tab)) {
      list.add(new ItemStack(this, 1, 0));
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn) {
    super.addInformation(stack, worldIn, tooltip, flagIn);
    if (FilterRegistry.isFilterSet(stack)) {
      if (!SpecialTooltipHandler.showAdvancedTooltips()) {
        tooltip.add(EnderIO.lang.localize("itemConduitFilterUpgrade"));
        SpecialTooltipHandler.addShowDetailsTooltip(tooltip);
      } else {
        tooltip.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.configured"));
        tooltip.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.clearConfigMethod"));
      }
    } else {
      tooltip.add(EnderIO.lang.localize("itemConduitFilterUpgrade"));
    }
  }

  @Override
  @Nullable
  public GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1) {
    return new SpeciesItemFilterGui(player.inventory, new ContainerFilter(player.inventory, param1, (TileEntityBase) world.getTileEntity(pos), facing), false,
        world.getTileEntity(pos));
  }

}
