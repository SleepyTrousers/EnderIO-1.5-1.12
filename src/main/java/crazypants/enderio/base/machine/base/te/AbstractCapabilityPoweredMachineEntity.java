package crazypants.enderio.base.machine.base.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.InventorySlot;
import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.base.capability.Filters;
import crazypants.enderio.base.capacitor.ICapacitorData;
import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.network.PacketPowerStorage;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.power.EnergyTank;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.relauncher.Side;

@Storable
public abstract class AbstractCapabilityPoweredMachineEntity extends AbstractCapabilityMachineEntity {

  static {
    PacketHandler.INSTANCE.registerMessage(PacketPowerStorage.Handler.class, PacketPowerStorage.class, PacketHandler.nextID(), Side.CLIENT);
  }

  protected static final @Nonnull String CAPSLOT = "cap";

  @Store
  private final @Nonnull EnergyTank energy;
  protected float lastSyncPowerStored = -1;

  protected AbstractCapabilityPoweredMachineEntity(@Nonnull IModObject modObject) {
    this(null, modObject, null, null, null);
  }

  protected AbstractCapabilityPoweredMachineEntity(@Nonnull IModObject modObject, @Nonnull ICapacitorKey maxEnergyRecieved,
      @Nonnull ICapacitorKey maxEnergyStored,
      @Nonnull ICapacitorKey maxEnergyUsed) {
    this(null, modObject, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
  }

  protected AbstractCapabilityPoweredMachineEntity(@Nullable EnderInventory subclassInventory, @Nonnull IModObject modObject) {
    this(subclassInventory, modObject, null, null, null);
  }

  private AbstractCapabilityPoweredMachineEntity(@Nullable EnderInventory subclassInventory, @Nonnull IModObject modObject,
      @Nullable ICapacitorKey maxEnergyRecieved, @Nullable ICapacitorKey maxEnergyStored, @Nullable ICapacitorKey maxEnergyUsed) {
    super(subclassInventory);
    getInventory().add(Type.UPGRADE, CAPSLOT, new InventorySlot(Filters.CAPACITORS, null, 1));
    energy = new EnergyTank(this, modObject, maxEnergyRecieved, maxEnergyStored, maxEnergyUsed);
    getEnergy().updateCapacitorFromSlot(getInventory().getSlot(CAPSLOT));
  }

  //----- Common Machine Functions

  @Override
  public void doUpdate() {
    super.doUpdate();
    if (!world.isRemote) {
      if ((lastSyncPowerStored != getEnergy().getEnergyStored() && shouldDoWorkThisTick(10))) {
        lastSyncPowerStored = getEnergy().getEnergyStored();
        PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
      }
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

  @Override
  public void markDirty() {
    super.markDirty();
    updateCapacitorFromSlot();
  }

  protected void onCapacitorDataChange() {
  };

  private void updateCapacitorFromSlot() {
    if (getEnergy().updateCapacitorFromSlot(getInventory().getSlot(CAPSLOT))) {
      forceClientUpdate.set();
      onCapacitorDataChange();
    }
  }

  //--------- NBT

  @Override
  protected void onAfterNbtRead() {
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
      return (T) energy.get(facingIn);
    }
    return super.getCapability(capability, facingIn);
  }

}
