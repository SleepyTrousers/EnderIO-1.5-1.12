package crazypants.enderio.conduit.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.ColorButton;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.MultiIconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduit.liquid.FluidFilter;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;

public class LiquidSettings extends BaseSettingsPanel {

  static final int ID_REDSTONE_BUTTON = GuiExternalConnection.nextButtonId();

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();
  private static final int ID_WHITELIST = GuiExternalConnection.nextButtonId();

  private static final int NEXT_FILTER_ID = 989322;

  private static final int ID_CHANNEL = GuiExternalConnection.nextButtonId();

  private final RedstoneModeButton rsB;
  private final ColorButton colorB;
  private ColorButton channelB;

  private static final String autoExtractStr = EnderIO.lang.localize("gui.conduit.fluid.autoExtract");
  private static final String filterStr = EnderIO.lang.localize("gui.conduit.fluid.filter");

  private final ILiquidConduit conduit;

  private EnderLiquidConduit eConduit;
  private boolean isEnder;
  private static final int filterX = 59;
  private static final int filterY = 63;
  private static final Rectangle filterBounds = new Rectangle(filterX, filterY, 90, 18);
  private GuiToolTip[] filterToolTips;

  private boolean inOutShowIn = true;
  private MultiIconButton inOutNextB;
  private IconButton whiteListB;

  protected LiquidSettings(final GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_FLUID, EnderIO.lang.localize("itemLiquidConduit.name"), gui, con);

    conduit = (ILiquidConduit) con;
    if(con instanceof EnderLiquidConduit) {
      eConduit = (EnderLiquidConduit) con;
      isEnder = true;

      int x = gui.getXSize() - 20;
      int y = customTop;

      inOutNextB = MultiIconButton.createRightArrowButton(gui, NEXT_FILTER_ID, x, y);

      x = filterX - 20;
      y = filterY + 1;

      whiteListB = new IconButton(gui, ID_WHITELIST, x, y, IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.fluid.whitelist"));
    } else {
      isEnder = false;
      gui.getContainer().setInventorySlotsVisible(false);
    }

    int x = gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap * 2;
    int y = customTop;

    if(isEnder)
    {
      channelB = new ColorButton(gui, ID_CHANNEL, x, y);
      channelB.setColorIndex(0);
      channelB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.item.channel"));
      x += channelB.getWidth() + 4;
    }

    rsB = new RedstoneModeButton(gui, ID_REDSTONE_BUTTON, x, y, new IRedstoneModeControlable() {

      @Override
      public void setRedstoneControlMode(RedstoneControlMode mode) {
        RedstoneControlMode curMode = getRedstoneControlMode();
        conduit.setExtractionRedstoneMode(mode, gui.getDir());
        if(curMode != mode) {
          PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(conduit, gui.getDir()));
        }

      }

      @Override
      public RedstoneControlMode getRedstoneControlMode() {
        return conduit.getExtractionRedstoneMode(gui.getDir());
      }
    });

    x += rsB.getWidth() + gap;
    colorB = new ColorButton(gui, ID_COLOR_BUTTON, x, y);
    colorB.setToolTipHeading(EnderIO.lang.localize("gui.conduit.redstone.signalColor"));
    colorB.setColorIndex(conduit.getExtractionSignalColor(gui.getDir()).ordinal());

  }

  private void addFilterTooltips() {
    filterToolTips = new GuiToolTip[5];
    for (int i = 0; i < 5; i++) {
      Rectangle bound = new Rectangle(filterX + (i * 18), filterY, 18, 18);
      filterToolTips[i] = new FilterToolTip(bound, i);
      gui.addToolTip(filterToolTips[i]);
    }
  }

  @Override
  public void actionPerformed(GuiButton guiButton) {
    super.actionPerformed(guiButton);
    if(guiButton.id == ID_COLOR_BUTTON) {
      conduit.setExtractionSignalColor(gui.getDir(), DyeColor.values()[colorB.getColorIndex()]);
      PacketHandler.INSTANCE.sendToServer(new PacketExtractMode(conduit, gui.getDir()));
    } else if(guiButton.id == ID_WHITELIST) {
      toggleBlacklist();
    } else if(guiButton.id == NEXT_FILTER_ID) {
      inOutShowIn = !inOutShowIn;
      if(channelB != null) {
        channelB.onGuiInit();
        if(isInput()) {
          channelB.setColorIndex(eConduit.getInputColor(gui.getDir()).ordinal());
        } else {
          channelB.setColorIndex(eConduit.getOutputColor(gui.getDir()).ordinal());
        }
      }
      if(isInput()) {
        rsB.onGuiInit();
        colorB.onGuiInit();
      } else {
        rsB.detach();
        colorB.detach();
      }
      if(isFilterVisible()) {
        updateWhiteListButton(eConduit.getFilter(gui.getDir(), isInput()));
      }
    } else if(guiButton.id == ID_CHANNEL) {
      if(isEnder) {

        DyeColor col = DyeColor.values()[channelB.getColorIndex()];

        if(isInput()) {
          eConduit.setInputColor(gui.getDir(), col);
        } else  {
          eConduit.setOutputColor(gui.getDir(), col);
        }
        setConduitChannel(col);
      }
    }
  }

  @Override
  protected void connectionModeChanged(ConnectionMode conectionMode) {
    super.connectionModeChanged(conectionMode);
    updateGuiVisibility();
  }

  private void toggleBlacklist() {
    if(!isFilterVisible()) {
      return;
    }
    FluidFilter filter = eConduit.getFilter(gui.getDir(), isInput());
    if(filter == null) {
      filter = new FluidFilter();
    }
    filter.setBlacklist(!filter.isBlacklist());
    setConduitFilter(filter);
    updateWhiteListButton(filter);
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {

    if(!isFilterVisible()) {
      return;
    }
    if(!filterBounds.contains(x, y)) {
      return;
    }
    ItemStack st = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
    FluidFilter filter = eConduit.getFilter(gui.getDir(), isInput());
    if(filter == null && st == null) {
      return;
    }
    if(filter == null) {
      filter = new FluidFilter();
    }
    int slot = (x - filterX) / 18;
    filter.setFluid(slot, st);
    setConduitFilter(filter);
  }

  protected void setConduitFilter(FluidFilter filter) {
    eConduit.setFilter(gui.getDir(), filter, isInput());

    PacketHandler.INSTANCE.sendToServer(new PacketFluidFilter(eConduit, gui.getDir(), filter, isInput()));
  }
  protected void setConduitChannel(DyeColor channel) {
    if(isInput()) {
      eConduit.setInputColor(gui.getDir(), channel);
    } else {
      eConduit.setOutputColor(gui.getDir(), channel);
    }

    PacketHandler.INSTANCE.sendToServer(new PacketFluidChannel(eConduit, gui.getDir(), isInput(), channel));
  }

  @Override
  protected void initCustomOptions() {
    updateGuiVisibility();
  }

  private void updateGuiVisibility() {
    deactivate();

    if(isInput()) {
      rsB.onGuiInit();
      colorB.onGuiInit();
    }

    if(!isEnder) {
      return;
    }

    channelB.onGuiInit();
    if(isInput()){
      channelB.setColorIndex(eConduit.getInputColor(gui.getDir()).ordinal());
    } else {
      channelB.setColorIndex(eConduit.getOutputColor(gui.getDir()).ordinal());
    }

    if(isFilterVisible()) {
      gui.getContainer().setInventorySlotsVisible(true);
      addFilterTooltips();

      whiteListB.onGuiInit();
      updateWhiteListButton(eConduit.getFilter(gui.getDir(), isInput()));
    } else {
      gui.getContainer().setInventorySlotsVisible(false);
    }

    ConnectionMode mode = con.getConnectionMode(gui.getDir());
    if(mode == ConnectionMode.IN_OUT) {
      inOutNextB.onGuiInit();
    }
  }

  private void updateWhiteListButton(FluidFilter filter) {
    if(filter != null && filter.isBlacklist()) {
      whiteListB.setIcon(IconEIO.FILTER_BLACKLIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.fluid.blacklist"));
    } else {
      whiteListB.setIcon(IconEIO.FILTER_WHITELIST);
      whiteListB.setToolTip(EnderIO.lang.localize("gui.conduit.fluid.whitelist"));
    }
  }

  @Override
  public void deactivate() {

    rsB.detach();
    colorB.detach();
    if(isEnder) {
      gui.getContainer().setInventorySlotsVisible(false);
      if(filterToolTips != null) {
        for (GuiToolTip tt : filterToolTips) {
          if(tt != null) {
            gui.removeToolTip(tt);
          }
        }
      }
      inOutNextB.detach();
      whiteListB.detach();
      channelB.detach();
    }
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    boolean isInput = isInput();
    if(isInput) {
      int x = gui.getGuiLeft() + gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap + 2;
      int y = top;
      gui.getFontRenderer().drawString(autoExtractStr, left, y, ColorUtil.getRGB(Color.DARK_GRAY));
    }
    if(isEnder && isFilterVisible()) {

      if(conduit.getConnectionMode(gui.getDir()) == ConnectionMode.IN_OUT) {
        String inOutStr = inOutShowIn ? EnderIO.lang.localize("gui.conduit.ioMode.input") : EnderIO.lang.localize("gui.conduit.ioMode.output");
        int x = gui.getGuiLeft() + gui.getXSize() - 20 - 5 - gui.getFontRenderer().getStringWidth(inOutStr);
        int y = top;
        gui.getFontRenderer().drawString(inOutStr, x, y, ColorUtil.getRGB(Color.DARK_GRAY));
      }

      GL11.glColor3f(1, 1, 1);
      gui.bindGuiTexture(1);
      gui.drawTexturedModalRect(gui.getGuiLeft(), gui.getGuiTop() + 55, 0, 55, gui.getXSize(), 145);

      FontRenderer fr = gui.getFontRenderer();
      int sw = fr.getStringWidth(filterStr);
      int x = (gui.width / 2) - sw / 2;
      int y = top + 20;
      fr.drawString(filterStr, x, y, ColorUtil.getRGB(Color.DARK_GRAY));

      x = gui.getGuiLeft() + filterX;
      y = gui.getGuiTop() + filterY;
      GL11.glColor3f(1, 1, 1);
      gui.bindGuiTexture();
      gui.drawTexturedModalRect(x, y, 24, 238, 90, 18);

      FluidFilter filter = eConduit.getFilter(gui.getDir(), isInput);
      if(filter != null && !filter.isEmpty()) {
        for (int i = 0; i < filter.size(); i++) {
          FluidStack f = filter.getFluidStackAt(i);
          if(f != null) {
            renderFluid(f, x + (i * 18), y);
          }
        }
      }

    }
  }

  private void renderFluid(FluidStack f, int x, int y) {
    IIcon icon = f.getFluid().getIcon();
    if(icon != null) {
      RenderUtil.bindBlockTexture();
      int color = f.getFluid().getColor(f);
      GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));
      gui.drawTexturedModelRectFromIcon(x + 1, y + 1, icon, 16, 16);
      GL11.glColor3f(1, 1, 1);
    }

  }

  private boolean isInput() {
    ConnectionMode mode = conduit.getConnectionMode(gui.getDir());
    return (mode == ConnectionMode.IN_OUT && inOutShowIn) || (mode == ConnectionMode.INPUT);
  }

  private boolean isFilterVisible() {
    if(!isEnder) {
      return false;
    }
    ConnectionMode mode = conduit.getConnectionMode(gui.getDir());
    return mode == ConnectionMode.INPUT || mode == ConnectionMode.OUTPUT || mode == ConnectionMode.IN_OUT;
  }

  private class FilterToolTip extends GuiToolTip {

    int index;

    public FilterToolTip(Rectangle bounds, int index) {
      super(bounds, (String[]) null);
      this.index = index;
    }

    @Override
    public List<String> getToolTipText() {
      if(!isFilterVisible()) {
        return null;
      }
      FluidFilter filter = eConduit.getFilter(gui.getDir(), isInput());
      if(filter == null) {
        return null;
      }
      if (filter.getFluidStackAt(index) == null) {
        return null;
      }
      return Collections.singletonList(filter.getFluidStackAt(index).getLocalizedName());
    }

  }

}
