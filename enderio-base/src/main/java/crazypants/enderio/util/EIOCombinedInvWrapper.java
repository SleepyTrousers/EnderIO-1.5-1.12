package crazypants.enderio.util;

import javax.annotation.Nonnull;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

/**
 * Just making some protected method available...
 * 
 * @author Henry Loenwind
 *
 */
public class EIOCombinedInvWrapper<T extends IItemHandlerModifiable> extends CombinedInvWrapper {

  @SafeVarargs
  public EIOCombinedInvWrapper(T... itemHandler) {
    super(itemHandler);
  }

  @SuppressWarnings({ "unchecked", "null" })
  @Override
  public @Nonnull T getHandlerFromIndex(int index) {
    return (T) super.getHandlerFromIndex(index);
  }

  @SuppressWarnings({ "unchecked", "null" })
  public @Nonnull T getHandlerFromSlot(int slot) {
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
