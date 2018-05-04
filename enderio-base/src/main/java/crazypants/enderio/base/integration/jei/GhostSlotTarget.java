package crazypants.enderio.base.integration.jei;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.filter.IFilter;
import crazypants.enderio.base.filter.gui.AbstractFilterGui;
import mezz.jei.api.gui.IGhostIngredientHandler.Target;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "mezz.jei.api.gui.IGhostIngredientHandler.Target", modid = "JustEnoughItems")
public class GhostSlotTarget<I> implements Target<I> {

  private IFilter filter;
  private int x, y, slotX, slotY, slotIndex;
  private AbstractFilterGui gui;

  public GhostSlotTarget(IFilter filter, GhostSlot slot, int x, int y, AbstractFilterGui gui) {
    this.filter = filter;
    this.slotIndex = slot.getSlot();
    this.slotX = slot.getX();
    this.slotY = slot.getY();
    this.x = x;
    this.y = y;
    this.gui = gui;
    this.slotIndex = slot.getSlot();
  }

  public GhostSlotTarget(IFilter filter, int slot, int x, int y, int slotX, int slotY, AbstractFilterGui gui) {
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
  public void accept(I ingredient) {
    if (ingredient instanceof ItemStack) {
      filter.setInventorySlotContents(slotIndex, (ItemStack) ingredient);
      gui.sendFilterChange();
    }
  }

}
