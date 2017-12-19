package crazypants.enderio.machines.machine.teleport.telepad;

import static crazypants.enderio.base.init.ModObject.itemLocationPrintout;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.base.item.coordselector.TelepadTarget.TelepadTargetArrayListHandler;
import crazypants.enderio.base.machine.baselegacy.PacketLegacyPowerStorage;
import crazypants.enderio.base.power.ILegacyPowerReceiver;
import crazypants.enderio.machines.machine.teleport.telepad.packet.PacketTargetList;
import crazypants.enderio.machines.network.PacketHandler;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.minecraft.HandleItemStack.HandleItemStackNNList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TileDialingDevice extends TileEntityEio implements ILegacyPowerReceiver, IItemHandlerModifiable {

  private static final int RF_PER_TICK = 20;

  @Store
  private int storedEnergyRF;

  private int lastSyncPowerStored;

  @Store(handler = HandleItemStackNNList.class)
  protected NNList<ItemStack> inventory = new NNList<>(2, ItemStack.EMPTY);

  @Store
  private DialerFacing facing;

  @Store(handler = TelepadTargetArrayListHandler.class)
  private final ArrayList<TelepadTarget> targets = new ArrayList<TelepadTarget>();

  @Override
  public void doUpdate() {
    if (world.isRemote) {
      super.doUpdate(); // disable ticking on the client
      return;
    }

    if (!inventory.get(0).isEmpty() && !inventory.get(1).isEmpty()) {
      ItemStack stack = inventory.get(0);
      TelepadTarget newTarg = TelepadTarget.readFromNBT(stack);
      if (newTarg != null && !targets.contains(newTarg)) {
        addTarget(newTarg);
        PacketHandler.sendToAllAround(new PacketTargetList(this, newTarg, true), this);
      }
      inventory.set(0, ItemStack.EMPTY);
      inventory.set(1, stack);
      markDirty();
    }

    if (getEnergyStored() <= 0) {
      return;
    }
    setEnergyStored(getEnergyStored() - getUsage());

    boolean powerChanged = (lastSyncPowerStored != getEnergyStored() && shouldDoWorkThisTick(5));
    if (powerChanged) {
      lastSyncPowerStored = getEnergyStored();
      PacketHandler.sendToAllAround(new PacketLegacyPowerStorage(this), this);
    }

  }

  public void addTarget(TelepadTarget newTarg) {
    if (newTarg == null) {
      return;
    }
    targets.add(newTarg);
    markDirty();
  }

  public void removeTarget(TelepadTarget target) {
    if (target == null) {
      return;
    }
    targets.remove(target);
    markDirty();
  }

  public ArrayList<TelepadTarget> getTargets() {
    return targets;
  }

  public void setTargets(Collection<TelepadTarget> t) {
    targets.clear();
    if (t != null) {
      targets.addAll(t);
    }
  }

  // ---------------------- Inventory -------------------------------------

  @Override
  public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return true;
    }
    return super.hasCapability(capability, facingIn);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return (T) this;
    }
    return super.getCapability(capability, facingIn);
  }

  @Override
  public int getSlots() {
    return 2;
  }

  @Override
  public @Nonnull ItemStack getStackInSlot(int slot) {
    if (slot < 0 || slot >= inventory.size()) {
      return ItemStack.EMPTY;
    }
    return inventory.get(slot);
  }

  @Override
  public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
    if (slot < 0 || slot >= inventory.size()) {
      return;
    }
    inventory.set(slot, stack);
  }

  @Override
  public @Nonnull ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
    if (slot != 0 || !inventory.get(0).isEmpty() || stack.isEmpty() || stack.getItem() != itemLocationPrintout.getItem()) {
      return stack;
    }
    if (!simulate) {
      inventory.set(0, stack.copy());
      markDirty();
    }
    return ItemStack.EMPTY;
  }

  @Override
  public @Nonnull ItemStack extractItem(int slot, int amount, boolean simulate) {
    if (slot != 1 || amount < 1 || inventory.get(1).isEmpty()) {
      return ItemStack.EMPTY;
    }
    ItemStack res = inventory.get(1).copy();
    if (!simulate) {
      markDirty();
      inventory.set(1, ItemStack.EMPTY);
    }
    return res;
  }

  // ---------------------- Power -------------------------------------

  @Override
  public int getEnergyStored() {
    return storedEnergyRF;
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return RF_PER_TICK * 40;
  }

  @Override
  public int getMaxEnergyStored() {
    return RF_PER_TICK * 20 * 60 * 4;
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  @Override
  public void setEnergyStored(int storedEnergy) {
    storedEnergyRF = MathHelper.clamp(storedEnergy, 0, getMaxEnergyStored());
  }

  @Override
  public boolean canConnectEnergy(@Nonnull EnumFacing from) {
    return true;
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    int max = Math.max(0, Math.min(Math.min(getMaxEnergyRecieved(from), maxReceive), getMaxEnergyStored() - getEnergyStored()));
    if (!simulate) {
      setEnergyStored(getEnergyStored() + max);
    }
    return max;
  }

  public int getUsage() {
    return RF_PER_TICK;
  }

  @Override
  public @Nonnull BlockPos getLocation() {
    return getPos();
  }

  public @Nonnull DialerFacing getFacing() {
    return facing != null ? facing : DialerFacing.DOWN_TONORTH;
  }

  public void setFacing(DialerFacing facing) {
    this.facing = facing;
    markDirty();
  }

  public int getPowerScaled(int scale) {
    return (int) ((((float) getEnergyStored()) / (getMaxEnergyStored())) * scale);
  }

  @Override
  public int getSlotLimit(int slot) {
    return 64;
  }
}
