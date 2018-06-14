package crazypants.enderio.machines.machine.wired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.common.NBTAction;

import crazypants.enderio.base.capacitor.ICapacitorKey;
import crazypants.enderio.base.machine.baselegacy.AbstractPowerConsumerEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.power.ILegacyPowerReceiver;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_WIRED_POWER_BUFFER;
import static crazypants.enderio.machines.capacitor.CapacitorKey.ENHANCED_WIRED_POWER_INTAKE;

@Storable
public class TileWiredCharger extends AbstractPowerConsumerEntity implements ILegacyPowerReceiver, IPaintable.IPaintableTileEntity, IProgressTile {

  protected TileWiredCharger(@Nonnull SlotDefinition slotDefinition, @Nonnull ICapacitorKey maxEnergyRecieved, @Nonnull ICapacitorKey maxEnergyStored) {
    super(slotDefinition, maxEnergyRecieved, maxEnergyStored, crazypants.enderio.base.capacitor.CapacitorKey.NO_POWER);
  }

  public TileWiredCharger() {
    this(new SlotDefinition(1, 1, 1), CapacitorKey.WIRED_POWER_INTAKE, CapacitorKey.WIRED_POWER_BUFFER);
  }

  public static class Enhanced extends TileWiredCharger {

    public Enhanced() {
      super(new SlotDefinition(1, 1, 1), ENHANCED_WIRED_POWER_INTAKE, ENHANCED_WIRED_POWER_BUFFER);
    }

    @Override
    public boolean supportsMode(@Nullable EnumFacing faceHit, @Nullable IoMode mode) {
      return (faceHit != EnumFacing.UP || mode == IoMode.NONE) && super.supportsMode(faceHit, mode);
    }

  }

  @Override
  public boolean isMachineItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
    if (i == getSlotDefinition().minInputSlot) {
      IEnergyStorage storage = PowerHandlerUtil.getCapability(itemstack, null);
      return storage != null && storage.receiveEnergy(1, true) > 0;
    }
    return false;
  }

  @Override
  public int getInventoryStackLimit() {
    return 1;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    if (!redstoneCheck || getEnergyStored() <= 0) {
      return false;
    }

    ItemStack stack = getStackInSlot(getSlotDefinition().minInputSlot);
    if (Prep.isValid(stack)) {
      int available = Math.min(getPowerUsePerTick(), getEnergyStored());
      if (available > 0) {
        IEnergyStorage chargable = PowerHandlerUtil.getCapability(stack, null);
        if (chargable != null) {
          int used = chargable.receiveEnergy(available, false);
          if (used > 0) {
            usePower(used);
            progress = chargable.getEnergyStored() / (float) chargable.getMaxEnergyStored();
            return false;
          }
        }
        // not charged
        if (Prep.isInvalid(getStackInSlot(getSlotDefinition().minOutputSlot))) {
          setInventorySlotContents(getSlotDefinition().minOutputSlot, stack);
          setInventorySlotContents(getSlotDefinition().minInputSlot, Prep.getEmpty());
        }
      }
    }

    progress = 0f;
    return false;
  }

  @Store(NBTAction.CLIENT)
  private @Nonnull ItemStack itemForClient = Prep.getEmpty();

  @SideOnly(Side.CLIENT)
  protected @Nonnull ItemStack getItemToRender() {
    return itemForClient;
  }

  @Override
  public void setInventorySlotContents(int slot, @Nonnull ItemStack contents) {
    super.setInventorySlotContents(slot, contents);
    if (slot == getSlotDefinition().minInputSlot) {
      // TESR renders this, need to keep clients updated
      itemForClient = contents;
      forceUpdatePlayers();
    }
  }

  private float progress = 0f;

  @Override
  public float getProgress() {
    return progress;
  }

  @Override
  public void setProgress(float progress) {
    this.progress = progress;
  }

  @Override
  @Nonnull
  public TileEntity getTileEntity() {
    return this;
  }

  @Override
  public int getPowerUsePerTick() {
    ItemStack stack = getStackInSlot(getSlotDefinition().maxInputSlot);
    if (!stack.isEmpty()) {
      IEnergyStorage chargable = PowerHandlerUtil.getCapability(stack, null);
      return chargable.getMaxEnergyStored() / CapacitorKey.WIRED_POWER_CHARGE.get(getCapacitorData());
    }
    return super.getPowerUsePerTick();
  }

}
