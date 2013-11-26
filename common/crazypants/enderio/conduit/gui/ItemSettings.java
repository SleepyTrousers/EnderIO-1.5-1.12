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
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;

public class ItemSettings extends BaseSettingsPanel {

  private static final int NEXT_FILTER_ID = 98932;

  private static final int ID_WHITELIST = 17;
  private static final int ID_NBT = 18;
  private static final int ID_META = 19;
  private static final int ID_ORE_DICT = 20;
  private static final int ID_STICKY = 21;

  private IItemConduit itemConduit;

  private String inputHeading = "Extraction Filter";
  private String outputHeading = "Insertion Filter";

  private IconButtonEIO nextFilterB;

  private ToggleButtonEIO useMetaB;
  private ToggleButtonEIO useNbtB;
  private IconButtonEIO whiteListB;
  private ToggleButtonEIO useOreDictB;
  private ToggleButtonEIO stickyB;

  boolean inOutShowIn = false;

  boolean isAdvanced;

  private ItemFilter activeFilter;

  protected ItemSettings(GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, ModObject.itemItemConduit.name, gui, con);
    itemConduit = (IItemConduit) con;
    isAdvanced = itemConduit.getMetaData() == 1;
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

    int y = customTop;
    int x;

    int headingWidth = gui.getFontRenderer().getStringWidth(getHeading());
    x = ((width - headingWidth) / 2) + headingWidth + 16;
    nextFilterB = new IconButtonEIO(gui, NEXT_FILTER_ID, x, y, IconEIO.RIGHT_ARROW);
    nextFilterB.setSize(8, 16);

    x = 112;
    y = 66;

    whiteListB = new IconButtonEIO(gui, ID_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
    whiteListB.setToolTip("Whitelist");

    x += 20;
    useMetaB = new ToggleButtonEIO(gui, ID_META, x, y, IconEIO.FILTER_META, IconEIO.FILTER_META);
    useMetaB.setToolTip("Match Meta Data");

    x += 20;
    stickyB = new ToggleButtonEIO(gui, ID_STICKY, x, y, IconEIO.FILTER_STICKY, IconEIO.FILTER_STICKY);
    stickyB.setToolTip("Sticky Mode", "Reserve Matched Items");

    y += 20;
    x = 112;

    useNbtB = new ToggleButtonEIO(gui, ID_NBT, x, y, IconEIO.FILTER_NBT, IconEIO.FILTER_NBT);
    useNbtB.setToolTip("Match NBT Data");

    x += 20;
    useOreDictB = new ToggleButtonEIO(gui, ID_ORE_DICT, x, y, IconEIO.FILTER_ORE_DICT, IconEIO.FILTER_ORE_DICT);
    useOreDictB.setToolTip("Use Ore Dictionary");

    if(isAdvanced) {
      useNbtB.onGuiInit();
      useOreDictB.onGuiInit();
    }
    //TODOO: Sticky mode not implemented
    //stickyB.onGuiInit();
    useMetaB.onGuiInit();
    whiteListB.onGuiInit();

    updateGuiVisibility();
  }

  private void updateGuiVisibility() {
    gui.removeButton(nextFilterB);
    gui.removeButton(stickyB);

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
      deactivate();
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
    boolean outputActive = (mode == ConnectionMode.IN_OUT && !inOutShowIn) || (mode == ConnectionMode.OUTPUT);
    if(outputActive) {
      //TODO: Sticky mode not implemented
      //stickyB.onGuiInit();
    }

    if(isAdvanced) {
      useNbtB.onGuiInit();
      useOreDictB.onGuiInit();
    }
    useMetaB.onGuiInit();
    whiteListB.onGuiInit();

    useNbtB.setSelected(activeFilter.isMatchNBT());
    useMetaB.setSelected(activeFilter.isMatchMeta());
    stickyB.setSelected(activeFilter.isSticky());
    useOreDictB.setSelected(activeFilter.isUseOreDict());

    if(activeFilter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip("Blacklist");
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip("Whitelist");
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
      int x = (width - headingWidth) / 2;
      int rgb = ColorUtil.getRGB(Color.darkGray);
      fr.drawString(heading, left + x, top, rgb);
    }

  }

  @Override
  public void deactivate() {
    gui.container.setInventorySlotsVisible(false);
    gui.container.setInputSlotsVisible(false);
    gui.container.setOutputSlotsVisible(false);
    useNbtB.detach();
    useMetaB.detach();
    useOreDictB.detach();
    whiteListB.detach();
    stickyB.detach();
  }

}
