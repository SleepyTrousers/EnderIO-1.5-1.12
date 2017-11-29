package crazypants.enderio.conduit.item.filter;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.ModObject;
import crazypants.enderio.base.render.IHaveRenderers;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.util.ClientUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSpeciesItemFilter extends Item implements IItemFilterUpgrade, IHaveRenderers  {

  public static ItemSpeciesItemFilter create() {
    ItemSpeciesItemFilter result = new ItemSpeciesItemFilter();
    result.init();
    return result;
  }

  protected ItemSpeciesItemFilter() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(ModObject.itemSpeciesItemFilter.getUnlocalisedName());
    setRegistryName(ModObject.itemSpeciesItemFilter.getUnlocalisedName());
    setHasSubtypes(false);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public IItemFilter createFilterFromStack(ItemStack stack) {
    IItemFilter filter = new SpeciesItemFilter();
    if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("filter")) {
      filter.readFromNBT(stack.getTagCompound().getCompoundTag("filter"));
    }
    return filter;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {                  
    ClientUtil.regRenderer(this, 0,"filterUpgradeSpecies");
  }

  @Override
  @Nonnull
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    return "enderio.filterUpgradeSpecies";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(@Nonnull Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    par3List.add(new ItemStack(this, 1, 0));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    if(FilterRegister.isFilterSet(par1ItemStack)) {
      if(!SpecialTooltipHandler.showAdvancedTooltips()) {
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
