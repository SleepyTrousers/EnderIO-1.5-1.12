package crazypants.enderio.conduit.item.filter;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.IResourceTooltipProvider;

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

}
