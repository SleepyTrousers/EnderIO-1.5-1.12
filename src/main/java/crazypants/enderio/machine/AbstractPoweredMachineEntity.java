package crazypants.enderio.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.ModObject;
import crazypants.enderio.capacitor.CapacitorHelper;
import crazypants.enderio.capacitor.CapacitorKey;
import crazypants.enderio.capacitor.CapacitorKeyType;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.capacitor.DefaultCapacitorKey;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.capacitor.ICapacitorKey;
import crazypants.enderio.capacitor.Scaler;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.PowerHandlerUtil;

public abstract class AbstractPoweredMachineEntity extends AbstractMachineEntity implements IInternalPoweredTile {

  // Power
  private ICapacitorData capacitorData = DefaultCapacitorData.BASIC_CAPACITOR;
  private final ICapacitorKey maxEnergyRecieved, maxEnergyStored, maxEnergyUsed;

  private int storedEnergyRF;
  protected float lastSyncPowerStored = -1;

  @Deprecated
  protected AbstractPoweredMachineEntity(SlotDefinition slotDefinition) {
    this(slotDefinition, null);
  }

  protected AbstractPoweredMachineEntity(SlotDefinition slotDefinition, ModObject modObject) {
    super(slotDefinition);
    if (modObject == null) {
      this.maxEnergyRecieved = CapacitorKey.LEGACY_ENERGY_INTAKE;
      this.maxEnergyStored = CapacitorKey.LEGACY_ENERGY_BUFFER;
      this.maxEnergyUsed = CapacitorKey.LEGACY_ENERGY_USE;
    } else {
      this.maxEnergyRecieved = new DefaultCapacitorKey(modObject, CapacitorKeyType.ENERGY_INTAKE, Scaler.Factory.POWER, 80);
      this.maxEnergyStored = new DefaultCapacitorKey(modObject, CapacitorKeyType.ENERGY_BUFFER, Scaler.Factory.POWER, 100000);
      this.maxEnergyUsed = new DefaultCapacitorKey(modObject, CapacitorKeyType.ENERGY_USE, Scaler.Factory.POWER, 20);
    }
  }

  public AbstractPoweredMachineEntity(SlotDefinition slotDefinition, ICapacitorKey maxEnergyRecieved, ICapacitorKey maxEnergyStored, ICapacitorKey maxEnergyUsed) {
    super(slotDefinition);
    this.maxEnergyRecieved = maxEnergyRecieved;
    this.maxEnergyStored = maxEnergyStored;
    this.maxEnergyUsed = maxEnergyUsed;
  }

  @Override
  public void init() {
    super.init();
    onCapacitorTypeChange();
  }

  @Override
  public void doUpdate() {

    super.doUpdate();

    if (worldObj.isRemote) {
      return;
    }
    boolean powerChanged = (lastSyncPowerStored != storedEnergyRF && shouldDoWorkThisTick(5));
    if(powerChanged) {
      lastSyncPowerStored = storedEnergyRF;
      PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
    }
  }

  //RF API Power

  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return !isSideDisabled(from);
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    if (isSideDisabled(dir) || maxEnergyRecieved == null) {
      return 0;
    }
    return maxEnergyRecieved.get(capacitorData);
  }

  @Override
  public int getMaxEnergyStored() {
    return maxEnergyStored == null ? 0 : maxEnergyStored.get(capacitorData);
  }

  @Override
  public void setEnergyStored(int stored) {
    storedEnergyRF = MathHelper.clamp_int(stored, 0, getMaxEnergyStored());
  }

  @Override
  public int getEnergyStored() {
    return storedEnergyRF;
  }

  //----- Common Machine Functions

  @Override
  public boolean displayPower() {
    return true;
  }

  public boolean hasPower() {
    return storedEnergyRF > 0;
  }

  public ICapacitorData getCapacitorData() {
    return capacitorData;
  }

  public int getEnergyStoredScaled(int scale) {
    // NB: called on the client so can't use the power provider
    final int maxEnergyStored2 = getMaxEnergyStored();
    return maxEnergyStored2 == 0 ? 0 : VecmathUtil.clamp(Math.round(scale * ((float) storedEnergyRF / maxEnergyStored2)), 0, scale);
  }

  public void onCapacitorDataChange() {
    onCapacitorTypeChange();
    //Force a check that the new value is in bounds
    setEnergyStored(getEnergyStored());
    forceClientUpdate = true;
  }

  @Deprecated
  public void onCapacitorTypeChange() {}

  public int getPowerUsePerTick() {
    return maxEnergyUsed == null ? 0 : maxEnergyUsed.get(capacitorData);
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if(slotDefinition.isUpgradeSlot(slot)) {
      updateCapacitorFromSlot();
    }
  }

  @Override
  public ItemStack decrStackSize(int fromSlot, int amount) {
    ItemStack res = super.decrStackSize(fromSlot, amount);
    if(slotDefinition.isUpgradeSlot(fromSlot)) {
      updateCapacitorFromSlot();
    }
    return res;
  }

  private void updateCapacitorFromSlot() {
    if(slotDefinition.getNumUpgradeSlots() <= 0) {
      capacitorData = DefaultCapacitorData.BASIC_CAPACITOR;
    } else {
      capacitorData = CapacitorHelper.getCapacitorDataFromItemStack(inventory[slotDefinition.minUpgradeSlot]);
      if (capacitorData == null) {
        capacitorData = DefaultCapacitorData.BASIC_CAPACITOR;
      }
    }
    onCapacitorDataChange();
  }

  //--------- NBT

  /**
   * Read state common to both block and item
   */
  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    updateCapacitorFromSlot();
    setEnergyStored(nbtRoot.getInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY));
  }

  /**
   * Write state common to both block and item
   */
  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    nbtRoot.setInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY, storedEnergyRF);
  }

  @Override
  public void readFromItemStack(ItemStack stack) {
    if (stack == null || stack.getTagCompound() == null) {
      return;
    }
    super.readFromItemStack(stack);
    NBTTagCompound nbtRoot = stack.getTagCompound();
    setEnergyStored(nbtRoot.getInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY));
  }

}
