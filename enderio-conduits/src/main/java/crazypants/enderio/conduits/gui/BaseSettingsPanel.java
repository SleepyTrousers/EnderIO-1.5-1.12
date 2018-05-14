package crazypants.enderio.conduits.gui;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CheckBox;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.conduit.IFilterChangeListener;
import crazypants.enderio.base.conduit.IGuiExternalConnection;
import crazypants.enderio.base.filter.network.IOpenFilterRemoteExec;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.network.PacketConnectionMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class BaseSettingsPanel extends Gui implements ITabPanel, IOpenFilterRemoteExec.GUI {

  static final int ID_INSERT_ENABLED = 327;
  static final int ID_EXTRACT_ENABLED = 328;
  protected static final int ID_INSERT_FILTER_OPTIONS = 329;
  protected static final int ID_EXTRACT_FILTER_OPTIONS = 330;

  protected final @Nonnull IWidgetIcon icon;
  protected final @Nonnull IGuiExternalConnection gui;
  protected @Nonnull IClientConduit con;
  protected final @Nonnull String typeName;
  protected final @Nonnull ResourceLocation texture;

  protected ConnectionMode oldConnectionMode;

  private @Nonnull String inputHeading;
  private @Nonnull String outputHeading;

  private boolean insertEnabled = false;
  private boolean extractEnabled = false;

  private final @Nonnull CheckBox extractEnabledB;
  private final @Nonnull CheckBox insertEnabledB;

  private @Nonnull IconButton insertFilterOptionsB;
  private @Nonnull IconButton extractFilterOptionsB;

  protected int left = 0;
  protected int top = 0;
  protected int width = 0;
  protected int height = 0;
  protected int rightColumn = 112;
  protected int leftColumn = 22;

  protected int gap = 5;

  protected int customTop = 0;

  protected BaseSettingsPanel(@Nonnull IWidgetIcon icon, @Nonnull String typeName, @Nonnull IGuiExternalConnection gui, @Nonnull IClientConduit con,
      @Nonnull String texture) {
    this.icon = icon;
    this.typeName = typeName;
    this.gui = gui;
    this.con = con;
    this.texture = EnderIO.proxy.getGuiTexture(texture);

    inputHeading = getInputHeading();
    outputHeading = getOutputHeading();

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    customTop = top + gap * 5 + fr.FONT_HEIGHT * 2;
    customTop -= 16;

    int x = leftColumn;
    int y = 6;

    insertEnabledB = new CheckBox(gui, ID_INSERT_ENABLED, x, y);

    x = rightColumn;

    extractEnabledB = new CheckBox(gui, ID_EXTRACT_ENABLED, x, y);

    x = leftColumn;
    y = 92;

    insertFilterOptionsB = new IconButton(gui, ID_INSERT_FILTER_OPTIONS, x, y, IconEIO.GEAR_LIGHT);
    insertFilterOptionsB.setToolTip(crazypants.enderio.base.lang.Lang.GUI_EDIT_ITEM_FILTER.get());

    x = rightColumn;

    extractFilterOptionsB = new IconButton(gui, ID_EXTRACT_FILTER_OPTIONS, x, y, IconEIO.GEAR_LIGHT);
    extractFilterOptionsB.setToolTip(crazypants.enderio.base.lang.Lang.GUI_EDIT_ITEM_FILTER.get());

    if (hasFilters()) {
      gui.getContainer().addFilterListener(new IFilterChangeListener() {
        @Override
        public void onFilterChanged() {
          filtersChanged();
        }
      });
    }

    gui.getContainer().setInOutSlotsVisible(false, false, con);

  }

  protected void filtersChanged() {
    insertFilterOptionsB.onGuiInit();
    extractFilterOptionsB.onGuiInit();

    if (gui.getContainer().hasFilter(true) && hasFilterGui(true)) {
      insertFilterOptionsB.setIsVisible(true);
    } else {
      insertFilterOptionsB.setIsVisible(false);
    }

    if (gui.getContainer().hasFilter(false) && hasFilterGui(false)) {
      extractFilterOptionsB.setIsVisible(true);
    } else {
      extractFilterOptionsB.setIsVisible(false);
    }
  }

  protected boolean hasFilterGui(boolean input) {
    return true;
  }

  public boolean updateConduit(@Nonnull IClientConduit conduit) {
    this.con = conduit;
    if (oldConnectionMode != con.getConnectionMode(gui.getDir())) {
      connectionModeChanged(con.getConnectionMode(gui.getDir()));
    }
    return true;
  }

  @Override
  public void onGuiInit(int leftIn, int topIn, int widthIn, int heightIn) {
    this.left = leftIn;
    this.top = topIn;
    this.width = widthIn;
    this.height = heightIn;

    updateConduit(con);

    insertEnabledB.onGuiInit();
    extractEnabledB.onGuiInit();

    insertEnabledB.setSelected(insertEnabled);
    extractEnabledB.setSelected(extractEnabled);

    initCustomOptions();
  }

  protected void initCustomOptions() {
  }

  @Override
  public void deactivate() {
    insertEnabledB.detach();
    extractEnabledB.detach();
    insertFilterOptionsB.detach();
    extractFilterOptionsB.detach();
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {
  }

  @Override
  public void keyTyped(char par1, int par2) {
  }

  @Override
  public void updateScreen() {
  }

  @Override
  @Nonnull
  public IWidgetIcon getIcon() {
    return icon;
  }

  @Override
  @Nonnull
  public ResourceLocation getTexture() {
    return texture;
  }

  private void updateConnectionMode() {
    ConnectionMode mode = ConnectionMode.DISABLED;
    if (insertEnabled && extractEnabled) {
      mode = ConnectionMode.IN_OUT;
    } else if (insertEnabled) {
      mode = ConnectionMode.OUTPUT;
    } else if (extractEnabled) {
      mode = ConnectionMode.INPUT;
    }
    PacketHandler.INSTANCE.sendToServer(new PacketConnectionMode(con, gui.getDir(), mode));
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    if (guiButton.id == ID_INSERT_ENABLED) {
      insertEnabled = !insertEnabled;
      updateConnectionMode();
    } else if (guiButton.id == ID_EXTRACT_ENABLED) {
      extractEnabled = !extractEnabled;
      updateConnectionMode();
    }
  }

  protected void connectionModeChanged(@Nonnull ConnectionMode mode) {
    oldConnectionMode = mode;
    insertEnabled = mode.acceptsOutput();
    extractEnabled = mode.acceptsInput();
  }

  @Override
  public void render(float par1, int par2, int par3) {
    FontRenderer fr = gui.getFontRenderer();

    int rgb = ColorUtil.getRGB(Color.darkGray);
    int x = left + 32;
    int y = gui.getGuiTop() + 10;
    fr.drawString(inputHeading, x, y, rgb);
    x += 92;
    fr.drawString(outputHeading, x, y, rgb);
    renderCustomOptions(y + gap + fr.FONT_HEIGHT + gap, par1, par2, par3);
  }

  protected void renderCustomOptions(int topIn, float par1, int par2, int par3) {

  }

  protected @Nonnull String getTypeName() {
    return typeName;
  }

  protected boolean hasFilters() {
    return false;
  }

  @Override
  public void setGuiID(int id) {
    gui.setGuiID(id);
  }

  @Override
  public int getGuiID() {
    return gui.getGuiID();
  }

  @Nonnull
  protected String getInputHeading() {
    return Lang.GUI_CONDUIT_INSERT_MODE.get();
  }

  @Nonnull
  protected String getOutputHeading() {
    return Lang.GUI_CONDUIT_EXTRACT_MODE.get();
  }

}
