package crazypants.enderio.conduit.item;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.init.IModObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemFunctionUpgrade extends Item implements IResourceTooltipProvider {

  private final FunctionUpgrade upgradeType;

  public static ItemFunctionUpgrade createInventoryPanelUpgrade(@Nonnull IModObject modObject) {
    return new ItemFunctionUpgrade(modObject, FunctionUpgrade.INVENTORY_PANEL);
  }

  protected ItemFunctionUpgrade(@Nonnull IModObject modObject, FunctionUpgrade upgradeType) {
    setCreativeTab(EnderIOTab.tabEnderIOItems);
    modObject.apply(this);
    setHasSubtypes(true);
    setMaxDamage(0);
    setMaxStackSize(64);
    this.upgradeType = upgradeType;

  }

  public static FunctionUpgrade getFunctionUpgrade(@Nonnull ItemStack stack) {
    if (stack.getItem() instanceof ItemFunctionUpgrade) {
      return ((ItemFunctionUpgrade) stack.getItem()).getFunctionUpgrade();
    }
    return null;
  }

  public FunctionUpgrade getFunctionUpgrade() {
    return upgradeType;
  }

  @Override
  public @Nonnull String getUnlocalizedNameForTooltip(@Nonnull ItemStack itemStack) {
    return getUnlocalizedName(itemStack);
  }

}
