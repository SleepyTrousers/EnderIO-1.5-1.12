package crazypants.enderio.base.integration.jei;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;

import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.gui.widget.GhostSlot;

import mezz.jei.api.gui.IGhostIngredientHandler.Target;
import net.minecraft.item.ItemStack;

public interface IHaveGhostTargets<T extends GuiContainerBase> {
  
  default List<? extends Target<?>> getGhostTargets() {
    if (!(this instanceof GuiContainerBase)) {
      throw new IllegalStateException("IHaveGhostTargets must be implemented by GuiContainerBase subclasses, or provide a custom implementation.");
    }
    GuiContainerBase gui = (GuiContainerBase) this;
    return gui.getGhostSlotHandler().getGhostSlots().stream()
        .filter(this::isSlotTarget)
        .map(s -> new Target<Object>() {
          
          @Override
          public Rectangle getArea() {
            return new Rectangle(s.getX() + gui.getGuiLeft(), s.getY() + gui.getGuiTop(), 16, 16);
          }
          
          @Override
          public void accept(Object ingredient) {
            if (ingredient instanceof ItemStack) {
              s.putStack((ItemStack) ingredient, 1);
            }
          }
        }).collect(Collectors.toList());
  }
  
  default boolean isSlotTarget(GhostSlot slot) {
    return true;
  }

}
