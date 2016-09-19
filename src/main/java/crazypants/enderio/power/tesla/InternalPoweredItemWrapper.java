package crazypants.enderio.power.tesla;

import javax.annotation.Nullable;

import crazypants.enderio.power.IInternalPoweredItem;
import crazypants.util.NbtValue;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class InternalPoweredItemWrapper implements ITeslaConsumer, ITeslaHolder, ITeslaProducer {

  public static class PoweredItemCapabilityProvider implements ICapabilityProvider {

    private final IInternalPoweredItem item;
    private final ItemStack stack;

    public PoweredItemCapabilityProvider(IInternalPoweredItem item, ItemStack stack) {
      this.item = item;
      this.stack = stack;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
      return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_HOLDER
          || capability == TeslaCapabilities.CAPABILITY_PRODUCER;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
      if (capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_HOLDER) {
        return (T) new InternalPoweredItemWrapper(stack, item);
      } else if (capability == TeslaCapabilities.CAPABILITY_PRODUCER) {
        InternalPoweredItemWrapper res = new InternalPoweredItemWrapper(stack, item);
        if (res.canExtract()) {
          return (T) res;
        }
      }
      return null;
    }

  }

  protected final ItemStack container;
  protected int capacity;
  protected int maxInput;
  protected int maxOutput;

  public InternalPoweredItemWrapper(ItemStack stack, IInternalPoweredItem item) {
    this(stack, item.getMaxEnergyStored(stack), item.getMaxInput(stack), item.getMaxOutput(stack));
  }

  public InternalPoweredItemWrapper(ItemStack container, int capacity, int maxInput, int maxOutput) {
    this.container = container;
    this.capacity = capacity;
    this.maxInput = maxInput;
    this.maxOutput = maxOutput;
  }

  public void setEnergyStored(int energy) {
    NbtValue.ENERGY.setInt(container, MathHelper.clamp_int(energy, 0, capacity));
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