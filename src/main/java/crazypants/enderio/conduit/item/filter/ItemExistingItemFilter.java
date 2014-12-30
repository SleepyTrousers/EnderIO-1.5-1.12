package crazypants.enderio.conduit.item.filter;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.item.FilterRegister;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.util.Lang;

public class ItemExistingItemFilter extends Item implements IItemFilterUpgrade, IResourceTooltipProvider {

  public static ItemExistingItemFilter create() {
    ItemExistingItemFilter result = new ItemExistingItemFilter();
    result.init();
    return result;
  }

  protected ItemExistingItemFilter() {
    setCreativeTab(EnderIOTab.tabEnderIO);
    setUnlocalizedName(ModObject.itemExistingItemFilter.unlocalisedName);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.registerItem(this, ModObject.itemExistingItemFilter.unlocalisedName);
  }

  @Override
  public IItemFilter createFilterFromStack(ItemStack stack) {
    IItemFilter filter = new ExistingItemFilter();
    if(stack.stackTagCompound != null && stack.stackTagCompound.hasKey("filter")) {
      filter.readFromNBT(stack.stackTagCompound.getCompoundTag("filter"));
    }
    return filter;
  }

  @Override
  public void registerIcons(IIconRegister IIconRegister) {
    itemIcon = IIconRegister.registerIcon("enderio:existingItemFilter");
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }
  
  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {          
    if(FilterRegister.isFilterSet(par1ItemStack)) {      
      if(TooltipAddera.instance.showAdvancedTooltips()) {        
        par3List.add(EnumChatFormatting.ITALIC + Lang.localize("itemConduitFilterUpgrade.configured"));
        par3List.add(EnumChatFormatting.ITALIC + Lang.localize("itemConduitFilterUpgrade.clearConfigMethod"));
      }      
    }
  }
  
  //par3List.add(EnumChatFormatting.ITALIC + Lang.localize("itemConduitFilterUpgrade.clearConfigMethod"));

}
