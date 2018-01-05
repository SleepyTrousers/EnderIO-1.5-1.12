package crazypants.enderio.base.integration.jei;

import java.awt.Rectangle;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import mezz.jei.api.gui.IAdvancedGuiHandler;

public class AdvancedGuiHandlerEnderIO implements IAdvancedGuiHandler<GuiContainerBaseEIO> {

  public AdvancedGuiHandlerEnderIO() {
  }

  @Override
  @Nonnull
  public Class<GuiContainerBaseEIO> getGuiContainerClass() {
    return GuiContainerBaseEIO.class;
  }

  @Override
  @Nullable
  public List<Rectangle> getGuiExtraAreas(@Nonnull GuiContainerBaseEIO guiContainer) {
    return guiContainer.getBlockingAreas();
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(@Nonnull GuiContainerBaseEIO guiContainer, int mouseX, int mouseY) {
    return guiContainer.getIngredientUnderMouse(mouseX - guiContainer.getGuiLeft(), mouseY - guiContainer.getGuiTop());
  }

}
