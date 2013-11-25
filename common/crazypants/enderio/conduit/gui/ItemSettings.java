package crazypants.enderio.conduit.gui;

import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.gui.IconEIO;
import crazypants.render.RenderUtil;

public class ItemSettings extends BaseSettingsPanel {

  //  private FilterGui inputFilterGui;

  private IItemConduit itemConduit;

  protected ItemSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, ModObject.itemItemConduit.name, gui, con);
    itemConduit = (IItemConduit) con;
  }

  @Override
  protected void initCustomOptions() {
    gui.container.setSlotsVisible(true);
    //gui.setSize(gui.getXSize(), 166 + 29);
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
    gui.drawTexturedModalRect(gui.getGuiLeft(), gui.getGuiTop() + 65, 0, 65, gui.getXSize(), 135);

  }

  @Override
  public void deactivate() {
    gui.container.setSlotsVisible(false);
    //gui.setSize(gui.getXSize(), 166);
  }

}
