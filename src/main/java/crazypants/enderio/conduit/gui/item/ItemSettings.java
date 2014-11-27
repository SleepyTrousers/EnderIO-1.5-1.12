package crazypants.enderio.conduit.gui.item;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.gui.BaseSettingsPanel;
import crazypants.enderio.conduit.gui.FilterChangeListener;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.filter.ExistingItemFilter;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.conduit.item.filter.ItemFilter;
import crazypants.enderio.conduit.item.filter.ModItemFilter;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.conduit.packet.PacketItemConduitFilter;
import crazypants.enderio.gui.ColorButton;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;
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
  private static final int ID_CHANNEL = 23;

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
  
  private ColorButton channelB;

  boolean inOutShowIn = false;

  private IItemFilter activeFilter;

  private int priLeft;
  private int priWidth = 32;

  private GuiToolTip priorityTooltip;

  private IItemFilterGui filterGui;

  public ItemSettings(final GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, Lang.localize("itemItemConduit.name"), gui, con);
    itemConduit = (IItemConduit) con;

    inputHeading = Lang.localize("gui.conduit.item.extractionFilter");
    outputHeading = Lang.localize("gui.conduit.item.insertionFilter");

    int x = 52;
    int y = customTop;

    nextFilterB = new IconButtonEIO(gui, NEXT_FILTER_ID, x, y, IconEIO.RIGHT_ARROW);
    nextFilterB.setSize(8, 16);

    x = 66;    
    channelB = new ColorButton(gui, ID_CHANNEL, x, y);
    channelB.setColorIndex(0);
    channelB.setToolTipHeading(Lang.localize("gui.conduit.item.channel"));
    
    x += channelB.getWidth() + 4;
    priLeft = x - 8;
    
    rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        RedstoneControlMode curMode = getRedstoneControlMode();
        itemConduit.setExtractionRedstoneMode(mode, gui.getDir());
        if(curMode != mode) {
          PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
        }

      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return itemConduit.getExtractionRedstoneMode(gui.getDir());
      }
    });

    x += rsB.getWidth() + 4;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.getDir()).ordinal());
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
    priUpB.setSize(8, 8);

    y += 8;
    priDownB = new IconButtonEIO(gui, ID_PRIORITY_DOWN, x, y, IconEIO.MINUS_BUT);
    priDownB.setSize(8, 8);

    priorityTooltip = new GuiToolTip(new Rectangle(priLeft + 9, y, priWidth, 20), Lang.localize("gui.conduit.item.priority"));

    gui.getContainer().addFilterListener(new FilterChangeListener() {

      @Override
      public void onFilterChanged() {
        filtersChanged();
      }

    });

  }

  private String getHeading() {
    ConnectionMode mode = con.getConnectionMode(gui.getDir());
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

    ConnectionMode mode = con.getConnectionMode(gui.getDir());
    if(mode == ConnectionMode.INPUT) {
      showInput = true;     
    } else if(mode == ConnectionMode.OUTPUT) {
      showOutput = true;

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
      activeFilter = itemConduit.getInputFilter(gui.getDir());
      gui.getContainer().setInventorySlotsVisible(true);
      gui.getContainer().setInputSlotsVisible(true);
      gui.getContainer().setOutputSlotsVisible(false);
      if(activeFilter != null) {
        filterGui = getFilterGui(activeFilter, true);

      }
    } else if(showOutput) {
      activeFilter = itemConduit.getOutputFilter(gui.getDir());
      gui.getContainer().setInputSlotsVisible(false);
      gui.getContainer().setOutputSlotsVisible(true);
      gui.getContainer().setInventorySlotsVisible(true);
      if(activeFilter != null) {
        filterGui = getFilterGui(activeFilter, false);
      }
    }

    updateButtons();

  }

  private void filtersChanged() {
    if(filterGui != null) {
      filterGui.deactivate();
      filterGui = null;
    }

    ConnectionMode mode = con.getConnectionMode(gui.getDir());
    if(mode == ConnectionMode.DISABLED) {
      return;
    }

    boolean showInput = mode == ConnectionMode.INPUT || (mode == ConnectionMode.IN_OUT && inOutShowIn);
    if(showInput) {
      gui.getContainer().setInventorySlotsVisible(true);
      gui.getContainer().setInputSlotsVisible(true);
      gui.getContainer().setOutputSlotsVisible(false);

      activeFilter = itemConduit.getInputFilter(gui.getDir());
      if(activeFilter != null) {
        filterGui = getFilterGui(activeFilter, true);        
      }
    } else {
      gui.getContainer().setInventorySlotsVisible(true);
      gui.getContainer().setInputSlotsVisible(false);
      gui.getContainer().setOutputSlotsVisible(true);

      activeFilter = itemConduit.getOutputFilter(gui.getDir());
      if(activeFilter != null) {
        filterGui = getFilterGui(activeFilter, false);        
      }

    }
    if(filterGui != null) {
      filterGui.updateButtons();
    }

  }

  private IItemFilterGui getFilterGui(IItemFilter filter, boolean isInput) {
    //TODO: move to a factory
    if(filter instanceof ItemFilter) {
      ItemConduitFilterContainer cont = new ItemConduitFilterContainer(itemConduit, gui.getDir(), isInput);
      return new BasicItemFilterGui(gui, cont, !isInput);
    } else if(filter instanceof ExistingItemFilter) {
      return new ExistingItemFilterGui(gui, itemConduit, isInput);
    } else if(filter instanceof ModItemFilter) {
      return new ModItemFilterGui(gui, itemConduit, isInput);
    }
    return null;
  }

  private void updateButtons() {

    ConnectionMode mode = con.getConnectionMode(gui.getDir());
    if(mode == ConnectionMode.DISABLED) {
      return;
    }
    boolean outputActive = (mode == ConnectionMode.IN_OUT && !inOutShowIn) || (mode == ConnectionMode.OUTPUT);
    
    if(!outputActive) {

      rsB.onGuiInit();
      rsB.setMode(itemConduit.getExtractionRedstoneMode(gui.getDir()));

      colorB.onGuiInit();
      colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.getDir()).ordinal());
    }

    if(mode == ConnectionMode.IN_OUT && !outputActive) {
      loopB.onGuiInit();
      loopB.setSelected(itemConduit.isSelfFeedEnabled(gui.getDir()));
    }

    if((mode == ConnectionMode.IN_OUT && !outputActive) || mode == ConnectionMode.INPUT) {
      roundRobinB.onGuiInit();
      roundRobinB.setSelected(itemConduit.isRoundRobinEnabled(gui.getDir()));
    }

    if((mode == ConnectionMode.IN_OUT && outputActive) || mode == ConnectionMode.OUTPUT) {
      priUpB.onGuiInit();
      priDownB.onGuiInit();
      gui.addToolTip(priorityTooltip);
    }
    
    int chanCol;
    if(!outputActive) {
      chanCol = itemConduit.getInputColor(gui.getDir()).ordinal();      
    } else {      
      chanCol = itemConduit.getOutputColor(gui.getDir()).ordinal();      
    }
    channelB.onGuiInit();
    channelB.setColorIndex(chanCol);

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
      itemConduit.setExtractionSignalColor(gui.getDir(), DyeColor.values()[colorB.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
    } else if(guiButton.id == ID_LOOP) {
      itemConduit.setSelfFeedEnabled(gui.getDir(), !itemConduit.isSelfFeedEnabled(gui.getDir()));
      PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
    } else if(guiButton.id == ID_ROUND_ROBIN) {
      itemConduit.setRoundRobinEnabled(gui.getDir(), !itemConduit.isRoundRobinEnabled(gui.getDir()));
      PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
    } else if(guiButton.id == ID_PRIORITY_UP) {
      itemConduit.setOutputPriority(gui.getDir(), itemConduit.getOutputPriority(gui.getDir()) + 1);
      PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
    } else if(guiButton.id == ID_PRIORITY_DOWN) {
      itemConduit.setOutputPriority(gui.getDir(), itemConduit.getOutputPriority(gui.getDir()) - 1);
      PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
    } else if(guiButton.id == ID_CHANNEL) {

      DyeColor col = DyeColor.values()[channelB.getColorIndex()];      
      boolean input;
      if(isInputVisible()) {
        col = DyeColor.values()[channelB.getColorIndex()];
        itemConduit.setInputColor(gui.getDir(), col);
        input = true;
      } else  {
        itemConduit.setOutputColor(gui.getDir(), DyeColor.values()[channelB.getColorIndex()]);
        input = false;
      } 
      PacketHandler.INSTANCE.sendToServer(new PacketItemConduitFilter(itemConduit, gui.getDir()));
    }

    if(filterGui != null) {
      filterGui.actionPerformed(guiButton);
    }
  }
  
  

  @Override
  public void mouseClicked(int x, int y, int par3) {    
    super.mouseClicked(x, y, par3);
    if(filterGui != null) {
      filterGui.mouseClicked(x, y, par3);
    }
  }

  private boolean isInputVisible() {
    ConnectionMode mode = con.getConnectionMode(gui.getDir());    
    return (mode == ConnectionMode.IN_OUT && inOutShowIn) || (mode == ConnectionMode.INPUT);
  }

  @Override
  protected void connectionModeChanged(ConnectionMode conectionMode) {
    super.connectionModeChanged(conectionMode);
    updateGuiVisibility();
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    ConnectionMode mode = con.getConnectionMode(gui.getDir());
    if(mode == ConnectionMode.DISABLED) {
      return;
    }

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
      IconEIO.BUTTON_DOWN.renderIcon(left + priLeft, top - 5, priWidth, 16, 0, true);
      String str = itemConduit.getOutputPriority(gui.getDir()) + "";
      int sw = fr.getStringWidth(str);
      fr.drawString(str, left + priLeft + priWidth - sw - gap, top, ColorUtil.getRGB(Color.black));

    } else {
      //draw speed upgrade slot
      GL11.glColor3f(1, 1, 1);
      RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
      gui.drawTexturedModalRect(gui.getGuiLeft() + 9 + 18, gui.getGuiTop() + 46, 94, 238, 18, 18);      
    }
    
    //filter upgrade slot
    GL11.glColor3f(1, 1, 1);
    RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
    gui.drawTexturedModalRect(gui.getGuiLeft() + 9, gui.getGuiTop() + 46, 94, 220, 18, 18);
    
    if(filterGui != null) {
      filterGui.renderCustomOptions(top, par1, par2, par3);
    }

  }

  @Override
  public void deactivate() {
    gui.getContainer().setInventorySlotsVisible(false);
    gui.getContainer().setInputSlotsVisible(false);
    gui.getContainer().setOutputSlotsVisible(false);
    rsB.detach();
    colorB.detach();
    roundRobinB.detach();
    loopB.detach();
    nextFilterB.detach();
    priUpB.detach();
    priDownB.detach();
    gui.removeToolTip(priorityTooltip);
    channelB.detach();

    if(filterGui != null) {
      filterGui.deactivate();
      filterGui = null;
    }
  }

}
