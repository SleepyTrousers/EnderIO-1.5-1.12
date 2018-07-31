package crazypants.enderio.base.conduit.item;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIOTab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemFunctionUpgrade extends Item implements IResourceTooltipProvider {

  private final FunctionUpgrade upgradeType;

  public static ItemFunctionUpgrade createUpgrade(@Nonnull IModObject modObject) {
    return new ItemFunctionUpgrade(modObject, FunctionUpgrade.EXTRACT_SPEED_UPGRADE);
  }

  public static ItemFunctionUpgrade createDowngrade(@Nonnull IModObject modObject) {
    return new ItemFunctionUpgrade(modObject, FunctionUpgrade.EXTRACT_SPEED_DOWNGRADE);
  }

  public static ItemFunctionUpgrade createRSCraftingUpgrade(@Nonnull IModObject modObject) {
    return new ItemFunctionUpgrade(modObject, FunctionUpgrade.RS_CRAFTING_UPGRADE);
  }

  public static ItemFunctionUpgrade createRSCraftingSpeedUpgrade(@Nonnull IModObject modObject) {
    return new ItemFunctionUpgrade(modObject, FunctionUpgrade.RS_CRAFTING_SPEED_UPGRADE);
  }

  public static ItemFunctionUpgrade createRSCraftingSpeedDowngrade(@Nonnull IModObject modObject) {
    return new ItemFunctionUpgrade(modObject, FunctionUpgrade.RS_CRAFTING_SPEED_DOWNGRADE);
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
