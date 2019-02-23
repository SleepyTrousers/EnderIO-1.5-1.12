package crazypants.enderio.powertools.machine.capbank;

import javax.annotation.Nonnull;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import crazypants.enderio.base.EnderIOTab;
import crazypants.enderio.base.ItemEIO;
import crazypants.enderio.base.power.forge.item.IInternalPoweredItem;
import crazypants.enderio.base.render.itemoverlay.PowerBarOverlayRenderHelper;
import crazypants.enderio.powertools.config.PersonalConfig;
import crazypants.enderio.powertools.init.PowerToolObject;
import crazypants.enderio.util.NbtValue;
import net.minecraft.item.ItemStack;

public class BlockItemCapBank extends ItemEIO implements IOverlayRenderAware, IInternalPoweredItem {

  public static @Nonnull ItemStack createItemStackWithPower(int meta, int storedEnergy) {
    ItemStack res = new ItemStack(PowerToolObject.block_cap_bank.getBlockNN(), 1, meta);
    if (storedEnergy > 0) {
      setStoredEnergyForItem(res, storedEnergy);
    }
    return res;
  }

  public static int getStoredEnergyForItem(@Nonnull ItemStack item) {
    return NbtValue.ENERGY.getInt(item);
  }

  public static void setStoredEnergyForItem(@Nonnull ItemStack item, int storedEnergy) {
    NbtValue.ENERGY.setInt(item, Math.max(0, storedEnergy));
  }

  public BlockItemCapBank(@Nonnull BlockCapBank blockCapBank) {
    super(blockCapBank);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
  }

  @Override
  public int getMetadata(int par1) {
    return par1;
  }

  @Override
  public @Nonnull String getUnlocalizedName(@Nonnull ItemStack par1ItemStack) {
    return CapBankType.getTypeFromMeta(par1ItemStack.getMetadata()).getUnlocalizedName();
  }

  @Override
  public void renderItemOverlayIntoGUI(@Nonnull ItemStack stack, int xPosition, int yPosition) {
    if (PersonalConfig.capacitorBankRenderPowerOverlayOnItem.get()) {
      PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
    }
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return CapBankType.getTypeFromMeta(stack.getMetadata()).isCreative() || super.hasEffect(stack);
  }

  @Override
  public int getMaxEnergyStored(@Nonnull ItemStack stack) {
    return CapBankType.getTypeFromMeta(stack.getMetadata()).getMaxEnergyStored();
  }

  @Override
  public int getMaxInput(@Nonnull ItemStack container) {
    return CapBankType.getTypeFromMeta(container.getMetadata()).getMaxIO();
  }

  @Override
  public int getMaxOutput(@Nonnull ItemStack container) {
    return CapBankType.getTypeFromMeta(container.getMetadata()).getMaxIO();
  }

  @Override
  public int getEnergyStored(@Nonnull ItemStack container) {
    if (CapBankType.getTypeFromMeta(container.getMetadata()).isCreative()) {
      return CapBankType.getTypeFromMeta(container.getMetadata()).getMaxEnergyStored() / 2;
    }
    return IInternalPoweredItem.super.getEnergyStored(container);
  }

  @Override
  public void setEnergyStored(@Nonnull ItemStack container, int energy) {
    if (CapBankType.getTypeFromMeta(container.getMetadata()).isCreative()) {
      energy = CapBankType.getTypeFromMeta(container.getMetadata()).getMaxEnergyStored() / 2;
    }
    IInternalPoweredItem.super.setEnergyStored(container, energy);
  }

}
