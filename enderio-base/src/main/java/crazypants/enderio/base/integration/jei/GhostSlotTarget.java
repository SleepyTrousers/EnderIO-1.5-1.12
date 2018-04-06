package crazypants.enderio.base.integration.jei;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.filter.filters.item.ItemFilter;
import crazypants.enderio.base.filter.gui.BasicItemFilterGui;
import mezz.jei.api.gui.IGhostIngredientHandler.Target;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "mezz.jei.api.gui.IGhostIngredientHandler.Target", modid = "JustEnoughItems")
public class GhostSlotTarget<I> implements Target<I> {

  private ItemFilter filter;
  private int x, y;
  private GhostSlot slot;
  private BasicItemFilterGui gui;

  public GhostSlotTarget(ItemFilter filter, GhostSlot slot, int x, int y, BasicItemFilterGui gui) {
    this.filter = filter;
    this.slot = slot;
    this.x = x;
    this.y = y;
    this.gui = gui;
  }

  @Override
  @Nonnull
  public Rectangle getArea() {
    return new Rectangle(slot.getX() + x, slot.getY() + y, 16, 16);
  }

  @Override
  public void accept(I ingredient) {
    if (ingredient instanceof ItemStack) {
      filter.setInventorySlotContents(slot.getSlot(), (ItemStack) ingredient);
      gui.sendFilterChange();
    }
  }

}
