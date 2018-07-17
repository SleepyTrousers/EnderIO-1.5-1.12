package crazypants.enderio.base.integration.jei;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.gui.widget.GhostSlot;

import mezz.jei.api.gui.IGhostIngredientHandler.Target;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IHaveGhostTargets<T extends GuiContainerBase> {

  default List<? extends Target<?>> getGhostTargets() {
    if (!(this instanceof GuiContainerBase)) {
      throw new IllegalStateException("IHaveGhostTargets must be implemented by GuiContainerBase subclasses, or provide a custom implementation.");
    }
    GuiContainerBase gui = (GuiContainerBase) this;
    return gui.getGhostSlotHandler().getGhostSlots().stream().filter(this::isSlotTarget).map(s -> new Target<Object>() {

      @Override
      public @Nonnull Rectangle getArea() {
        return new Rectangle(s.getX() + gui.getGuiLeft(), s.getY() + gui.getGuiTop(), 16, 16);
      }

      @Override
      public void accept(Object ingredient) {
        System.out.println(ingredient);
        if (ingredient instanceof ItemStack) {
          s.putStack((ItemStack) ingredient, 1);
        } else if (ingredient instanceof FluidStack && s instanceof IFluidGhostSlot) {
          ((IFluidGhostSlot) s).putFluidStack((FluidStack) ingredient);
        } else if (ingredient instanceof Fluid && s instanceof IFluidGhostSlot) {
          ((IFluidGhostSlot) s).putFluid((Fluid) ingredient);
        }
      }
    }).collect(Collectors.toList());
  }

  default boolean isSlotTarget(GhostSlot slot) {
    return true;
  }

  public interface IFluidGhostSlot {

    default void putFluidStack(@Nonnull FluidStack stack) {
      final Fluid fluid = stack.getFluid();
      if (fluid != null) {
        putFluid(fluid);
      }
    }

    void putFluid(@Nonnull Fluid fluid);

  }

}
