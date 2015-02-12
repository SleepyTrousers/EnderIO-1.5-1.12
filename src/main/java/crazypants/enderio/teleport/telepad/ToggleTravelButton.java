package crazypants.enderio.teleport.telepad;

import net.minecraft.client.Minecraft;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;

public class ToggleTravelButton extends IconButtonEIO {

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
