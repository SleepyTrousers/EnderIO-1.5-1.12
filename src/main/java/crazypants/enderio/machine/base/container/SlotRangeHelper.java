package crazypants.enderio.machine.base.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;

import javax.annotation.Nonnull;

import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.SlotPredicate;
import com.enderio.core.common.inventory.EnderInventory.Type;

import crazypants.enderio.machine.base.container.SlotRangeHelper.IRangeProvider;
import crazypants.enderio.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.machine.baselegacy.SlotDefinition;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public abstract class SlotRangeHelper<T extends Container & IRangeProvider> {
  
  protected final T owner;
  
  protected SlotRangeHelper(T owner) {
    this.owner = owner;
  }

  protected final void addInventorySlotRange(@Nonnull List<SlotRange> res, IntFunction<Slot> slotProvider, int start, int end) {
    SlotRange range = null;
    for (int i = start; i < end; i++) {
      Slot slotFromInventory = slotProvider.apply(i);
      if (slotFromInventory != null) {
        int slotNumber = slotFromInventory.slotNumber;
        if (range == null) {
          range = new SlotRange(slotNumber, slotNumber + 1, false);
        } else if (range.getEnd() == slotNumber) {
          range = new SlotRange(range.getStart(), slotNumber + 1, false);
        } else {
          res.add(range);
          range = new SlotRange(slotNumber, slotNumber + 1, false);
        }
      }
    }
    if (range != null) {
      res.add(range);
    }
  }
  
  @Deprecated
  public static class Legacy<I extends AbstractInventoryMachineEntity> extends SlotRangeHelper<AbstractMachineContainer<I>> {
    public Legacy(AbstractMachineContainer<I> owner) {
      super(owner);
    }

    @Override
    public void addInputSlotRanges(@Nonnull List<SlotRange> res) {
      SlotDefinition slotDef = owner.getTe().getSlotDefinition();
      if (slotDef.getNumInputSlots() > 0) {
        addInventorySlotRange(res, i -> owner.getSlotFromInventory(owner.getInv(), i), slotDef.getMinInputSlot(), slotDef.getMaxInputSlot() + 1);
      }
    }

    @Override
    public void addUpgradeSlotRanges(@Nonnull List<SlotRange> res) {
      SlotDefinition slotDef = owner.getTe().getSlotDefinition();
      if (slotDef.getNumUpgradeSlots() > 0) {
        addInventorySlotRange(res, i -> owner.getSlotFromInventory(owner.getInv(), i), slotDef.getMinUpgradeSlot(), slotDef.getMaxUpgradeSlot() + 1);
      }
    }

    @Override
    public @Nonnull List<SlotRange> getTargetSlotsForTransfer(int slotNumber, @Nonnull Slot slot) {
      if (slot.inventory == owner.getInv()) {
        SlotDefinition slotDef = owner.getTe().getSlotDefinition();
        if (slotDef.isInputSlot(slot.getSlotIndex()) || slotDef.isUpgradeSlot(slot.getSlotIndex())) {
          return Collections.singletonList(owner.getPlayerInventorySlotRange(false));
        }
        if (slotDef.isOutputSlot(slot.getSlotIndex())) {
          return Collections.singletonList(owner.getPlayerInventorySlotRange(true));
        }
      } else if (slotNumber >= owner.getPlayerInventoryWithoutHotbarSlotRange().getStart()) {
        List<SlotRange> res = new ArrayList<SlotRange>();
        addInputSlotRanges(res);
        addUpgradeSlotRanges(res);
        addPlayerSlotRanges(res, slotNumber);
        return res;
      }
      return Collections.emptyList();
    }
  }
  
//  public static class Cap<I extends AbstractCapabilityMachineEntity> extends SlotRangeHelper<AbstractCapabilityMachineContainer<I>> {
//
//    protected Cap(AbstractCapabilityMachineContainer<I> owner) {
//      super(owner);
//    }
//    
//    @Override
//    public void addInputSlotRanges(@Nonnull List<SlotRange> res) {
//      IItemHandler inputSlots = owner.getItemHandler().getView(Type.INPUT);
//      if (inputSlots.getSlots() > 0) {
//        addInventorySlotRange(res, i -> getSlotFromHandler(inputSlots, i), 0, inputSlots.getSlots());
//      }
//    }
//
//    @Override
//    public void addUpgradeSlotRanges(@Nonnull List<SlotRange> res) {
//      IItemHandler upgradeSlots = owner.getItemHandler().getView(Type.UPGRADE);
//      if (upgradeSlots.getSlots() > 0) {
//        addInventorySlotRange(res, i -> getSlotFromHandler(upgradeSlots, i), 0, upgradeSlots.getSlots());
//      }
//    }
//
//    @Override
//    public @Nonnull List<SlotRange> getTargetSlotsForTransfer(int slotNumber, @Nonnull Slot slot) {
//      if (slotNumber < owner.getPlayerInventoryWithoutHotbarSlotRange().getStart()) {
//        if (!(slot instanceof SlotItemHandler)) {
//          throw new IllegalArgumentException("SlotRangeHandler.Cap cannot be used with IInventory slots!");
//        }
//        IItemHandler handler = ((SlotItemHandler)slot).getItemHandler();
//        EnderInventory inv = owner.getOwner().getInventory();
//        if (handler == inv.getView(Type.INPUT) || handler == inv.getView(Type.UPGRADE)) {
//          return Collections.singletonList(owner.getPlayerInventorySlotRange(false));
//        }
//        if (handler == inv.getView(Type.OUTPUT)) {
//          return Collections.singletonList(owner.getPlayerInventorySlotRange(true));
//        }
//      } else {
//        List<SlotRange> res = new ArrayList<SlotRange>();
//        addInputSlotRanges(res);
//        addUpgradeSlotRanges(res);
//        addPlayerSlotRanges(res, slotNumber);
//        return res;
//      }
//      return Collections.emptyList();
//    }
//    
//    private Slot getSlotFromHandler(IItemHandler handler, int slotIndex) {
//      for (Slot slot : owner.inventorySlots) {
//        if (slot instanceof SlotItemHandler && ((SlotItemHandler) slot).getItemHandler() == handler && slotIndex == slot.getSlotIndex()) {
//          return slot;
//        }
//      }
//
//      return null;
//    }
//  }

  public abstract void addInputSlotRanges(@Nonnull List<SlotRange> res);

  public abstract void addUpgradeSlotRanges(@Nonnull List<SlotRange> res);

  public void addPlayerSlotRanges(@Nonnull List<SlotRange> res, int slotIndex) {
    SlotRange hotbar = owner.getPlayerHotbarSlotRange();
    if (slotIndex < owner.getPlayerInventoryWithoutHotbarSlotRange().getEnd()) {
      res.add(hotbar);
    }
    if (slotIndex >= hotbar.getStart() && slotIndex < hotbar.getEnd()) {
      res.add(owner.getPlayerInventoryWithoutHotbarSlotRange());
    }
  }
  
  public abstract @Nonnull List<SlotRange> getTargetSlotsForTransfer(int slotNumber, @Nonnull Slot slot); 
  
  public static class SlotRange implements SlotPredicate {
    private final int start;
    private final int end;
    private final boolean reverse;

    public SlotRange(int start, int end, boolean reverse) {
      this.start = start;
      this.end = end;
      this.reverse = reverse;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }

    @Override
    public boolean test(Slot t) {
      return t.slotNumber >= getStart() && t.slotNumber < getEnd();
    }
    
    @Override
    public boolean isReverse() {
      return reverse;
    }
  }
  
  public interface IRangeProvider {
    
    
    
    SlotRange getPlayerInventorySlotRange(boolean reverse);

    SlotRange getPlayerInventoryWithoutHotbarSlotRange();

    SlotRange getPlayerHotbarSlotRange();

  }

}
