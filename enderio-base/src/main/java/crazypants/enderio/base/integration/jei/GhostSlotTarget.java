package crazypants.enderio.base.integration.jei;

import java.awt.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.gui.AbstractFilterGui;
import mezz.jei.api.gui.IGhostIngredientHandler.Target;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "mezz.jei.api.gui.IGhostIngredientHandler$Target", modid = "jei")
public class GhostSlotTarget<I> implements Target<I> {

  private @Nonnull IFilter filter;
  private int x, y, slotX, slotY, slotIndex;
  private @Nonnull AbstractFilterGui gui;
  private GhostSlot slot;

  public GhostSlotTarget(@Nonnull IFilter filter, @Nonnull GhostSlot slot, int x, int y, @Nonnull AbstractFilterGui gui) {
    this.filter = filter;
    this.slotIndex = slot.getSlot();
    this.slotX = slot.getX();
    this.slotY = slot.getY();
    this.x = x;
    this.y = y;
    this.gui = gui;
    this.slotIndex = slot.getSlot();
    this.slot = slot;
  }

  public GhostSlotTarget(@Nonnull IFilter filter, int slot, int x, int y, int slotX, int slotY, @Nonnull AbstractFilterGui gui) {
    this.filter = filter;
    this.slotIndex = slot;
    this.x = x;
    this.y = y;
    this.gui = gui;
    this.slotX = slotX;
    this.slotY = slotY;
  }

  @Override
  @Nonnull
  public Rectangle getArea() {
    return new Rectangle(slotX + x, slotY + y, 16, 16);
  }

  @Override
  public void accept(@Nullable I ingredient) {
    if (ingredient instanceof ItemStack) {
      if (slot != null) {
        slot.putStack((ItemStack) ingredient, 1);
        return;
      }
      filter.setInventorySlotContents(slotIndex, (ItemStack) ingredient);
      gui.sendFilterChange();
    }
  }

}
