package crazypants.enderio.conduit.gui;

import java.awt.Color;
import java.awt.Rectangle;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.IItemFilter;
import crazypants.enderio.conduit.item.ItemFilter;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.gui.ColorButton;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.gui.GuiToolTip;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.DyeColor;
import crazypants.util.Lang;

public class ItemSettings extends BaseSettingsPanel {

  private static final int NEXT_FILTER_ID = 98932;

  private static final int ID_REDSTONE_BUTTON = 12614;

  private static final int ID_COLOR_BUTTON = 179816;

  private static final int ID_LOOP = 22;
  private static final int ID_ROUND_ROBIN = 24;
  private static final int ID_PRIORITY_UP = 25;
  private static final int ID_PRIORITY_DOWN = 26;

  private IItemConduit itemConduit;

  private String inputHeading;
  private String outputHeading;

  private IconButtonEIO nextFilterB;

  private ToggleButtonEIO loopB;
  private ToggleButtonEIO roundRobinB;

  private IconButtonEIO priUpB;
  private IconButtonEIO priDownB;

  private RedstoneModeButton rsB;
  private ColorButton colorB;

  boolean inOutShowIn = false;

  private IItemFilter activeFilter;

  private int priLeft = 120;
  private int priWidth = 32;

  private GuiToolTip priorityTooltip;

  private BasicItemFilterGui filterGui;

  protected ItemSettings(final GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, Lang.localize("itemItemConduit.name"), gui, con);
    itemConduit = (IItemConduit) con;

    inputHeading = Lang.localize("gui.conduit.item.extractionFilter");
    outputHeading = Lang.localize("gui.conduit.item.insertionFilter");

    int x = 98;
    int y = customTop;

    nextFilterB = new IconButtonEIO(gui, NEXT_FILTER_ID, x, y, IconEIO.RIGHT_ARROW);
    nextFilterB.setSize(8, 16);

    x = 115;
    rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        RedstoneControlMode curMode = getRedstoneControlMode();
        itemConduit.setExtractionRedstoneMode(mode, gui.dir);
        if(curMode != mode) {
          EnderIO.packetPipeline.sendToServer(new PacketExtractMode(itemConduit, gui.dir));
        }

      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return itemConduit.getExtractionRedstoneMode(gui.dir);
      }
    });

    x += rsB.getWidth() + 4;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.dir).ordinal());
    colorB.setToolTipHeading(Lang.localize("gui.conduit.item.sigCol"));

    x += 4 + colorB.getWidth();
    roundRobinB = new ToggleButtonEIO(gui, ID_ROUND_ROBIN, x, y, IconEIO.ROUND_ROBIN_OFF, IconEIO.ROUND_ROBIN);
    roundRobinB.setSelectedToolTip(Lang.localize("gui.conduit.item.roundRobinEnabled"));
    roundRobinB.setUnselectedToolTip(Lang.localize("gui.conduit.item.roundRobinDisabled"));
    roundRobinB.setPaintSelectedBorder(false);

    x += 4 + roundRobinB.getWidth();
    loopB = new ToggleButtonEIO(gui, ID_LOOP, x, y, IconEIO.LOOP_OFF, IconEIO.LOOP);
    loopB.setSelectedToolTip(Lang.localize("gui.conduit.item.selfFeedEnabled"));
    loopB.setUnselectedToolTip(Lang.localize("gui.conduit.item.selfFeedDisabled"));
    loopB.setPaintSelectedBorder(false);

    x = priLeft + priWidth + 9;
    priUpB = new IconButtonEIO(gui, ID_PRIORITY_UP, x, y, IconEIO.ADD_BUT);
    priUpB.setSize(10, 10);

    y += 10;
    priDownB = new IconButtonEIO(gui, ID_PRIORITY_DOWN, x, y, IconEIO.MINUS_BUT);
    priDownB.setSize(10, 10);

    priorityTooltip = new GuiToolTip(new Rectangle(priLeft + 9, y, priWidth, 20), Lang.localize("gui.conduit.item.priority"));

    gui.container.filterListeners.add(new FilterChangeListener() {

      @Override
      public void onFilterChanged() {
        filtersChanged();
      }

    });

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
    updateGuiVisibility();
  }

  private void updateGuiVisibility() {

    deactivate();

    boolean showInput = false;
    boolean showOutput = false;

    ConnectionMode mode = con.getConectionMode(gui.dir);
    if(mode == ConnectionMode.INPUT) {
      showInput = true;
      //      gui.container.setInputSlotsVisible(true);
      //      gui.container.setOutputSlotsVisible(false);
      //      gui.container.setInventorySlotsVisible(true);
    } else if(mode == ConnectionMode.OUTPUT) {
      showOutput = true;
      //      gui.container.setInputSlotsVisible(false);
      //      gui.container.setOutputSlotsVisible(true);
      //      gui.container.setInventorySlotsVisible(true);
    } else if(mode == ConnectionMode.IN_OUT) {

      if(nextFilterB != null) {
        nextFilterB.onGuiInit();
      }
      showInput = inOutShowIn;
      showOutput = !inOutShowIn;

    }

    if(!showInput && !showOutput) {
      filterGui = null;
      activeFilter = null;
    } else if(showInput) {
      activeFilter = itemConduit.getInputFilter(gui.dir);
      gui.container.setInventorySlotsVisible(true);
      gui.container.setInputSlotsVisible(true);
      gui.container.setOutputSlotsVisible(false);
      if(activeFilter != null) {
        filterGui = new BasicItemFilterGui(gui, itemConduit, true);        
        
      }
    } else if(showOutput) {
      activeFilter = itemConduit.getOutputFilter(gui.dir);
      gui.container.setInputSlotsVisible(false);
      gui.container.setOutputSlotsVisible(true);
      gui.container.setInventorySlotsVisible(true);
      if(activeFilter != null) {
        filterGui = new BasicItemFilterGui(gui, itemConduit, false);        
      }
    }

    updateButtons();

  }

  private void filtersChanged() {
    if(filterGui != null) {
      filterGui.deactivate();
      filterGui = null;
    }

    ConnectionMode mode = con.getConectionMode(gui.dir);
    if(mode == ConnectionMode.DISABLED) {
      return;
    }

    boolean showInput = mode == ConnectionMode.INPUT || (mode == ConnectionMode.IN_OUT && inOutShowIn);
    if(showInput) {
      gui.container.setInventorySlotsVisible(true);   
      gui.container.setInputSlotsVisible(true);
      gui.container.setOutputSlotsVisible(false);
      
      activeFilter = itemConduit.getInputFilter(gui.dir);
      if(activeFilter != null) {
        filterGui = new BasicItemFilterGui(gui, itemConduit, true);        
      }
    } else {
      gui.container.setInventorySlotsVisible(true);   
      gui.container.setInputSlotsVisible(false);
      gui.container.setOutputSlotsVisible(true);
      
      activeFilter = itemConduit.getOutputFilter(gui.dir);
      if(activeFilter != null) {
        filterGui = new BasicItemFilterGui(gui, itemConduit, false);        
      }
      
    }
    if(filterGui != null) {      
      filterGui.updateButtons();
    }

  }

  private void updateButtons() {

    ConnectionMode mode = con.getConectionMode(gui.dir);
    if(mode == ConnectionMode.DISABLED) {
      return;
    }
    boolean outputActive = (mode == ConnectionMode.IN_OUT && !inOutShowIn) || (mode == ConnectionMode.OUTPUT);
    int chanCol;
    if(!outputActive) {

      rsB.onGuiInit();
      rsB.setMode(itemConduit.getExtractionRedstoneMode(gui.dir));

      colorB.onGuiInit();
      colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.dir).ordinal());
    }

    if(mode == ConnectionMode.IN_OUT && !outputActive) {
      loopB.onGuiInit();
      loopB.setSelected(itemConduit.isSelfFeedEnabled(gui.dir));
    }

    if((mode == ConnectionMode.IN_OUT && !outputActive) || mode == ConnectionMode.INPUT) {
      roundRobinB.onGuiInit();
      roundRobinB.setSelected(itemConduit.isRoundRobinEnabled(gui.dir));
    }

    if((mode == ConnectionMode.IN_OUT && outputActive) || mode == ConnectionMode.OUTPUT) {
      priUpB.onGuiInit();
      priDownB.onGuiInit();
      gui.addToolTip(priorityTooltip);
    }

    if(filterGui != null) {
      filterGui.updateButtons();
    }

  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if(guiButton.id == NEXT_FILTER_ID) {
      inOutShowIn = !inOutShowIn;
      updateGuiVisibility();
    } else if(guiButton.id == ID_COLOR_BUTTON) {
      itemConduit.setExtractionSignalColor(gui.dir, DyeColor.values()[colorB.getColorIndex()]);
      EnderIO.packetPipeline.sendToServer(new PacketExtractMode(itemConduit, gui.dir));
    } else if(guiButton.id == ID_LOOP) {
      itemConduit.setSelfFeedEnabled(gui.dir, !itemConduit.isSelfFeedEnabled(gui.dir));
      EnderIO.packetPipeline.sendToServer(new PacketItemConduitFilter(itemConduit, gui.dir));
    } else if(guiButton.id == ID_ROUND_ROBIN) {
      itemConduit.setRoundRobinEnabled(gui.dir, !itemConduit.isRoundRobinEnabled(gui.dir));
      EnderIO.packetPipeline.sendToServer(new PacketItemConduitFilter(itemConduit, gui.dir));
    } else if(guiButton.id == ID_PRIORITY_UP) {
      itemConduit.setOutputPriority(gui.dir, itemConduit.getOutputPriority(gui.dir) + 1);
      EnderIO.packetPipeline.sendToServer(new PacketItemConduitFilter(itemConduit, gui.dir));
    } else if(guiButton.id == ID_PRIORITY_DOWN) {
      itemConduit.setOutputPriority(gui.dir, itemConduit.getOutputPriority(gui.dir) - 1);
      EnderIO.packetPipeline.sendToServer(new PacketItemConduitFilter(itemConduit, gui.dir));
    }

    if(filterGui != null) {
      filterGui.actionPerformed(guiButton);
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
      int x = 0;
      int rgb = ColorUtil.getRGB(Color.darkGray);
      fr.drawString(heading, left + x, top, rgb);

      boolean outputActive = (mode == ConnectionMode.IN_OUT && !inOutShowIn) || (mode == ConnectionMode.OUTPUT);
      if(outputActive) {
        GL11.glColor3f(1, 1, 1);
        IconEIO.BUTTON_DOWN.renderIcon(left + priLeft, top - 5, priWidth, 20, 0, true);
        String str = itemConduit.getOutputPriority(gui.dir) + "";
        int sw = fr.getStringWidth(str);
        fr.drawString(str, left + priLeft + priWidth - sw - gap, top, ColorUtil.getRGB(Color.black));

        GL11.glColor3f(1, 1, 1);
        RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
        gui.drawTexturedModalRect(gui.getGuiLeft() + 9, gui.getGuiTop() + 66, 94, 220, 18, 18);

      } else {
        //draw speed upgrade slot
        GL11.glColor3f(1, 1, 1);
        RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
        gui.drawTexturedModalRect(gui.getGuiLeft() + 9, gui.getGuiTop() + 66, 94, 220, 18, 36);
      }
      if(filterGui != null) {
        filterGui.renderCustomOptions(top, par1, par2, par3);
      }
    }
  }

  @Override
  public void deactivate() {
    gui.container.setInventorySlotsVisible(false);
    gui.container.setInputSlotsVisible(false);
    gui.container.setOutputSlotsVisible(false);
    rsB.detach();
    colorB.detach();
    roundRobinB.detach();
    loopB.detach();
    nextFilterB.detach();
    priUpB.detach();
    priDownB.detach();
    gui.removeToolTip(priorityTooltip);

    if(filterGui != null) {
      filterGui.deactivate();
      filterGui = null;
    }
  }

}
