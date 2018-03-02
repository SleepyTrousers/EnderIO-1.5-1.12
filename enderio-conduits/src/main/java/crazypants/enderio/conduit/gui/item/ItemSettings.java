package crazypants.enderio.conduit.gui.item;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.handlers.SpecialTooltipHandler;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.EnderWidget;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IFilterChangeListener;
import crazypants.enderio.base.filter.gui.FilterGuiUtil;
import crazypants.enderio.base.filter.gui.IOpenFilterRemoteExec;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.machine.interfaces.IRedstoneModeControlable;
import crazypants.enderio.base.machine.modes.RedstoneControlMode;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.conduit.gui.BaseSettingsPanel;
import crazypants.enderio.conduit.gui.GuiExternalConnection;
import crazypants.enderio.conduit.item.FunctionUpgrade;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class ItemSettings extends BaseSettingsPanel implements IOpenFilterRemoteExec.GUI {

  private static final int ID_REDSTONE_BUTTON = 12614;
  private static final int ID_COLOR_BUTTON = 179816;
  private static final int ID_LOOP = 22;
  private static final int ID_ROUND_ROBIN = 24;
  private static final int ID_PRIORITY_UP = 25;
  private static final int ID_PRIORITY_DOWN = 26;
  private static final int ID_INSERT_CHANNEL = 23;
  private static final int ID_EXTRACT_CHANNEL = 27;
  private static final int ID_INSERT_FILTER_OPTIONS = 28;
  private static final int ID_EXTRACT_FILTER_OPTIONS = 29;

  private IItemConduit itemConduit;

  private final ToggleButton loopB;
  private final ToggleButton roundRobinB;

  private final MultiIconButton priUpB;
  private final MultiIconButton priDownB;

  private final RedstoneModeButton<?> rsB;
  private final ColorButton colorB;

  private ColorButton insertChannelB;
  private ColorButton extractChannelB;

  private IconButton insertFilterOptionsB;
  private IconButton extractFilterOptionsB;

  private int priLeft = 46;
  private int priWidth = 32;

  private final GuiToolTip priorityTooltip;
  private final GuiToolTip speedUpgradeTooltip;
  private final GuiToolTip functionUpgradeTooltip;
  private final GuiToolTip filterUpgradeTooltip;

  public ItemSettings(@Nonnull final GuiExternalConnection gui, @Nonnull IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_ITEM, EnderIO.lang.localize("itemItemConduit.name"), gui, con, "item_settings");
    itemConduit = (IItemConduit) con;

    // TODO Lang

    int x = leftColumn;
    int y = customTop;

    insertChannelB = new ColorButton(gui, ID_INSERT_CHANNEL, x, y);
    insertChannelB.setColorIndex(0);
    insertChannelB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.item.channel"));

    x = rightColumn;
    extractChannelB = new ColorButton(gui, ID_EXTRACT_CHANNEL, x, y);
    extractChannelB.setColorIndex(0);
    extractChannelB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.item.channel"));

    x += 4 + extractChannelB.getWidth();
    roundRobinB = new ToggleButton(gui, ID_ROUND_ROBIN, x, y, IconEIO.ROUND_ROBIN_OFF, IconEIO.ROUND_ROBIN);
    roundRobinB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.roundRobinEnabled"));
    roundRobinB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.roundRobinDisabled"));
    roundRobinB.setPaintSelectedBorder(false);

    x += 4 + roundRobinB.getWidth();
    loopB = new ToggleButton(gui, ID_LOOP, x, y, IconEIO.LOOP_OFF, IconEIO.LOOP);
    loopB.setSelectedToolTip(EnderIO.lang.localize("gui.conduit.item.selfFeedEnabled"));
    loopB.setUnselectedToolTip(EnderIO.lang.localize("gui.conduit.item.selfFeedDisabled"));
    loopB.setPaintSelectedBorder(false);

    filterUpgradeTooltip = new GuiToolTip(new Rectangle(x - 21 - 18 * 2, customTop + 3 + 16, 18, 18), EnderIO.lang.localize("gui.conduit.item.filterupgrade")) {
      @Override
      public boolean shouldDraw() {
        // TODO remove redudant code
        return !gui.getContainer().hasFilter(true) && super.shouldDraw();
      }
    };
    speedUpgradeTooltip = new GuiToolTip(new Rectangle(x - 21 - 18, customTop + 3 + 16, 18, 18), EnderIO.lang.localize("gui.conduit.item.speedupgrade"),
        EnderIO.lang.localize("gui.conduit.item.speedupgrade2")) {
      @Override
      public boolean shouldDraw() {
        return !gui.getContainer().hasSpeedUpgrades() && super.shouldDraw();
      }
    };

    ArrayList<String> list = new ArrayList<String>();
    SpecialTooltipHandler.addTooltipFromResources(list, "enderio.gui.conduit.item.functionupgrade.line");
    for (FunctionUpgrade upgrade : FunctionUpgrade.values()) {
      list.add(EnderIO.lang.localizeExact(upgrade.unlocName.concat(".name")));
    }
    functionUpgradeTooltip = new GuiToolTip(new Rectangle(x - 21 - 18 * 2, customTop + 3 + 34, 18, 18), list) {
      @Override
      public boolean shouldDraw() {
        return !gui.getContainer().hasFunctionUpgrade() && super.shouldDraw();
      }
    };

    y += insertChannelB.getHeight() + 6;
    x = rightColumn;

    rsB = new RedstoneModeButton<>(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(@Nonnull RedstoneControlMode mode) {
        RedstoneControlMode curMode = getRedstoneControlMode();
        itemConduit.setExtractionRedstoneMode(mode, gui.getDir());
        if (mode == RedstoneControlMode.OFF || mode == RedstoneControlMode.ON) {
          colorB.onGuiInit();
          colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.getDir()).ordinal());
          colorB.setIsVisible(true);
        } else {
          colorB.setIsVisible(false);
        }
        if (curMode != mode) {
          PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
        }

      }

      @Override
      @Nonnull
      public RedstoneControlMode getRedstoneControlMode() {
        return itemConduit.getExtractionRedstoneMode(gui.getDir());
      }

      @Override
      public boolean getRedstoneControlStatus() {
        return false;
      }
    });

    x += rsB.getWidth() + 4;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    colorB.setColorIndex(itemConduit.getExtractionSignalColor(gui.getDir()).ordinal());
    colorB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.item.sigCol"));

    priorityTooltip = new GuiToolTip(new Rectangle(priLeft + 9, y, priWidth, 16), EnderIO.lang.localize("gui.conduit.item.priority"));

    x = priLeft + priWidth + 9;
    priUpB = MultiIconButton.createAddButton(gui, ID_PRIORITY_UP, x, y);
    priDownB = MultiIconButton.createMinusButton(gui, ID_PRIORITY_DOWN, x, y + 8);

    gui.getContainer().addFilterListener(new IFilterChangeListener() {
      @Override
      public void onFilterChanged() {
        filtersChanged();
      }
    });

    x = leftColumn;
    y = 92;

    // TODO use a better icon
    insertFilterOptionsB = new IconButton(gui, ID_INSERT_FILTER_OPTIONS, x, y, IconEIO.IO_WHATSIT);

    x = rightColumn;

    extractFilterOptionsB = new IconButton(gui, ID_EXTRACT_FILTER_OPTIONS, x, y, IconEIO.IO_WHATSIT);

  }

  @Override
  protected void initCustomOptions() {
    updateGuiVisibility();
  }

  private void updateGuiVisibility() {
    deactivate();
    updateButtons();
  }

  private void filtersChanged() {
    insertFilterOptionsB.onGuiInit();
    extractFilterOptionsB.onGuiInit();

    if (gui.getContainer().hasFilter(true)) {
      insertFilterOptionsB.setIsVisible(true);
    } else {
      insertFilterOptionsB.setIsVisible(false);
    }

    if (gui.getContainer().hasFilter(false)) {
      extractFilterOptionsB.setIsVisible(true);
    } else {
      extractFilterOptionsB.setIsVisible(false);
    }
  }

  private void updateButtons() {
    gui.getContainer().setInOutSlotsVisible(true, true);
    rsB.onGuiInit();
    rsB.setMode(RedstoneControlMode.IconHolder.getFromMode(itemConduit.getExtractionRedstoneMode(gui.getDir())));

    gui.addToolTip(filterUpgradeTooltip);
    gui.addToolTip(functionUpgradeTooltip);
    loopB.onGuiInit();
    loopB.setSelected(itemConduit.isSelfFeedEnabled(gui.getDir()));
    roundRobinB.onGuiInit();
    roundRobinB.setSelected(itemConduit.isRoundRobinEnabled(gui.getDir()));
    gui.addToolTip(speedUpgradeTooltip);

    priUpB.onGuiInit();
    priDownB.onGuiInit();
    gui.addToolTip(priorityTooltip);

    insertChannelB.onGuiInit();
    insertChannelB.setColorIndex(itemConduit.getOutputColor(gui.getDir()).ordinal());
    extractChannelB.onGuiInit();
    extractChannelB.setColorIndex(itemConduit.getInputColor(gui.getDir()).ordinal());
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if (guiButton.id == ID_COLOR_BUTTON) {
      itemConduit.setExtractionSignalColor(gui.getDir(), DyeColor.values()[colorB.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
    } else if (guiButton.id == ID_LOOP) {
      itemConduit.setSelfFeedEnabled(gui.getDir(), !itemConduit.isSelfFeedEnabled(gui.getDir()));
    } else if (guiButton.id == ID_ROUND_ROBIN) {
      itemConduit.setRoundRobinEnabled(gui.getDir(), !itemConduit.isRoundRobinEnabled(gui.getDir()));
    } else if (guiButton.id == ID_PRIORITY_UP) {
      itemConduit.setOutputPriority(gui.getDir(), itemConduit.getOutputPriority(gui.getDir()) + 1);
    } else if (guiButton.id == ID_PRIORITY_DOWN) {
      itemConduit.setOutputPriority(gui.getDir(), itemConduit.getOutputPriority(gui.getDir()) - 1);
    } else if (guiButton.id == ID_INSERT_CHANNEL) {
      DyeColor col = DyeColor.values()[insertChannelB.getColorIndex()];
      itemConduit.setOutputColor(gui.getDir(), col);
    } else if (guiButton.id == ID_EXTRACT_CHANNEL) {
      DyeColor col = DyeColor.values()[extractChannelB.getColorIndex()];
      itemConduit.setInputColor(gui.getDir(), col);
    } else if (guiButton.id == ID_INSERT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_INPUT);
    } else if (guiButton.id == ID_EXTRACT_FILTER_OPTIONS) {
      doOpenFilterGui(FilterGuiUtil.INDEX_OUTPUT);
    }
  }

  @Override
  protected void connectionModeChanged(@Nonnull ConnectionMode mode) {
    super.connectionModeChanged(mode);
    PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(itemConduit, gui.getDir()));
    updateGuiVisibility();
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    ConnectionMode mode = con.getConnectionMode(gui.getDir());

    FontRenderer fr = gui.getFontRenderer();
    int x = 0;
    int rgb = ColorUtil.getRGB(Color.darkGray);

    GL11.glColor3f(1, 1, 1);
    IconEIO.map.render(EnderWidget.BUTTON_DOWN, left + priLeft, top - 5, priWidth, 16, 0, true);
    String str = itemConduit.getOutputPriority(gui.getDir()) + "";
    int sw = fr.getStringWidth(str);

    // TODO Lang
    String priority = "Priority";
    fr.drawString(priority, left + 12, top + 25, ColorUtil.getRGB(Color.black));
    fr.drawString(str, left + priLeft + priWidth - sw - gap, top + 25, ColorUtil.getRGB(Color.black));
  }

  @Override
  public void deactivate() {
    gui.getContainer().setInOutSlotsVisible(false, false);
    rsB.detach();
    colorB.detach();
    roundRobinB.detach();
    loopB.detach();
    priUpB.detach();
    priDownB.detach();
    gui.removeToolTip(priorityTooltip);
    gui.removeToolTip(speedUpgradeTooltip);
    gui.removeToolTip(functionUpgradeTooltip);
    gui.removeToolTip(filterUpgradeTooltip);
    insertChannelB.detach();
    extractChannelB.detach();
    insertFilterOptionsB.detach();
    extractFilterOptionsB.detach();
  }

  @Override
  public void setGuiID(int id) {
    gui.setGuiID(id);
  }

  @Override
  public int getGuiID() {
    return gui.getGuiID();
  }

}
