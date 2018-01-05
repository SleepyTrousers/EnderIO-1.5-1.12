package crazypants.enderio.base.power.tesla;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.power.IInternalPoweredItem;
import crazypants.enderio.base.power.ItemPowerCapabilityProvider;
import crazypants.enderio.util.NbtValue;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;

public class InternalPoweredItemWrapper implements ITeslaConsumer, ITeslaHolder, ITeslaProducer {

  public static class PoweredItemCapabilityProvider implements ItemPowerCapabilityProvider {

    @Override
    public boolean hasCapability(@Nonnull ItemStack stack, Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_HOLDER
          || capability == TeslaCapabilities.CAPABILITY_PRODUCER;
    }

    @Override
    public <T> T getCapability(@Nonnull ItemStack stack, Capability<T> capability, @Nullable EnumFacing facing) {
      if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
        return TeslaCapabilities.CAPABILITY_CONSUMER.cast(new InternalPoweredItemWrapper(stack));
      } else if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
        return TeslaCapabilities.CAPABILITY_HOLDER.cast(new InternalPoweredItemWrapper(stack));
      } else if (capability == TeslaCapabilities.CAPABILITY_PRODUCER) {
        InternalPoweredItemWrapper res = new InternalPoweredItemWrapper(stack);
        if (res.canExtract()) {
          return TeslaCapabilities.CAPABILITY_PRODUCER.cast(res);
        }
      }
      return null;
    }

  }

  protected final @Nonnull ItemStack container;
  protected int capacity;
  protected int maxInput;
  protected int maxOutput;

  public InternalPoweredItemWrapper(@Nonnull ItemStack stack) {
    this(stack, ((IInternalPoweredItem) stack.getItem()).getMaxEnergyStored(stack), ((IInternalPoweredItem) stack.getItem()).getMaxInput(stack),
        ((IInternalPoweredItem) stack.getItem()).getMaxOutput(stack));
  }

  public InternalPoweredItemWrapper(@Nonnull ItemStack container, int capacity, int maxInput, int maxOutput) {
    this.container = container;
    this.capacity = capacity;
    this.maxInput = maxInput;
    this.maxOutput = maxOutput;
  }

  public void setEnergyStored(int energy) {
    NbtValue.ENERGY.setInt(container, MathHelper.clamp(energy, 0, capacity));
  }

  @Override
  public long getStoredPower() {
    return NbtValue.ENERGY.getInt(container);
  }

  @Override
  public long getCapacity() {
    return capacity;
  }

  @Override
  public long givePower(long maxReceive, boolean simulated) {
    if (!canReceive()) {
      return 0;
    }
    int energy = getEnergyStored();
    int energyReceived = Math.min(capacity - energy, Math.min(maxInput, (int) maxReceive));
    if (!simulated) {
      energy += energyReceived;
      setEnergyStored(energy);
    }
    return energyReceived;
  }

  @Override
  public long takePower(long power, boolean simulated) {
    if (!canExtract()) {
      return 0;
    }
    int energy = getEnergyStored();
    int energyExtracted = Math.min(energy, Math.min(maxOutput, (int) power));
    if (!simulated) {
      energy -= energyExtracted;
      setEnergyStored(energy);
    }
    return energyExtracted;
  }

  public int getEnergyStored() {
    return NbtValue.ENERGY.getInt(container);
  }

  public boolean canExtract() {
    return maxOutput > 0;
  }

  public boolean canReceive() {
    return maxInput > 0;
  }

}