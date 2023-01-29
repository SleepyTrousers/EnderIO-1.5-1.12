package crazypants.enderio.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.vecmath.VecmathUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.*;

public abstract class AbstractPoweredMachineEntity extends AbstractMachineEntity implements IInternalPoweredTile {

    // Power
    private Capacitors capacitorType;
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
        if (powerChanged) {
            lastSyncPowerStored = storedEnergyRF;
            PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
        }
    }

    // RF API Power

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return !isSideDisabled(from.ordinal());
    }

    @Override
    public int getMaxEnergyRecieved(ForgeDirection dir) {
        if (isSideDisabled(dir.ordinal())) {
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

    // ----- Common Machine Functions

    @Override
    public boolean displayPower() {
        return true;
    }

    public boolean hasPower() {
        return storedEnergyRF > 0;
    }

    public Capacitors getCapacitorType() {
        return capacitorType;
    }

    public ICapacitor getCapacitor() {

        if (capacitor != null) {
            return capacitor;
        } else if (capacitorType == Capacitors.TOTEMIC_CAPACITOR) {
            ItemStack contents = inventory[slotDefinition.minUpgradeSlot];
            if (contents != null && contents.getItem() == EnderIO.itemBasicCapacitor) {
                return EnderIO.itemBasicCapacitor.getCapacitor(contents);
            }
        }

        return capacitorType.capacitor;
    }

    public int getEnergyStoredScaled(int scale) {
        // NB: called on the client so can't use the power provider
        return VecmathUtil.clamp(Math.round(scale * ((float) storedEnergyRF / getMaxEnergyStored())), 0, scale);
    }

    protected void setCapacitor(ICapacitor capacitor) {
        this.capacitor = capacitor;
        // Force a check that the new value is in bounds
        setEnergyStored(getEnergyStored());
    }

    public void setCapacitor(Capacitors capacitorType) {
        this.capacitorType = capacitorType;

        if (capacitorType == Capacitors.TOTEMIC_CAPACITOR) {
            ItemStack contents = inventory[slotDefinition.minUpgradeSlot];
            if (contents != null && contents.getItem() == EnderIO.itemBasicCapacitor) {
                setCapacitor(EnderIO.itemBasicCapacitor.getCapacitor(contents));
            }
        } else {
            this.capacitor = capacitorType.capacitor;
        }

        onCapacitorTypeChange();
        // Force a check that the new value is in bounds
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
        if (slotDefinition.isUpgradeSlot(slot)) {
            updateCapacitorFromSlot();
        }
    }

    @Override
    public ItemStack decrStackSize(int fromSlot, int amount) {
        ItemStack res = super.decrStackSize(fromSlot, amount);
        if (slotDefinition.isUpgradeSlot(fromSlot)) {
            updateCapacitorFromSlot();
        }
        return res;
    }

    private void updateCapacitorFromSlot() {
        if (slotDefinition.getNumUpgradeSlots() <= 0) {
            setCapacitor(Capacitors.BASIC_CAPACITOR);
            return;
        }
        ItemStack contents = inventory[slotDefinition.minUpgradeSlot];
        if (contents == null || contents.getItem() != EnderIO.itemBasicCapacitor) {
            setCapacitor(Capacitors.BASIC_CAPACITOR);
        } else {
            setCapacitor(Capacitors.values()[contents.getItemDamage()]);
        }
    }

    // --------- NBT

    /**
     * Read state common to both block and item
     */
    @Override
    public void readCommon(NBTTagCompound nbtRoot) {
        super.readCommon(nbtRoot);
        setCapacitor(Capacitors.values()[nbtRoot.getShort("capacitorType")]);
        int energy;
        if (nbtRoot.hasKey("storedEnergy")) {
            float storedEnergyMJ = nbtRoot.getFloat("storedEnergy");
            energy = (int) (storedEnergyMJ * 10);
        } else {
            energy = nbtRoot.getInteger(PowerHandlerUtil.STORED_ENERGY_NBT_KEY);
        }
        setEnergyStored(energy);
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
}
