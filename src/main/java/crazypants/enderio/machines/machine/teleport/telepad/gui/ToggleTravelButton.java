package crazypants.enderio.machines.machine.teleport.telepad.gui;

import com.enderio.core.client.gui.button.IconButton;

import crazypants.enderio.gui.IconEIO;
import net.minecraft.client.Minecraft;

public class ToggleTravelButton extends IconButton {

  IToggleableGui gui;
  
  public ToggleTravelButton(IToggleableGui gui, int id, int x, int y, IconEIO icon) {
    super(gui, id, x, y, icon);
    this.gui = gui;
  }
  
  @Override
  public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
    boolean result = super.mousePressed(par1Minecraft, par2, par3);
    if(result) {
      gui.switchGui();
    }
    return result;
  }
}
