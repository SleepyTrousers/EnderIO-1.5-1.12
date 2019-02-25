package crazypants.enderio.base.integration.jei;

import java.awt.Rectangle;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.GuiContainerBase;
import com.enderio.core.client.gui.widget.GhostSlot;

import mezz.jei.api.gui.IGhostIngredientHandler.Target;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
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
        if (ingredient instanceof ItemStack) {
          s.putStack((ItemStack) ingredient, 1);
        } else if (ingredient instanceof FluidStack && s instanceof IFluidGhostSlot) {
          ((IFluidGhostSlot) s).putFluidStack((FluidStack) ingredient);
        } else if (ingredient instanceof Fluid && s instanceof IFluidGhostSlot) {
          ((IFluidGhostSlot) s).putFluid((Fluid) ingredient);
        } else if (ingredient instanceof EnchantmentData && s instanceof IEnchantmentGhostSlot) {
          ((IEnchantmentGhostSlot) s).putEnchantmentData((EnchantmentData) ingredient);
        } else if (ingredient instanceof Enchantment && s instanceof IEnchantmentGhostSlot) {
          ((IEnchantmentGhostSlot) s).putEnchantment((Enchantment) ingredient);
        } else if (s instanceof ICustomGhostSlot) {
          ICustomGhostSlot customSlot = (ICustomGhostSlot) s;
          if (customSlot.isType(ingredient)) {
            customSlot.putIngredient(ingredient);
          }
        }
      }
    }).collect(Collectors.toList());
  }

  default boolean isSlotTarget(GhostSlot slot) {
    return true;
  }

  interface IFluidGhostSlot {
    default void putFluidStack(@Nonnull FluidStack stack) {
      final Fluid fluid = stack.getFluid();
      if (fluid != null) {
        putFluid(fluid);
      }
    }

    void putFluid(@Nonnull Fluid fluid);
  }

  interface IEnchantmentGhostSlot {
    default void putEnchantmentData(@Nonnull EnchantmentData enchantmentData) {
      if (enchantmentData.enchantmentLevel > 0) {
        putEnchantment(enchantmentData.enchantment);
      }
    }

    void putEnchantment(@Nonnull Enchantment enchantment);
  }

  interface ICustomGhostSlot {
    void putIngredient(Object ingredient);

    boolean isType(Object object);
  }

}
