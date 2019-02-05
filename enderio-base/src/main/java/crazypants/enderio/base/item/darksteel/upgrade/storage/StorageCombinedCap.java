package crazypants.enderio.base.item.darksteel.upgrade.storage;

import net.minecraftforge.items.wrapper.CombinedInvWrapper;

/**
 * Just making some protected method available...
 * 
 * @author Henry Loenwind
 *
 */
public class StorageCombinedCap extends CombinedInvWrapper {

  public StorageCombinedCap(StorageCap... itemHandler) {
    super(itemHandler);
  }

  @Override
  protected StorageCap getHandlerFromIndex(int index) {
    return (StorageCap) super.getHandlerFromIndex(index);
  }

  protected StorageCap getHandlerFromSlot(int slot) {
    return (StorageCap) super.getHandlerFromIndex(getIndexForSlot(slot));
  }

  @Override
  protected int getIndexForSlot(int slot) {
    return super.getIndexForSlot(slot);
  }

}
