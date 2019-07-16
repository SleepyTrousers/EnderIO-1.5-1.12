package crazypants.enderio.invpanel.chest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.InventorySlot;

import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityPoweredMachineEntity;
import crazypants.enderio.base.machine.base.te.EnergyLogic;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.invpanel.capacitor.CapacitorKey;
import crazypants.enderio.util.Prep;
import info.loenwind.autosave.annotations.Storable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

@Storable
public abstract class TileInventoryChest extends AbstractCapabilityPoweredMachineEntity implements IPaintable.IPaintableTileEntity {

  @Storable
  public static class Tiny extends TileInventoryChest {
    public Tiny() {
      super(EnumChestSize.TINY);
    }
  }

  @Storable
  public static class Small extends TileInventoryChest {
    public Small() {
      super(EnumChestSize.SMALL);
    }
  }

  @Storable
  public static class Medium extends TileInventoryChest {
    public Medium() {
      super(EnumChestSize.MEDIUM);
    }
  }

  @Storable
  public static class Big extends TileInventoryChest {
    public Big() {
      super(EnumChestSize.BIG);
    }
  }

  @Storable
  public static class Large extends TileInventoryChest {
    public Large() {
      super(EnumChestSize.LARGE);
    }
  }

  @Storable
  public static class Huge extends TileInventoryChest {
    public Huge() {
      super(EnumChestSize.HUGE);
    }
  }

  @Storable
  public static class Enormous extends TileInventoryChest {
    public Enormous() {
      super(EnumChestSize.ENORMOUS);
    }
  }

  @Storable
  public static class Warehouse extends TileInventoryChest {
    public Warehouse() {
      super(EnumChestSize.WAREHOUSE);
    }
  }

  @Storable
  public static class Warehouse13 extends TileInventoryChest {
    public Warehouse13() {
      super(EnumChestSize.WAREHOUSE13);
    }
  }

  private final EnumChestSize size;

  private TileInventoryChest(@Nonnull EnumChestSize size) {
    super(null, CapacitorKey.INV_CHEST_ENERGY_INTAKE, CapacitorKey.INV_CHEST_ENERGY_BUFFER, CapacitorKey.INV_CHEST_ENERGY_USE);
    this.size = size;
    for (int i = 0; i < size.getSlots(); i++) {
      getInventory().add(EnderInventory.Type.INOUT, "slot" + i, new InventorySlot());
    }
    getInventory().getSlot(EnergyLogic.CAPSLOT).set(new ItemStack(ModObject.itemBasicCapacitor.getItemNN(), 1, DefaultCapacitorData.ENDER_CAPACITOR.ordinal()));
  }

  @Override
  public boolean isActive() {
    return hasPower();
  }

  private boolean lastState = false;

  @Override
  protected boolean processTasks(boolean redstoneCheck) {
    getEnergy().useEnergy();
    if (lastState != hasPower()) {
      lastState = hasPower();
      return true;
    }
    return false;
  }

  public int getComparatorInputOverride() {
    if (size == null) {
      return 0;
    }
    int count = 0;
    for (InventorySlot slot : getInventory().getView(EnderInventory.Type.INOUT)) {
      if (Prep.isValid(slot.getStackInSlot(0))) {
        count++;
      }
    }
    return count == 0 ? 0 : (14 * count / size.getSlots() + 1);
  }

  @Override
  public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facingIn) {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && !hasPower()) {
      return null;
    }
    return super.getCapability(capability, facingIn);
  }

}
