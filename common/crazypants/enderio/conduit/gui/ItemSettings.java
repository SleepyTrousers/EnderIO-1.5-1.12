package crazypants.enderio.conduit.gui;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;

public class ItemSettings extends BaseSettingsPanel {

  private static final int NEXT_FILTER_ID = 98932;

  private IItemConduit itemConduit;

  private String inputHeading = "Extraction Filter";
  private String outputHeading = "Insertion Filter";

  private IconButtonEIO nextFilterB;
  private IconButtonEIO prevFilterB;

  boolean inOutShowIn = true;

  protected ItemSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, ModObject.itemItemConduit.name, gui, con);
    itemConduit = (IItemConduit) con;

  }

  private String getHeading() {
    ConnectionMode mode = con.getConectionMode(gui.dir);
    if(mode == ConnectionMode.DISABLED) {
      return "";
    }
    if(mode == ConnectionMode.OUTPUT) {
      return outputHeading;
    }
    if(mode == ConnectionMode.INPUT || inOutShowIn) {
      return inputHeading;
    }
    return outputHeading;
  }

  @Override
  protected void initCustomOptions() {
    if(nextFilterB != null) {
      gui.removeButton(nextFilterB);
    }
    int headingWidth = gui.getFontRenderer().getStringWidth(getHeading());
    int x = ((width - headingWidth) / 2) + headingWidth + 10;
    int y = customTop;
    nextFilterB = new IconButtonEIO(gui, NEXT_FILTER_ID, x, y, IconEIO.RIGHT_ARROW);
    nextFilterB.setSize(8, 16);

    x = ((width - headingWidth) / 2);

    prevFilterB = new IconButtonEIO(gui, NEXT_FILTER_ID, x, y, IconEIO.LEFT_ARROW);
    prevFilterB.setSize(8, 16);
    //nextFilterB.onGuiInit();

    updateGuiVisibility();
  }

  private void updateGuiVisibility() {
    gui.removeButton(nextFilterB);
    gui.removeButton(prevFilterB);

    ConnectionMode mode = con.getConectionMode(gui.dir);
    if(mode == ConnectionMode.DISABLED) {
      deactivate();
    } else if(mode == ConnectionMode.INPUT) {
      gui.container.setInputSlotsVisible(true);
      gui.container.setOutputSlotsVisible(false);
      gui.container.setInventorySlotsVisible(true);
    } else if(mode == ConnectionMode.OUTPUT) {
      gui.container.setInputSlotsVisible(false);
      gui.container.setOutputSlotsVisible(true);
      gui.container.setInventorySlotsVisible(true);
    } else {
      //TODO
      if(nextFilterB != null) {
        nextFilterB.onGuiInit();
        prevFilterB.onGuiInit();
      }
      if(inOutShowIn) {
        gui.container.setInputSlotsVisible(true);
        gui.container.setOutputSlotsVisible(false);
        gui.container.setInventorySlotsVisible(true);
      } else {
        gui.container.setInputSlotsVisible(false);
        gui.container.setOutputSlotsVisible(true);
        gui.container.setInventorySlotsVisible(true);
      }
    }

  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if(guiButton.id == NEXT_FILTER_ID) {
      inOutShowIn = !inOutShowIn;
      updateGuiVisibility();
    }
  }

  @Override
  protected void connectionModeChanged(ConnectionMode conectionMode) {
    super.connectionModeChanged(conectionMode);
    updateGuiVisibility();
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    ConnectionMode mode = con.getConectionMode(gui.dir);
    if(mode != ConnectionMode.DISABLED) {
      RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
      gui.drawTexturedModalRect(gui.getGuiLeft(), gui.getGuiTop() + 55, 0, 55, gui.getXSize(), 145);

      FontRenderer fr = gui.getFontRenderer();
      String heading = getHeading();
      int headingWidth = fr.getStringWidth(heading);
      int x = (width - headingWidth) / 2;

      int rgb = ColorUtil.getRGB(Color.darkGray);
      if(mode == ConnectionMode.IN_OUT) {
        //        rgb = ColorUtil.getRGB(Color.white);
        //
        //        GL11.glColor3f(1, 1, 1);
        //        IconEIO icon = new IconEIO(10, 60, 64, 20);
        //        icon.renderIcon(left + x - gap, top - 5, headingWidth + gap * 2, leftArrow.getHeight(), 0, true);

      }

      fr.drawString(heading, left + x, top, rgb);
    }

  }

  @Override
  public void deactivate() {
    gui.container.setInventorySlotsVisible(false);
    gui.container.setInputSlotsVisible(false);
    gui.container.setOutputSlotsVisible(false);
  }

}
