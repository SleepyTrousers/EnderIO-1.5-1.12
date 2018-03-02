package crazypants.enderio.base.filter.gui;

// TODO Javadocs (check)
public interface IItemFilterGui {

  /**
   * Called to update the buttons based on user input or activating/deactivating the gui
   */
  void updateButtons();

  void renderCustomOptions(int top, float par1, int par2, int par3);

}
