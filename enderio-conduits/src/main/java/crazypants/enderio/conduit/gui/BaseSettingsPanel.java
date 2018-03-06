package crazypants.enderio.conduit.gui;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CheckBox;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IClientConduit;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduit.lang.Lang;
import crazypants.enderio.conduit.packet.PacketConnectionMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class BaseSettingsPanel extends Gui implements ITabPanel {

  static final int ID_INSERT_ENABLED = 327;
  static final int ID_EXTRACT_ENABLED = 328;

  private static final String ENABLED = Lang.GUI_CONDUIT_ENABLED_MODE.get();
  private static final String DISABLED = Lang.GUI_CONDUIT_DISABLED_MODE.get();

  protected final @Nonnull IconEIO icon;
  protected final GuiExternalConnection gui;
  protected IClientConduit con;
  protected final String typeName;
  protected final @Nonnull ResourceLocation texture;

  protected ConnectionMode oldConnectionMode;

  private String inputHeading;
  private String outputHeading;

  private boolean insertEnabled = false;
  private boolean extractEnabled = false;

  private final CheckBox extractEnabledB;
  private final CheckBox insertEnabledB;

  private final boolean hasInputOutputMode;

  protected int left = 0;
  protected int top = 0;
  protected int width = 0;
  protected int height = 0;
  protected int rightColumn = 112;
  protected int leftColumn = 22;

  protected int gap = 5;

  protected int customTop = 0;

  protected BaseSettingsPanel(@Nonnull IconEIO icon, String typeName, @Nonnull GuiExternalConnection gui, @Nonnull IClientConduit con,
      @Nonnull String texture) {
    this(icon, typeName, gui, con, texture, true);
  }

  protected BaseSettingsPanel(@Nonnull IconEIO icon, String typeName, @Nonnull GuiExternalConnection gui, @Nonnull IClientConduit con, @Nonnull String texture,
      boolean hasInputOutputMode) {
    this.icon = icon;
    this.typeName = typeName;
    this.gui = gui;
    this.con = con;
    this.texture = EnderIO.proxy.getGuiTexture(texture);
    this.hasInputOutputMode = hasInputOutputMode;

    if (hasInputOutputMode) {
      inputHeading = Lang.GUI_CONDUIT_INSERT_MODE.get();
    } else {
      inputHeading = ENABLED;
    }
    outputHeading = Lang.GUI_CONDUIT_EXTRACT_MODE.get();

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    customTop = top + gap * 5 + fr.FONT_HEIGHT * 2;
    customTop -= 16;

    int x = leftColumn;
    int y = 6;

    insertEnabledB = new CheckBox(gui, ID_INSERT_ENABLED, x, y);

    x = rightColumn;

    extractEnabledB = new CheckBox(gui, ID_EXTRACT_ENABLED, x, y);

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

    insertEnabledB.onGuiInit();
    if (hasInputOutputMode) {
      extractEnabledB.onGuiInit();
    }

    ConnectionMode mode = con.getConnectionMode(gui.getDir());
    switch (mode) {
    case IN_OUT:
      insertEnabled = true;
      extractEnabled = true;
      break;
    case INPUT:
      insertEnabled = false;
      extractEnabled = true;
      break;
    case OUTPUT:
      insertEnabled = true;
      extractEnabled = false;
      break;
    case DISABLED:
      insertEnabled = false;
      extractEnabled = false;
      break;
    default:
      break;
    }

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
      if (!hasInputOutputMode) {
        extractEnabled = !extractEnabled;
        swapEnabledText();
      }
      updateConnectionMode();
    } else if (guiButton.id == ID_EXTRACT_ENABLED) {
      extractEnabled = !extractEnabled;
      updateConnectionMode();
    }
  }

  private void swapEnabledText() {
    if (inputHeading.equals(ENABLED)) {
      inputHeading = DISABLED;
    } else {
      inputHeading = ENABLED;
    }
  }

  protected void connectionModeChanged(@Nonnull ConnectionMode mode) {
    oldConnectionMode = mode;
  }

  @Override
  public void render(float par1, int par2, int par3) {
    FontRenderer fr = gui.getFontRenderer();

    int rgb = ColorUtil.getRGB(Color.darkGray);
    int x = left + 32;
    int y = gui.getGuiTop() + 10;
    fr.drawString(inputHeading, x, y, rgb);

    if (hasInputOutputMode) {
      x += 92;
      fr.drawString(outputHeading, x, y, rgb);
    }
    renderCustomOptions(y + gap + fr.FONT_HEIGHT + gap, par1, par2, par3);
  }

  protected void renderCustomOptions(int topIn, float par1, int par2, int par3) {

  }

  protected String getTypeName() {
    return typeName;
  }

}
