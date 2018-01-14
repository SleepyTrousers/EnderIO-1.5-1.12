package crazypants.enderio.base.power;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class AbstractPoweredItem extends Item implements IInternalPoweredItem {

  protected int maxEneryStored;
  protected int maxInput;
  protected int maxOutput;

  public AbstractPoweredItem(int maxEneryStored, int maxInput, int maxOutput) {
    this.maxEneryStored = maxEneryStored;
    this.maxInput = maxInput;
    this.maxOutput = maxOutput;
  }

  @Override
  public int getMaxEnergyStored(@Nonnull ItemStack container) {
    return maxEneryStored;
  }

  @Override
  public int getMaxInput(@Nonnull ItemStack container) {
    return maxInput;
  }

  @Override
  public int getMaxOutput(@Nonnull ItemStack container) {
    return maxOutput;
  }

  @Override
  public @Nonnull ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
    return IInternalPoweredItem.super.initCapabilities(stack, nbt);
  }

}
