package crazypants.enderio.base.item.darksteel.upgrade.storage;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

/**
 * Just making some protected method available...
 * 
 * @author Henry Loenwind
 *
 */
public class StorageCombinedCap<T extends IItemHandlerModifiable> extends CombinedInvWrapper {

  @SafeVarargs
  public StorageCombinedCap(T... itemHandler) {
    super(itemHandler);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T getHandlerFromIndex(int index) {
    return (T) super.getHandlerFromIndex(index);
  }

  @SuppressWarnings("unchecked")
  public T getHandlerFromSlot(int slot) {
    return (T) super.getHandlerFromIndex(getIndexForSlot(slot));
  }

  @Override
  public int getIndexForSlot(int slot) {
    return super.getIndexForSlot(slot);
  }

  public int getIndexForHandler(int slot) {
    return getSlotFromIndex(slot, getIndexForSlot(slot));
  }

}
