package crazypants.enderio.machine.monitor;

import java.awt.Color;
import java.awt.Rectangle;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.CheckBox;
import com.enderio.core.client.gui.button.InvisibleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.gui.widget.TextFieldEnder;
import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.BlockItemCapBank;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.monitor.TilePowerMonitor.StatData;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.ModObject.blockAlloySmelter;
import static crazypants.enderio.ModObject.blockCapBank;
import static crazypants.enderio.ModObject.blockPowerMonitor;
import static crazypants.enderio.ModObject.itemPowerConduit;
import static crazypants.enderio.machine.power.PowerDisplayUtil.formatPower;
import static crazypants.enderio.machine.power.PowerDisplayUtil.formatPowerFloat;

public class GuiPowerMonitor extends GuiPoweredMachineBase<TilePowerMonitor> {

  private static enum Tab {
    GRAPH(0, new ItemStack(BlockPowerMonitor.advancedInstance)),
    STAT(1, new ItemStack(blockPowerMonitor.getBlock())),
    CONTROL(2, new ItemStack(Items.REDSTONE));

    int tabNo;
    @Nonnull
    ItemStack itemStack;
    InvisibleButton button;

    private Tab(int tabNo, @Nonnull ItemStack itemStack) {
      this.tabNo = tabNo;
      this.itemStack = itemStack;
    }

  }

  private static final NumberFormat INT_NF = NumberFormat.getIntegerInstance();

  protected Tab tab = Tab.GRAPH;

  protected int timebase = 2;
  protected int timebaseOffset = 0;
  protected InvisibleButton plus;
  protected InvisibleButton minus;

  private CheckBox engineControlEnabled;
  private TextFieldEnder engineControlStart;
  private TextFieldEnder engineControlStop;
  private boolean engineControlEnabled_value;
  private String engineControlStart_value = null;
  private String engineControlStop_value;

  private GuiToolTip tooltipConduitStorage, tooltipCapacitorBankStorage, tooltipMachineBuffers, tooltipAverageOutput, tooltipAverageInput;

  public GuiPowerMonitor(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TilePowerMonitor te) {
    super(te, new ContainerPowerMonitor(par1InventoryPlayer, te), "pmon", "pmon2");

    plus = new InvisibleButton(this, 1, 154, 28);
    plus.setToolTip("+");
    minus = new InvisibleButton(this, 2, 154, 52);
    minus.setToolTip("-");

    for (Tab drawTab : Tab.values()) {
      drawTab.button = new InvisibleButton(this, 3, 0, 0);
    }

    if (!te.isAdvanced()) {
      tab = Tab.STAT;
    }

    engineControlEnabled = new CheckBox(this, 4, 0, 0);
    engineControlEnabled.setSelectedToolTip(EnderIO.lang.localize("gui.enabled"));
    engineControlEnabled.setUnselectedToolTip(EnderIO.lang.localize("gui.disabled"));

    engineControlStart = new TextFieldEnder(getFontRenderer(), 0, 0, 28, 14);
    engineControlStart.setCanLoseFocus(true);
    engineControlStart.setMaxStringLength(3);
    textFields.add(engineControlStart);

    engineControlStop = new TextFieldEnder(getFontRenderer(), 0, 0, 28, 14);
    engineControlStop.setCanLoseFocus(true);
    engineControlStop.setMaxStringLength(3);
    textFields.add(engineControlStop);

    addToolTip(tooltipConduitStorage = new GuiToolTip(new Rectangle(0, 0, 0, 0), EnderIO.lang.localize("gui.powerMonitor.monHeading1")));
    addToolTip(tooltipCapacitorBankStorage = new GuiToolTip(new Rectangle(0, 0, 0, 0), EnderIO.lang.localize("gui.powerMonitor.monHeading2")));
    addToolTip(tooltipMachineBuffers = new GuiToolTip(new Rectangle(0, 0, 0, 0), EnderIO.lang.localize("gui.powerMonitor.monHeading3")));
    addToolTip(tooltipAverageOutput = new GuiToolTip(new Rectangle(0, 0, 0, 0), EnderIO.lang.localize("gui.powerMonitor.monHeading4")));
    addToolTip(tooltipAverageInput = new GuiToolTip(new Rectangle(0, 0, 0, 0), EnderIO.lang.localize("gui.powerMonitor.monHeading5")));
  }

  @Override
  public void initGui() {
    super.initGui();
    redstoneButton.visible = false;
    configB.visible = false;
    plus.onGuiInit();
    minus.onGuiInit();

    for (Tab drawTab : Tab.values()) {
      drawTab.button.onGuiInit();
    }

    engineControlEnabled.onGuiInit();
  }

  protected void updateVisibility() {
    switch (tab) {
    case GRAPH:
      if (!getTileEntity().isAdvanced()) {
        tab = Tab.STAT;
        updateVisibility();
        return;
      }
      plus.enabled = timebase < 6;
      minus.enabled = timebase > 0;
      engineControlEnabled.visible = false;
      engineControlStart.setVisible(false);
      engineControlStop.setVisible(false);
      tooltipConduitStorage.setIsVisible(false);
      tooltipCapacitorBankStorage.setIsVisible(false);
      tooltipMachineBuffers.setIsVisible(false);
      tooltipAverageOutput.setIsVisible(false);
      tooltipAverageInput.setIsVisible(false);
      break;
    case STAT:
      plus.enabled = minus.enabled = false;
      engineControlEnabled.visible = false;
      engineControlStart.setVisible(false);
      engineControlStop.setVisible(false);
      tooltipConduitStorage.setIsVisible(true);
      tooltipCapacitorBankStorage.setIsVisible(true);
      tooltipMachineBuffers.setIsVisible(true);
      tooltipAverageOutput.setIsVisible(true);
      tooltipAverageInput.setIsVisible(true);
      break;
    case CONTROL:
      plus.enabled = minus.enabled = false;
      engineControlEnabled.visible = true;
      engineControlStart.setVisible(true);
      engineControlStop.setVisible(true);
      tooltipConduitStorage.setIsVisible(false);
      tooltipCapacitorBankStorage.setIsVisible(false);
      tooltipMachineBuffers.setIsVisible(false);
      tooltipAverageOutput.setIsVisible(false);
      tooltipAverageInput.setIsVisible(false);

      if (engineControlStart_value == null) {
        engineControlEnabled.setSelected(engineControlEnabled_value = getTileEntity().isEngineControlEnabled());
        engineControlStart.setText(engineControlStart_value = INT_NF.format(getTileEntity().getStartLevel() * 100));
        engineControlStop.setText(engineControlStop_value = INT_NF.format(getTileEntity().getStopLevel() * 100));
      }

      if (engineControlEnabled_value != engineControlEnabled.isSelected() || !engineControlStart_value.equals(engineControlStart.getText())
          || !engineControlStop_value.equals(engineControlStop.getText())) {
        PacketHandler.INSTANCE.sendToServer(new PacketPowerMonitorConfig(getTileEntity(), engineControlEnabled.isSelected(), getInt(engineControlStart) / 100f,
            getInt(engineControlStop) / 100f));
        getTileEntity().setEngineControlEnabled(engineControlEnabled.isSelected());
        getTileEntity().setStartLevel(getInt(engineControlStart) / 100f);
        getTileEntity().setStopLevel(getInt(engineControlStop) / 100f);
      }

      if (engineControlEnabled_value != getTileEntity().isEngineControlEnabled()
          || !engineControlStart_value.equals(INT_NF.format(getTileEntity().getStartLevel() * 100))
          || !engineControlStop_value.equals(INT_NF.format(getTileEntity().getStopLevel() * 100))) {
        engineControlEnabled.setSelected(engineControlEnabled_value = getTileEntity().isEngineControlEnabled());
        engineControlStart.setText(engineControlStart_value = INT_NF.format(getTileEntity().getStartLevel() * 100));
        engineControlStop.setText(engineControlStop_value = INT_NF.format(getTileEntity().getStopLevel() * 100));
      }

      break;
    }
    for (Tab drawTab : Tab.values()) {
      drawTab.button.enabled = drawTab != tab;
    }
  }

  @Override
  protected void actionPerformed(GuiButton btn) {
    if (btn == plus) {
      if (timebase >= 6) {
        return;
      }
      timebase++;
      timebaseOffset -= 16;
    } else if (btn == minus) {
      if (timebase <= 0) {
        return;
      }
      timebase--;
      timebaseOffset += 16;
    } else {
      for (Tab drawTab : Tab.values()) {
        if (btn == drawTab.button) {
          tab = drawTab;
          return;
        }
      }
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected int getPowerX() {
    return 8;
  }

  @Override
  protected int getPowerY() {
    return 10;
  }

  @Override
  protected int getPowerWidth() {
    return 4;
  }

  @Override
  protected int getPowerHeight() {
    return 66;
  }

  private long lastTick = 0;

  private void drawTimebase(int x, int y) {
    int u = 200, v = timebase * 16 + timebaseOffset, w = 18, h = 16;
    if (v < 0) {
      v = 0;
    } else if (v > 6 * 16) {
      v = 6 * 16;
    }
    drawTexturedModalRect(x, y, u, v, w, h);
    if (lastTick != EnderIO.proxy.getTickCount()) {
      lastTick = EnderIO.proxy.getTickCount();
      if (timebaseOffset < 0) {
        timebaseOffset += 1 - timebaseOffset / 8;
      } else if (timebaseOffset > 0) {
        timebaseOffset -= 1 + timebaseOffset / 8;
      }
    }
  }

  private void drawGraph(int x, int y) {
    StatCollector stat = getTileEntity().getStatCollector(timebase);
    int[][] values = stat.getValues();
    for (int i = 0; i < StatCollector.MAX_VALUES; i++) {
      int min = values[0][i], max = values[1][i];
      drawTexturedModalRect(x + i, y + 63 - max, 220, 63 - max, 1, max - min + 1);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    updateVisibility();

    switch (tab) {
    case GRAPH:
      bindGuiTexture(0);
      drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
      drawTimebase(sx + 149, sy + 35);
      drawGraph(sx + 48, sy + 11);
      break;
    case STAT:
      bindGuiTexture(1);
      drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
      drawStats(sx, sy);
      bindGuiTexture(1);
      // drawing area: start=16/7 size=153/72
      break;
    case CONTROL:
      bindGuiTexture(1);
      drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
      drawControls(sx, sy);
      bindGuiTexture(1);
      break;
    }

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

    startTabs();
    for (Tab drawTab : Tab.values()) {
      if (drawTab != Tab.GRAPH || getTileEntity().isAdvanced()) {
        renderStdTab(sx, sy, drawTab.tabNo - (getTileEntity().isAdvanced() ? 0 : 1), drawTab.itemStack, drawTab.button, drawTab == tab);
      }
    }
  }

  @Override
  protected boolean renderPowerBar() {
    return tab == Tab.GRAPH;
  }

  private static final int TEXT_MARGIN_TOP = 7;
  private static final int TEXT_MARGIN_LEFT = 7;
  private static final int TEXT_WIDTH = 162;
  private static final int LINE_Y_OFFSET = 18;
  private static final int TEXT_X_OFFSET = 18;
  private static final int TEXT_Y_OFFSET = 4;

  private static final int CONTROL_LF_PX = 16;

  private void drawControls(int sx, int sy) {
    FontRenderer fontRenderer = getFontRenderer();
    int textColor = ColorUtil.getRGB(engineControlEnabled.isSelected() ? Color.black : Color.darkGray);

    int x0 = sx + TEXT_MARGIN_LEFT;
    int y0 = sy + TEXT_MARGIN_TOP;

    String engineTxt1 = EnderIO.lang.localize("gui.powerMonitor.engineSection1").trim(); // Emit signal when storage less
    String engineTxt2 = EnderIO.lang.localize("gui.powerMonitor.engineSection2").trim(); // than
    String engineTxt3 = EnderIO.lang.localize("gui.powerMonitor.engineSection3").trim(); // % full.
    String engineTxt4 = EnderIO.lang.localize("gui.powerMonitor.engineSection4").trim(); // Stop when storage greater than
    String engineTxt5 = EnderIO.lang.localize("gui.powerMonitor.engineSection5").trim(); // or equal to

    List<Object> elems = new ArrayList<Object>();
    elems.add(engineControlEnabled);
    elems.addAll(Arrays.asList(engineTxt1.split("(\\s+)")));
    elems.addAll(Arrays.asList(engineTxt2.split("(\\s+)")));
    elems.add(engineControlStart);
    elems.addAll(Arrays.asList(engineTxt3.split("(\\s+)")));
    elems.addAll(Arrays.asList(engineTxt4.split("(\\s+)")));
    elems.addAll(Arrays.asList(engineTxt5.split("(\\s+)")));
    elems.add(engineControlStop);
    elems.addAll(Arrays.asList(engineTxt3.split("(\\s+)")));

    int x = 0, y = 0;

    for (Object elem : elems) {
      int elemWidth = 0;
      if (elem instanceof String) {
        elemWidth = fontRenderer.getStringWidth((String) elem);
      } else if (elem instanceof CheckBox) {
        elemWidth = ((CheckBox) elem).width;
      } else if (elem instanceof TextFieldEnder) {
        elemWidth = ((TextFieldEnder) elem).width;
      }

      if (x + elemWidth > TEXT_WIDTH) {
        x = 0;
        y += CONTROL_LF_PX;
        if (" ".equals(elem)) {
          continue;
        }
      }

      if (elem instanceof String) {
        fontRenderer.drawString((String) elem, x0 + x, y0 + y + TEXT_Y_OFFSET, textColor);
      } else if (elem instanceof CheckBox) {
        ((CheckBox) elem).xPosition = x0 + x;
        ((CheckBox) elem).yPosition = y0 + y;
      } else if (elem instanceof TextFieldEnder) {
        ((TextFieldEnder) elem).xPosition = x0 + x;
        ((TextFieldEnder) elem).yPosition = y0 + y;
      }

      x += elemWidth + fontRenderer.getStringWidth(" ");
    }

  }

  private void drawStats(int sx, int sy) {
    FontRenderer fontRenderer = getFontRenderer();

    int valuesCol = ColorUtil.getRGB(Color.black);
    int errorCol = ColorUtil.getRGB(Color.red);
    int x = sx + TEXT_MARGIN_LEFT;
    int y = sy + TEXT_MARGIN_TOP;

    StatData statData = getTileEntity().getStatData();
    if (statData == null || statData.maxPowerInConduits == 0) {
      fontRenderer.drawSplitString(EnderIO.lang.localize("gui.powerMonitor.noNetworkError"), x, y, TEXT_WIDTH, errorCol);
      return;
    }

    RenderHelper.enableGUIStandardItemLighting();
    itemRender.renderItemIntoGUI(new ItemStack(itemPowerConduit.getItem(), 1, 2), x, y);
    tooltipConduitStorage.setBounds(new Rectangle(TEXT_MARGIN_LEFT, TEXT_MARGIN_TOP, TEXT_WIDTH, 16));

    ItemStack capBankStack = new ItemStack(blockCapBank.getBlock(), 1, 3);
    if (statData.maxPowerInCapBanks > 0) {
      BlockItemCapBank.setStoredEnergyForItem(capBankStack,
              (int) (((BlockItemCapBank) capBankStack.getItem()).getMaxEnergyStored(capBankStack) * ((double) statData.powerInCapBanks / (double) statData.maxPowerInCapBanks)));
    }

    itemRender.renderItemIntoGUI(capBankStack, x, y + LINE_Y_OFFSET);
    tooltipCapacitorBankStorage.setBounds(new Rectangle(TEXT_MARGIN_LEFT, TEXT_MARGIN_TOP + LINE_Y_OFFSET, TEXT_WIDTH, 16));

    itemRender.renderItemIntoGUI(new ItemStack(blockAlloySmelter.getBlock()), x, y + 3 * LINE_Y_OFFSET);
    tooltipMachineBuffers.setBounds(new Rectangle(TEXT_MARGIN_LEFT, TEXT_MARGIN_TOP + 3 * LINE_Y_OFFSET, TEXT_WIDTH, 16));
    RenderHelper.disableStandardItemLighting();

    bindGuiTexture(1);
    drawTexturedModalRect(x, y + 2 * LINE_Y_OFFSET, 180, 31, 16, 16);
    tooltipAverageOutput.setBounds(new Rectangle(TEXT_MARGIN_LEFT, TEXT_MARGIN_TOP + 2 * LINE_Y_OFFSET, TEXT_WIDTH / 2, 16));

    drawTexturedModalRect(x + TEXT_WIDTH / 2, y + 2 * LINE_Y_OFFSET, 196, 31, 16, 16);
    tooltipAverageInput.setBounds(new Rectangle(TEXT_MARGIN_LEFT + TEXT_WIDTH / 2, TEXT_MARGIN_TOP + 2 * LINE_Y_OFFSET, TEXT_WIDTH / 2, 16));

    StringBuilder sb = new StringBuilder();
    sb.append(formatPower(statData.powerInConduits));
    sb.append(" ");
    sb.append(PowerDisplayUtil.ofStr());
    sb.append(" ");
    sb.append(formatPower(statData.maxPowerInConduits));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    fontRenderer.drawString(sb.toString(), x + TEXT_X_OFFSET, y + TEXT_Y_OFFSET, valuesCol, false);

    sb = new StringBuilder();
    sb.append(formatPower(statData.powerInCapBanks));
    sb.append(" ");
    sb.append(PowerDisplayUtil.ofStr());
    sb.append(" ");
    sb.append(formatPower(statData.maxPowerInCapBanks));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    fontRenderer.drawString(sb.toString(), x + TEXT_X_OFFSET, y + TEXT_Y_OFFSET + LINE_Y_OFFSET, valuesCol, false);

    sb = new StringBuilder();
    sb.append(formatPower(statData.powerInMachines));
    sb.append(" ");
    sb.append(PowerDisplayUtil.ofStr());
    sb.append(" ");
    sb.append(formatPower(statData.maxPowerInMachines));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    fontRenderer.drawString(sb.toString(), x + TEXT_X_OFFSET, y + TEXT_Y_OFFSET + 3 * LINE_Y_OFFSET, valuesCol, false);

    sb = new StringBuilder();
    sb.append(formatPowerFloat(statData.aveRfSent));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append(PowerDisplayUtil.perTickStr());
    fontRenderer.drawString(sb.toString(), x + TEXT_X_OFFSET, y + TEXT_Y_OFFSET + 2 * LINE_Y_OFFSET, valuesCol, false);

    sb = new StringBuilder();
    sb.append(formatPowerFloat(statData.aveRfReceived));
    sb.append(" ");
    sb.append(PowerDisplayUtil.abrevation());
    sb.append(PowerDisplayUtil.perTickStr());
    fontRenderer.drawString(sb.toString(), x + TEXT_X_OFFSET + TEXT_WIDTH / 2, y + TEXT_Y_OFFSET + 2 * LINE_Y_OFFSET, valuesCol, false);

  }

  private int getInt(GuiTextField tf) {
    try {
      int val = Integer.parseInt(tf.getText());
      if (val >= 0 && val <= 100) {
        return val;
      }
      return 0;
    } catch (Exception e) {
      return 0;
    }
  }

}
