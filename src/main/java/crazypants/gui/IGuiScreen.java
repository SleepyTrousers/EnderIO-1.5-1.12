package crazypants.gui;

import java.util.List;
import net.minecraft.client.gui.GuiButton;

public interface IGuiScreen {

  void addToolTip(GuiToolTip toolTip);

  void removeToolTip(GuiToolTip toolTip);

  int getGuiLeft();

  int getGuiTop();

  int getXSize();

  int getYSize();

  void addButton(GuiButton button);

  void removeButton(GuiButton button);

  int getOverlayOffsetX();
  
  void doActionPerformed(GuiButton but);

  List<GhostSlot> getGhostSlots();

}
