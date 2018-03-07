package crazypants.enderio.base.conduit.item;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Allows item conduits to extract more quickly
 */
public class ItemExtractSpeedUpgrade extends Item implements IResourceTooltipProvider {

  private static final SpeedUpgrade UPGRADES[] = SpeedUpgrade.values();
  private final SpeedUpgrade upgradeType;

  public static ItemExtractSpeedUpgrade createUpgrade(@Nonnull IModObject modObject) {
    return new ItemExtractSpeedUpgrade(modObject, SpeedUpgrade.UPGRADE);
  }

  public static ItemExtractSpeedUpgrade createDowngrade(@Nonnull IModObject modObject) {
    return new ItemExtractSpeedUpgrade(modObject, SpeedUpgrade.DOWNGRADE);
  }

  protected ItemExtractSpeedUpgrade(@Nonnull IModObject modObject, SpeedUpgrade upgradeType) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    this.upgradeType = upgradeType;
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
  }

  public static SpeedUpgrade getSpeedUpgrade(@Nonnull ItemStack stack) {
    if (stack.getItem() instanceof ItemExtractSpeedUpgrade) {
      return ((ItemExtractSpeedUpgrade) stack.getItem()).getSpeedUpgrade();
    }
    return null;
  }

  public SpeedUpgrade getSpeedUpgrade() {
    return upgradeType;
  }

  @Override
  public String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

}
