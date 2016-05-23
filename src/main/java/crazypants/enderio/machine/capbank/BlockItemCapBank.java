package crazypants.enderio.machine.capbank;

import javax.annotation.Nonnull;

import com.enderio.core.common.transform.EnderCoreMethods.IOverlayRenderAware;

import cofh.api.energy.IEnergyContainerItem;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.PowerBarOverlayRenderHelper;
import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class BlockItemCapBank extends ItemBlock implements IEnergyContainerItem, IOverlayRenderAware {

  public static ItemStack createItemStackWithPower(int meta, int storedEnergy) {
    ItemStack res = new ItemStack(EnderIO.blockCapBank, 1, meta);
    if (storedEnergy > 0) {
      PowerHandlerUtil.setStoredEnergyForItem(res, storedEnergy);
    }
    return res;
  }

  public BlockItemCapBank(@Nonnull BlockCapBank blockCapBank, @Nonnull String name) {
    super(blockCapBank);
    setHasSubtypes(true);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setRegistryName(name);
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
  public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
    if (container.stackSize > 1) {
      return 0;
    }
    CapBankType type = CapBankType.getTypeFromMeta(container.getMetadata());
    int energy = getEnergyStored(container);
    int maxInput = type.getMaxIO();
    int energyReceived = Math.min(type.getMaxEnergyStored() - energy, Math.min(maxReceive, maxInput));

    if (!simulate && !type.isCreative()) {
      energy += energyReceived;
      PowerHandlerUtil.setStoredEnergyForItem(container, energy);
    }
    return energyReceived;

  }

  @Override
  public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
    if (container.stackSize > 1) {
      return 0;
    }
    CapBankType type = CapBankType.getTypeFromMeta(container.getMetadata());
    int energy = getEnergyStored(container);
    int maxOutput = type.getMaxIO();
    int energyExtracted = Math.min(energy, Math.min(maxExtract, maxOutput));

    if (!simulate && !type.isCreative()) {
      energy -= energyExtracted;
      PowerHandlerUtil.setStoredEnergyForItem(container, energy);
    }
    return energyExtracted;
  }

  @Override
  public int getEnergyStored(ItemStack container) {
    return CapBankType.getTypeFromMeta(container.getMetadata()).isCreative() ? CapBankType.getTypeFromMeta(container.getItemDamage()).getMaxEnergyStored() / 2
        : PowerHandlerUtil.getStoredEnergyForItem(container);
  }

  @Override
  public int getMaxEnergyStored(ItemStack container) {
    return CapBankType.getTypeFromMeta(container.getMetadata()).getMaxEnergyStored();
  }

  @Override
  public void renderItemOverlayIntoGUI(ItemStack stack, int xPosition, int yPosition) {
    if (Config.capacitorBankRenderPowerOverlayOnItem) {
      PowerBarOverlayRenderHelper.instance.render(stack, xPosition, yPosition);
    }
  }

  @Override
  public boolean hasEffect(@Nonnull ItemStack stack) {
    return CapBankType.getTypeFromMeta(stack.getMetadata()).isCreative() || super.hasEffect(stack);
  }

}
