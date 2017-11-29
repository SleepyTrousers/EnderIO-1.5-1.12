package crazypants.enderio.base.filter.items;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.IItemFilterUpgrade;
import crazypants.enderio.base.filter.filters.PowerItemFilter;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.util.NbtValue;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 *
 * @author matthias
 */
public class ItemPowerItemFilter extends Item implements IItemFilterUpgrade, IResourceTooltipProvider {

  public static ItemPowerItemFilter create(@Nonnull IModObject modObject) {
    return new ItemPowerItemFilter(modObject);
  }

  protected ItemPowerItemFilter(@Nonnull IModObject modObject) {
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
    IItemFilter filter = new PowerItemFilter();
    if (NbtValue.FILTER.hasTag(stack)) {
      filter.readFromNBT(NbtValue.FILTER.getTag(stack));
    }
    return filter;
  }

  // @Override
  // @SideOnly(Side.CLIENT)
  // public void registerIcons(IIconRegister IIconRegister) {
  // itemIcon = IIconRegister.registerIcon("enderio:filterUpgradePower");
  // }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack stack) {
    return getUnlocalizedName();
  }

}
