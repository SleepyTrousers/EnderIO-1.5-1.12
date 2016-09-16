package crazypants.enderio.teleport.telepad;

import java.util.ArrayList;
import java.util.Collection;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.machine.PacketPowerStorage;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.teleport.telepad.TelepadTarget.TelepadTargetArrayListHandler;
import crazypants.enderio.teleport.telepad.packet.PacketTargetList;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public class TileDialingDevice extends TileEntityEio implements IInternalPowerReceiver, IItemHandlerModifiable{

  private static final int RF_PER_TICK = 20;
  
  @Store
  private int storedEnergyRF;
  
  private int lastSyncPowerStored;
  
  @Store
  protected ItemStack[] inventory = new ItemStack[2];

  @Store
  private DialerFacing facing;
  
  @Store(handler = TelepadTargetArrayListHandler.class)
  private final ArrayList<TelepadTarget> targets = new ArrayList<TelepadTarget>();
  
  @Override
  public void doUpdate() {
    super.doUpdate();
    
    if (worldObj.isRemote) {
      return;
    }
    
    if(inventory[0] != null && inventory[1] == null) {
      ItemStack stack = inventory[0];
      TelepadTarget newTarg = TelepadTarget.readFromNBT(stack);
      if(newTarg != null && !targets.contains(newTarg)) {
        addTarget(newTarg);
        PacketHandler.sendToAllAround(new PacketTargetList(this, newTarg, true), this);
      }
      inventory[0] = null;
      inventory[1] = stack;
      markDirty();
    }

    if(getEnergyStored() <= 0) {
      return;
    }
    setEnergyStored(getEnergyStored() - getUsage());
    

    boolean powerChanged = (lastSyncPowerStored != getEnergyStored() && shouldDoWorkThisTick(5));
    if (powerChanged) {
      lastSyncPowerStored = getEnergyStored();
      PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
    }
    
  }
  
  public void addTarget(TelepadTarget newTarg) {
    if(newTarg == null) {
      return;
    }
    targets.add(newTarg);
    markDirty();
  }
  
  public void removeTarget(TelepadTarget target) {
    if(target == null) {
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
    if(t != null) {
      targets.addAll(t);
    }
  }
  
  
  //---------------------- Inventory -------------------------------------
  
  @Override
  public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return true;
    }
    return super.hasCapability(capability, facing);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
      return (T) this;
    }
    return super.getCapability(capability, facing);
  }

  @Override
  public int getSlots() {
    return 2;
  }

  @Override
  public ItemStack getStackInSlot(int slot) {
    if(slot < 0 || slot >= inventory.length) {
      return null;
    }
    return inventory[slot];
  }
  
  @Override
  public void setStackInSlot(int slot, ItemStack stack) {
    if(slot < 0 || slot >= inventory.length) {
      return;
    }
    inventory[slot] = stack;
  }

  @Override
  public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
    if(slot != 0 || inventory[0] != null || stack == null || stack.getItem() != EnderIO.itemlocationPrintout) {
      return stack;
    }
    if(!simulate) {
      inventory[0] = stack.copy();
      markDirty();
    }
    return null;
  }

  @Override
  public ItemStack extractItem(int slot, int amount, boolean simulate) {
    if(slot != 1 || amount < 1 || inventory[1] == null) {
      return null;
    }
    ItemStack res = inventory[1].copy();
    if(!simulate) {
      markDirty();
      inventory[1] = null;
    }
    return res;
  }
  
  //---------------------- Power -------------------------------------

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return RF_PER_TICK * 40;
  }

  @Override
  public int getMaxEnergyStored(EnumFacing from) {
    return RF_PER_TICK * 20 * 60 * 4;
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  @Override
  public int getEnergyStored(EnumFacing from) {
    return storedEnergyRF;
  }

  @Override
  public void setEnergyStored(int storedEnergy) {
    storedEnergyRF = MathHelper.clamp_int(storedEnergy, 0, getMaxEnergyStored(null));
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return true;
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    int max = Math.max(0, Math.min(Math.min(getMaxEnergyRecieved(from), maxReceive), getMaxEnergyStored(from) - getEnergyStored(null)));
    if (!simulate) {
      setEnergyStored(getEnergyStored() + max);
    }
    return max;
  }
  
  public int getUsage() {
    return RF_PER_TICK;
  }

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(pos);
  }

  public DialerFacing getFacing() {
    return facing == null ? DialerFacing.DOWN_TONORTH : facing;
  }
  
  public void setFacing(DialerFacing facing) {
    this.facing = facing;
    markDirty();
  }

  public int getPowerScaled(int scale) {
    return (int) ((((float) getEnergyStored()) / (getMaxEnergyStored(null))) * scale);
  }

  public int getEnergyStored() {
    return getEnergyStored(null);
  }

}
