package crazypants.enderio.machines.machine.teleport.telepad.gui;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.IconButton;

import crazypants.enderio.base.gui.IconEIO;
import net.minecraft.client.Minecraft;

public class ToggleTravelButton extends IconButton {

  IToggleableGui gui;

  public ToggleTravelButton(@Nonnull IToggleableGui gui, int id, int x, int y, IconEIO icon) {
    super(gui, id, x, y, icon);
    this.gui = gui;
  }

  @Override
  public boolean mousePressed(@Nonnull Minecraft par1Minecraft, int par2, int par3) {
    boolean result = super.mousePressed(par1Minecraft, par2, par3);
    if (result) {
      gui.switchGui();
    }
    return result;
  }
}
