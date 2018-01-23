package crazypants.enderio.base.filter.items;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.filter.FilterRegistry;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.base.filter.filters.SpeciesItemFilter;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.util.ClientUtil;
import crazypants.enderio.util.NbtValue;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.GameRegistry;
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

  protected void init() {
    GameRegistry.register(this);
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
  public void getSubItems(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
    subItems.add(new ItemStack(this, 1, 0));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack par1ItemStack, @Nonnull EntityPlayer par2EntityPlayer, @Nonnull List<String> par3List, boolean par4) {
    if (FilterRegistry.isFilterSet(par1ItemStack)) {
      if (!SpecialTooltipHandler.showAdvancedTooltips()) {
        par3List.add(EnderIO.lang.localize("itemConduitFilterUpgrade"));
        SpecialTooltipHandler.addShowDetailsTooltip(par3List);
      } else {
        par3List.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.configured"));
        par3List.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.clearConfigMethod"));
      }
    } else {
      par3List.add(EnderIO.lang.localize("itemConduitFilterUpgrade"));
    }
  }

}
