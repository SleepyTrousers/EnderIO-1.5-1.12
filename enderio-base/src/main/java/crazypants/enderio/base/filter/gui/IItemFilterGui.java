package crazypants.enderio.base.filter.gui;

import net.minecraft.client.gui.GuiButton;

import javax.annotation.Nonnull;

// TODO Javadocs (check)
public interface IItemFilterGui {

  /**
   * Called when closing the filter part of the Gui
   */
  void deactivate();

  /**
   * Called to update the buttons based on user input
   * or activating/deactivationg the gui
   */
  void updateButtons();

  void actionPerformed(@Nonnull GuiButton guiButton);
  
  void renderCustomOptions(int top, float par1, int par2, int par3);

  void mouseClicked(int x, int y, int par3);

}
