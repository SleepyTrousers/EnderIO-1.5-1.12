package crazypants.enderio.conduit.item.filter;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.ModObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 *
 * @author matthias
 */
public class ItemPowerItemFilter extends Item implements IItemFilterUpgrade, IResourceTooltipProvider {

  public static ItemPowerItemFilter create() {
    ItemPowerItemFilter result = new ItemPowerItemFilter();
    result.init();
    return result;
  }

  protected ItemPowerItemFilter() {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    setUnlocalizedName(ModObject.itemPowerItemFilter.getUnlocalisedName());
    setRegistryName(ModObject.itemPowerItemFilter.getUnlocalisedName());
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  protected void init() {
    GameRegistry.register(this);
  }

  @Override
  public IItemFilter createFilterFromStack(ItemStack stack) {
    IItemFilter filter = new PowerItemFilter();
    if(stack.getTagCompound() != null && stack.getTagCompound().hasKey("filter")) {
      filter.readFromNBT(stack.getTagCompound().getCompoundTag("filter"));
    }
    return filter;
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerIcons(IIconRegister IIconRegister) {
//    itemIcon = IIconRegister.registerIcon("enderio:filterUpgradePower");
//  }

  @Override
  public String getUnlocalizedName(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

}
