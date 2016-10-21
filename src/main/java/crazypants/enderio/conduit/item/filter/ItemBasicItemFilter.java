package crazypants.enderio.conduit.item.filter;

import java.util.List;

import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBasicItemFilter extends Item implements IItemFilterUpgrade, IHaveRenderers  {

  public static ItemBasicItemFilter create() {
    ItemBasicItemFilter result = new ItemBasicItemFilter();
    result.init();
    return result;
  }

  protected ItemBasicItemFilter() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemBasicFilterUpgrade.getUnlocalisedName());
    setRegistryName(ModObject.itemBasicFilterUpgrade.getUnlocalisedName());
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public IItemFilter createFilterFromStack(ItemStack stack) {
    int damage = MathHelper.clamp_int(stack.getItemDamage(), 0, 1);
    ItemFilter filter;
    if(damage == 0) {
      filter = new ItemFilter(false);
    } else {
      filter = new ItemFilter(true);
    }
    if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("filter")) {
      filter.readFromNBT(stack.getTagCompound().getCompoundTag("filter"));
    }
    return filter;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {                  
    ClientUtil.regRenderer(this, 0,"filterUpgradeBasic");
    ClientUtil.regRenderer(this, 1 ,"filterUpgradeAdvanced");    
  }  

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    int i = MathHelper.clamp_int(par1ItemStack.getItemDamage(), 0, 1);
    return i == 0 ? "enderio.filterUpgradeBasic" : "enderio.filterUpgradeAdvanced";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int j = 0; j < 2; ++j) {
      par3List.add(new ItemStack(this, 1, j));
    }
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
