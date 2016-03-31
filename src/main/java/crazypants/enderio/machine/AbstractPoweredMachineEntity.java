package crazypants.enderio.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.capacitor.ICapacitorData;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.ICapacitor;
import crazypants.enderio.power.IInternalPoweredTile;
import crazypants.enderio.power.PowerHandlerUtil;

public abstract class AbstractPoweredMachineEntity extends AbstractMachineEntity implements IInternalPoweredTile {

  // Power
  private ICapacitorData capacitorData = DefaultCapacitorData.BASIC_CAPACITOR; // WIP
  @Deprecated
  private Capacitors capacitorType;
  @Deprecated
  private ICapacitor capacitor;

  private int storedEnergyRF;
  protected float lastSyncPowerStored = -1;

  protected AbstractPoweredMachineEntity(SlotDefinition slotDefinition) {
    super(slotDefinition);
    capacitorType = Capacitors.BASIC_CAPACITOR;
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
    if(isSideDisabled(dir)) {
      return 0;
    }
    return getCapacitor().getMaxEnergyReceived();
  }

  @Override
  public int getMaxEnergyStored() {
    return getCapacitor().getMaxEnergyStored();
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

  @Deprecated
  public Capacitors getCapacitorType() {
    return capacitorType;
  }

  @Deprecated
  public ICapacitor getCapacitor() {
    return capacitor != null ? capacitor : capacitorType.capacitor;
  }

  public int getEnergyStoredScaled(int scale) {
    // NB: called on the client so can't use the power provider
    return VecmathUtil.clamp(Math.round(scale * ((float) storedEnergyRF / getMaxEnergyStored())), 0, scale);
  }

  @Deprecated
  protected void setCapacitor(ICapacitor capacitor) {
    this.capacitor = capacitor;
    //Force a check that the new value is in bounds
    setEnergyStored(getEnergyStored());
  }

  @Deprecated
  public void setCapacitor(Capacitors capacitorType) {
    this.capacitorType = capacitorType;
    this.capacitor = null;
    onCapacitorTypeChange();
    //Force a check that the new value is in bounds
    setEnergyStored(getEnergyStored());
    forceClientUpdate = true;
  }

  public void onCapacitorTypeChange() {}

  public int getPowerUsePerTick() {
    return getCapacitor().getMaxEnergyExtracted();
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
      setCapacitor(Capacitors.BASIC_CAPACITOR);
      return;
    }
    ItemStack contents = inventory[slotDefinition.minUpgradeSlot];
    if(contents == null || contents.getItem() != EnderIO.itemBasicCapacitor) {
      setCapacitor(Capacitors.BASIC_CAPACITOR);
    } else {
      setCapacitor(Capacitors.values()[contents.getItemDamage()]);
    }
  }

  //--------- NBT

  /**
   * Read state common to both block and item
   */
  @Override
  public void readCommon(NBTTagCompound nbtRoot) {
    super.readCommon(nbtRoot);
    setCapacitor(Capacitors.values()[nbtRoot.getShort("capacitorType")]);
    setEnergyStored(nbtRoot.getInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY));
  }

  /**
   * Write state common to both block and item
   */
  @Override
  public void writeCommon(NBTTagCompound nbtRoot) {
    super.writeCommon(nbtRoot);
    nbtRoot.setInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY, storedEnergyRF);
    nbtRoot.setShort("capacitorType", (short) capacitorType.ordinal());
  }

  @Override
  public void readFromItemStack(ItemStack stack) {
    if (stack == null || stack.getTagCompound() == null) {
      return;
    }
    NBTTagCompound nbtRoot = stack.getTagCompound();
    setEnergyStored(nbtRoot.getInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY));
    super.readFromItemStack(stack);
  }

}
