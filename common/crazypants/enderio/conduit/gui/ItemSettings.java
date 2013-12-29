package crazypants.enderio.conduit.gui;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.network.packet.Packet;
import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitPacketHandler;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemFilter;
import crazypants.enderio.gui.ColorButton;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.DyeColor;

public class ItemSettings extends BaseSettingsPanel {

  private static final int NEXT_FILTER_ID = 98932;

  private static final int ID_REDSTONE_BUTTON = 12614;

  private static final int ID_COLOR_BUTTON = 179816;

  private static final int ID_WHITELIST = 17;
  private static final int ID_NBT = 18;
  private static final int ID_META = 19;
  private static final int ID_ORE_DICT = 20;
  private static final int ID_STICKY = 21;
  private static final int ID_LOOP = 22;
  private static final int ID_CHANNEL = 23;

  private IItemConduit itemConduit;

  private String inputHeading = "Extraction Filter";
  private String outputHeading = "Insertion Filter";

  private IconButtonEIO nextFilterB;

  private ToggleButtonEIO useMetaB;
  private ToggleButtonEIO useNbtB;
  private IconButtonEIO whiteListB;
  private ToggleButtonEIO useOreDictB;
  private ToggleButtonEIO stickyB;
  private ColorButton channelB;

  private ToggleButtonEIO loopB;

  private RedstoneModeButton rsB;
  private ColorButton colorB;
  private String autoExtractStr = "Auto Extract";

  boolean inOutShowIn = false;

  boolean isAdvanced;

  private ItemFilter activeFilter;

  protected ItemSettings(final GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, ModObject.itemItemConduit.name, gui, con);
    itemConduit = (IItemConduit) con;
    isAdvanced = itemConduit.getMetaData() == 1;

    int x = 98;
    int y = customTop;

    nextFilterB = new IconButtonEIO(gui, NEXT_FILTER_ID, x, y, IconEIO.RIGHT_ARROW);
    nextFilterB.setSize(8, 16);

    x = 112;
    rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        RedstoneControlMode curMode = getRedstoneControlMode();
        itemConduit.setExtractionRedstoneMode(mode, gui.dir);
        if(curMode != mode) {
          Packet pkt = ConduitPacketHandler.createExtractionModePacket(itemConduit, gui.dir, mode);
          PacketDispatcher.sendPacketToServer(pkt);
        }

      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return itemConduit.getExtractioRedstoneMode(gui.dir);
      }
    });

    x += rsB.getWidth() + gap;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.dir).ordinal());
    colorB.setToolTipHeading("Signal Color");

    x = 112;
    y = 66;
    whiteListB = new IconButtonEIO(gui, ID_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip("Whitelist");

    x += 20;
    useMetaB = new ToggleButtonEIO(gui, ID_META, x, y, IconEIO.FILTER_META_OFF, IconEIO.FILTER_META);
    useMetaB.setSelectedToolTip("Match Meta Data");
    useMetaB.setUnselectedToolTip("Ignore Meta Data");
    useMetaB.setPaintSelectedBorder(false);

    x += 20;
    stickyB = new ToggleButtonEIO(gui, ID_STICKY, x, y, IconEIO.FILTER_STICKY_OFF, IconEIO.FILTER_STICKY);
    stickyB.setSelectedToolTip("Sticky Mode Enabled", "Selected items will only", "be sent to this or other", "Sticky outputs.");
    stickyB.setUnselectedToolTip("Sticky Mode Disabled");
    stickyB.setPaintSelectedBorder(false);

    y += 20;
    x = 112;

    channelB = new ColorButton(gui, ID_CHANNEL, x, y);
    channelB.setColorIndex(0);
    channelB.setToolTipHeading("Channel");

    x += 20;
    useNbtB = new ToggleButtonEIO(gui, ID_NBT, x, y, IconEIO.FILTER_NBT_OFF, IconEIO.FILTER_NBT);
    useNbtB.setSelectedToolTip("Match NBT Data");
    useNbtB.setUnselectedToolTip("Ignore NBT Data.");
    useNbtB.setPaintSelectedBorder(false);

    x += 20;
    useOreDictB = new ToggleButtonEIO(gui, ID_ORE_DICT, x, y, IconEIO.FILTER_ORE_DICT_OFF, IconEIO.FILTER_ORE_DICT);
    useOreDictB.setSelectedToolTip("Ore Dictionary Enabled.");
    useOreDictB.setUnselectedToolTip("Ore Dictionary Disabled.");
    useOreDictB.setPaintSelectedBorder(false);

    x += 20;
    y = customTop;
    loopB = new ToggleButtonEIO(gui, ID_LOOP, x, y, IconEIO.LOOP_OFF, IconEIO.LOOP);
    loopB.setSelectedToolTip("Self Feed Enabled");
    loopB.setUnselectedToolTip("Self Feed Disabled.");
    loopB.setPaintSelectedBorder(false);

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
      gui.container.setInputSlotsVisible(true);
      gui.container.setOutputSlotsVisible(false);
      gui.container.setInventorySlotsVisible(true);
    } else if(mode == ConnectionMode.OUTPUT) {
      showOutput = true;
      gui.container.setInputSlotsVisible(false);
      gui.container.setOutputSlotsVisible(true);
      gui.container.setInventorySlotsVisible(true);
    } else if(mode == ConnectionMode.IN_OUT) {

      if(nextFilterB != null) {
        nextFilterB.onGuiInit();
      }
      showInput = inOutShowIn;
      showOutput = !inOutShowIn;

    }

    if(!showInput && !showOutput) {
      activeFilter = null;
    } else {
      gui.container.setInventorySlotsVisible(true);
      if(showInput) {
        activeFilter = itemConduit.getInputFilter(gui.dir);
        gui.container.setInputSlotsVisible(true);
        gui.container.setOutputSlotsVisible(false);
      } else {
        activeFilter = itemConduit.getOutputFilter(gui.dir);
        gui.container.setInputSlotsVisible(false);
        gui.container.setOutputSlotsVisible(true);
      }
    }

    updateButtons();

  }

  private void updateButtons() {
    if(activeFilter == null || useNbtB == null) {
      return;
    }

    ConnectionMode mode = con.getConectionMode(gui.dir);
    if(mode == ConnectionMode.DISABLED) {
      return;
    }
    boolean outputActive = (mode == ConnectionMode.IN_OUT && !inOutShowIn) || (mode == ConnectionMode.OUTPUT);
    int chanCol;
    if(outputActive) {
      stickyB.onGuiInit();
      stickyB.setSelected(activeFilter.isSticky());

      chanCol = itemConduit.getOutputColor(gui.dir).ordinal();
    } else {

      rsB.onGuiInit();
      rsB.setMode(itemConduit.getExtractioRedstoneMode(gui.dir));

      colorB.onGuiInit();
      colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.dir).ordinal());

      chanCol = itemConduit.getInputColor(gui.dir).ordinal();
    }

    channelB.onGuiInit();
    channelB.setColorIndex(chanCol);

    if(isAdvanced) {
      useNbtB.onGuiInit();
      useNbtB.setSelected(activeFilter.isMatchNBT());

      useOreDictB.onGuiInit();
      useOreDictB.setSelected(activeFilter.isUseOreDict());
    }

    useMetaB.onGuiInit();
    useMetaB.setSelected(activeFilter.isMatchMeta());

    whiteListB.onGuiInit();
    if(activeFilter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip("Blacklist");
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip("Whitelist");
    }

    if(mode == ConnectionMode.IN_OUT) {
      loopB.onGuiInit();
      loopB.setSelected(itemConduit.isSelfFeedEnabled(gui.dir));
    }

  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if(guiButton.id == NEXT_FILTER_ID) {
      inOutShowIn = !inOutShowIn;
      updateGuiVisibility();
    }

    if(activeFilter == null) {
      return;
    }
    if(guiButton.id == ID_META) {
      activeFilter.setMatchMeta(useMetaB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_NBT) {
      activeFilter.setMatchNBT(useNbtB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_STICKY) {
      activeFilter.setSticky(stickyB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_ORE_DICT) {
      activeFilter.setUseOreDict(useOreDictB.isSelected());
      sendFilterChange();
    } else if(guiButton.id == ID_WHITELIST) {
      activeFilter.setBlacklist(!activeFilter.isBlacklist());
      sendFilterChange();
    } else if(guiButton.id == ID_COLOR_BUTTON) {
      Packet pkt = ConduitPacketHandler.createSignalColorPacket(itemConduit, gui.dir, DyeColor.values()[colorB.getColorIndex()]);
      PacketDispatcher.sendPacketToServer(pkt);
    } else if(guiButton.id == ID_LOOP) {
      itemConduit.setSelfFeedEnabled(gui.dir, !itemConduit.isSelfFeedEnabled(gui.dir));
      Packet pkt = ConduitPacketHandler.createItemLoopPacket(itemConduit, gui.dir);
      PacketDispatcher.sendPacketToServer(pkt);
    } else if(guiButton.id == ID_CHANNEL) {

      ConnectionMode mode = con.getConectionMode(gui.dir);
      if(mode == ConnectionMode.IN_OUT) {
        mode = inOutShowIn ? ConnectionMode.INPUT : ConnectionMode.OUTPUT;
      }

      DyeColor col = DyeColor.values()[channelB.getColorIndex()];
      boolean input;
      if(mode == ConnectionMode.INPUT) {
        col = DyeColor.values()[channelB.getColorIndex()];
        itemConduit.setInputColor(gui.dir, col);
        input = true;
      } else if(mode == ConnectionMode.OUTPUT) {
        itemConduit.setOutputColor(gui.dir, DyeColor.values()[channelB.getColorIndex()]);
        input = false;
      } else {
        return;
      }
      Packet pkt = ConduitPacketHandler.createItemChannelPacket(itemConduit, gui.dir, col, input);
      PacketDispatcher.sendPacketToServer(pkt);
    }
  }

  private void sendFilterChange() {
    updateButtons();

    if(activeFilter != null) {
      ConnectionMode mode = con.getConectionMode(gui.dir);
      boolean inputActive = (mode == ConnectionMode.IN_OUT && inOutShowIn) || (mode == ConnectionMode.INPUT);
      Packet pkt = ConduitPacketHandler.createItemFilterPacket(itemConduit, gui.dir, inputActive, activeFilter);
      PacketDispatcher.sendPacketToServer(pkt);
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
      if(itemConduit.getMetaData() == 0) {
        RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
      } else {
        RenderUtil.bindTexture("enderio:textures/gui/itemFilterAdvanced.png");
      }
      gui.drawTexturedModalRect(gui.getGuiLeft(), gui.getGuiTop() + 55, 0, 55, gui.getXSize(), 145);

      FontRenderer fr = gui.getFontRenderer();
      String heading = getHeading();
      int headingWidth = fr.getStringWidth(heading);
      int x = 0;
      int rgb = ColorUtil.getRGB(Color.darkGray);
      fr.drawString(heading, left + x, top, rgb);
    }

  }

  @Override
  public void deactivate() {
    gui.container.setInventorySlotsVisible(false);
    gui.container.setInputSlotsVisible(false);
    gui.container.setOutputSlotsVisible(false);
    rsB.detach();
    colorB.detach();
    channelB.detach();
    useNbtB.detach();
    useMetaB.detach();
    useOreDictB.detach();
    whiteListB.detach();
    stickyB.detach();
    loopB.detach();
    nextFilterB.detach();
  }

}
