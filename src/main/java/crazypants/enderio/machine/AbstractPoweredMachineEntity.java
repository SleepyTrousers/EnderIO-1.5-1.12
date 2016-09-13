package crazypants.enderio.machine;

import javax.annotation.Nullable;

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
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.annotations.Store.StoreFor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

@Storable
public abstract class AbstractPoweredMachineEntity extends AbstractMachineEntity implements IInternalPoweredTile {

  // Power
  protected ICapacitorData capacitorData = DefaultCapacitorData.BASIC_CAPACITOR;
  protected final ICapacitorKey maxEnergyRecieved, maxEnergyStored, maxEnergyUsed;

  @Store({ StoreFor.SAVE, StoreFor.CLIENT })
  // Not StoreFor.ITEM to keep the storedEnergy tag out in the open
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
    onCapacitorDataChange();
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
    //Force a check that the new value is in bounds
    setEnergyStored(getEnergyStored());
    forceClientUpdate.set();
  }

  public int getPowerUsePerTick() {
    return maxEnergyUsed == null ? 0 : maxEnergyUsed.get(capacitorData);
  }

  @Override
  public void setInventorySlotContents(int slot, @Nullable ItemStack contents) {
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
  }

  @Override
  public void readFromItemStack(ItemStack stack) {
    super.readFromItemStack(stack);
    if (stack != null) {
      NBTTagCompound root = stack.getTagCompound();
      if (root != null) {
        setEnergyStored(root.getInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY));
      }
    }
  }

  @Override
  public void writeToItemStack(ItemStack stack) {
    if (stack == null) {
      return;
    }
    super.writeToItemStack(stack);
    NBTTagCompound root = stack.getTagCompound();
    if (root == null) {
      stack.setTagCompound(root = new NBTTagCompound());
    }
    root.setInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY, storedEnergyRF);
  }

}
