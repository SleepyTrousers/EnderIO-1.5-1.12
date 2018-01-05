package crazypants.enderio.conduit.item.filter;

import java.util.List;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.ModObject;
import crazypants.enderio.conduit.item.FilterRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModItemFilter extends Item implements IItemFilterUpgrade, IResourceTooltipProvider {

  public static ItemModItemFilter create() {
    ItemModItemFilter result = new ItemModItemFilter();
    result.init();
    return result;
  }

  protected ItemModItemFilter() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(ModObject.itemModItemFilter.getUnlocalisedName());
    setRegistryName(ModObject.itemModItemFilter.getUnlocalisedName());
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public IItemFilter createFilterFromStack(ItemStack stack) {
    IItemFilter filter = new ModItemFilter();
    if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("filter")) {
      filter.readFromNBT(stack.getTagCompound().getCompoundTag("filter"));
    }
    return filter;
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerIcons(IIconRegister IIconRegister) {
//    itemIcon = IIconRegister.registerIcon("enderio:modItemFilter");
//  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List, boolean par4) {
    if(FilterRegister.isFilterSet(par1ItemStack)) {
      if(SpecialTooltipHandler.showAdvancedTooltips()) {
        par3List.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.configured"));
        par3List.add(TextFormatting.ITALIC + EnderIO.lang.localize("itemConduitFilterUpgrade.clearConfigMethod"));
      }
    }
  }

}
