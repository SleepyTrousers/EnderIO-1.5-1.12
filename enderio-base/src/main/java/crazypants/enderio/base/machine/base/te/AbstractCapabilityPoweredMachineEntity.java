package crazypants.enderio.base.machine.base.te;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.NBTAction;
import com.enderio.core.common.inventory.Callback;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.base.capability.Filters;
import crazypants.enderio.base.capacitor.ICapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.base.network.PacketPowerStorage;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.power.EnergyTank;
import crazypants.enderio.util.NbtValue;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

@Storable
public abstract class AbstractCapabilityPoweredMachineEntity extends AbstractCapabilityMachineEntity {

  public static final @Nonnull String CAPSLOT = "cap";

  public final @Nonnull Callback<ItemStack> CAP_CALLBACK = new Callback<ItemStack>() {
    @Override
    public final void onChange(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack) {
      updateCapacitorFromSlot();
    }
  };

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  // Not NBTAction.ITEM to keep the storedEnergy tag out in the open
  private final @Nonnull EnergyTank energy;
  protected float lastSyncPowerStored = -1;

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  protected boolean isCapacitorDamageable = false;

  protected @Nonnull Random random = new Random();

  protected AbstractCapabilityPoweredMachineEntity(@Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    this(null, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractCapabilityPoweredMachineEntity(@Nullable EnderInventory subclassInventory, @Nonnull ICapacitorKey maxEnergyRecieved,
      @Nonnull ICapacitorKey maxEnergyStored, @Nonnull ICapacitorKey maxEnergyUsed) {
    super(subclassInventory);
    getInventory().add(Type.UPGRADE, CAPSLOT, new InventorySlot(Filters.CAPACITORS, null, CAP_CALLBACK, 1));
    energy = new EnergyTank(this, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    updateCapacitorFromSlot();
  }

  // ----- Common Machine Functions

  @Override
  public void doUpdate() {
    super.doUpdate();
    if (!world.isRemote) {
      energy.loseEnergy();
      final int scaledPower = scaledPower();
      if ((lastSyncPowerStored != scaledPower && (lastSyncPowerStored == 0 || scaledPower == 0 || shouldDoWorkThisTick(20)))) {
        lastSyncPowerStored = scaledPower;
        PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
      }
    }
  }

  protected int scaledPower() {
    if (getEnergy().getEnergyStored() == 0) {
      return 0;
    } else {
      return 1 + getEnergy().getEnergyStored() / 1000;
    }
  }

  public boolean displayPower() {
    return true;
  }

  public boolean hasPower() {
    return getEnergy().getEnergyStored() > 0;
  }

  public @Nonnull ICapacitorData getCapacitorData() {
    return getEnergy().getCapacitorData();
  }

  public @Nonnull EnergyTank getEnergy() {
    return energy;
  }

  public int getEnergyStoredScaled(int scale) {
    final int maxEnergyStored2 = getEnergy().getMaxEnergyStored();
    return maxEnergyStored2 == 0 ? 0 : VecmathUtil.clamp(Math.round(scale * ((float) getEnergy().getEnergyStored() / maxEnergyStored2)), 0, scale);
  }

  protected void onCapacitorDataChange() {
  };

  private void updateCapacitorFromSlot() {
    if (getEnergy().updateCapacitorFromSlot(getInventory().getSlot(CAPSLOT))) {
      isCapacitorDamageable = getInventory().getSlot(CAPSLOT).get().isItemStackDamageable();
      onCapacitorDataChange();
    }
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    damageCapacitor();
    return true;
  }

  protected void damageCapacitor() {
    if (isCapacitorDamageable) {
      ItemStack cap = getInventory().getSlot(CAPSLOT).get();
      if (cap.attemptDamageItem(1, random, null)) {
        getInventory().getSlot(CAPSLOT).clear();
      }
    }
  }

  // --------- NBT

  @Override
  public void readCustomNBT(@Nonnull ItemStack stack) {
    super.readCustomNBT(stack);
    energy.setEnergyStored(NbtValue.ENERGY.getInt(stack));
  }

  @Override
  public void writeCustomNBT(@Nonnull ItemStack stack) {
    super.writeCustomNBT(stack);
    NbtValue.ENERGY.setInt(stack, energy.getEnergyStored());
    NbtValue.ENERGY_BUFFER.setInt(stack, energy.getMaxEnergyStored());
  }

  @Override
  protected void onAfterNbtRead() {
    super.onAfterNbtRead();
    updateCapacitorFromSlot();
  }

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return facingIn == null || getIoMode(facingIn).canInputOrOutput();
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityEnergy.ENERGY) {
      return (T) getEnergy().get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
