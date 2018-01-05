package crazypants.enderio.conduit.item.filter;

import java.util.List;

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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBasicItemFilter extends Item implements IItemFilterUpgrade, IHaveRenderers  {

  private static final String[] TYPES = { "filterUpgradeBasic", "filterUpgradeAdvanced", "filterUpgradeLimited" };

  public static ItemBasicItemFilter create() {
    ItemBasicItemFilter result = new ItemBasicItemFilter();
    result.init();
    return result;
  }

  protected ItemBasicItemFilter() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
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
    int damage = MathHelper.clamp(stack.getItemDamage(), 0, TYPES.length);
    ItemFilter filter = new ItemFilter(damage);
    if (stack.hasTagCompound() && stack.getTagCompound().hasKey("filter")) {
      filter.readFromNBT(stack.getTagCompound().getCompoundTag("filter"));
    }
    return filter;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    for (int i = 0; i < TYPES.length; i++) {
      ClientUtil.regRenderer(this, i, TYPES[i]);
    }
  }  

  @Override
  public String getUnlocalizedName(ItemStack par1ItemStack) {
    return "enderio." + TYPES[MathHelper.clamp(par1ItemStack.getItemDamage(), 0, TYPES.length)];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List<ItemStack> par3List) {
    for (int i = 0; i < TYPES.length; i++) {
      par3List.add(new ItemStack(this, 1, i));
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
