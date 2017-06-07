package crazypants.enderio.gui;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;

import net.minecraft.client.Minecraft;

public class ToggleTravelButton extends IconButton {

  IToggleableGui togglegui;

  public ToggleTravelButton(@Nonnull IToggleableGui gui, int id, int x, int y, IconEIO icon) {
    super(gui, id, x, y, icon);
    this.togglegui = gui;
  }

  @Override
  public boolean mousePressed(@Nonnull Minecraft par1Minecraft, int par2, int par3) {
    boolean result = super.mousePressed(par1Minecraft, par2, par3);
    if (result) {
      togglegui.switchGui();
    }
    return result;
  }

}
