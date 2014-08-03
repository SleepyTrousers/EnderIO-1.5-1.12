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

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduit.liquid.FluidFilter;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.packet.PacketExtractMode;
import crazypants.enderio.gui.ColorButton;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.IRedstoneModeControlable;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.GuiToolTip;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.DyeColor;
import crazypants.util.Lang;

public class LiquidSettings extends BaseSettingsPanel {

  static final int ID_REDSTONE_BUTTON = GuiExternalConnection.nextButtonId();

  private static final int ID_COLOR_BUTTON = GuiExternalConnection.nextButtonId();

  private RedstoneModeButton rsB;

  private ColorButton colorB;

  private String autoExtractStr = Lang.localize("gui.conduit.fluid.autoExtract");
  private String filterStr = Lang.localize("gui.conduit.fluid.filter");

  private ILiquidConduit conduit;

  private EnderLiquidConduit eConduit;
  private boolean isEnder;
  private int filterX = 59;
  private int filterY = 63;
  private Rectangle filterBounds = new Rectangle(filterX, filterY, 90, 18);
  private GuiToolTip[] filterToolTips;

  protected LiquidSettings(final GuiExternalConnection gui, IConduit con) {
    super(IconEIO.WRENCH_OVERLAY_FLUID, Lang.localize("itemLiquidConduit.name"), gui, con);

    conduit = (ILiquidConduit) con;
    if(con instanceof EnderLiquidConduit) {
      eConduit = (EnderLiquidConduit) con;
      isEnder = true;
      if(isFilterVisible()) {
        gui.getContainer().setInventorySlotsVisible(true);
      }
      addFilterTooltips();
    } else {
      isEnder = false;
      gui.getContainer().setInventorySlotsVisible(false);
    }

    int x = gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap * 2;
    int y = customTop;

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
    colorB.setToolTipHeading(Lang.localize("gui.conduit.redstone.signalColor"));
    colorB.setColorIndex(conduit.getExtractionSignalColor(gui.getDir()).ordinal());
  }

  private void addFilterTooltips() {
    filterToolTips = new GuiToolTip[5];
    for(int i=0;i<5;i++) {
      Rectangle bound = new Rectangle(filterX + (i*18), filterY, 18, 18);
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
    }
  }

  @Override
  protected void connectionModeChanged(ConnectionMode conectionMode) {
    super.connectionModeChanged(conectionMode);
    if(conectionMode == ConnectionMode.INPUT) {
      rsB.onGuiInit();
      colorB.onGuiInit();
    } else {
      gui.removeButton(rsB);
      gui.removeButton(colorB);
    }

    if(isEnder && isFilterVisible()) {
      gui.getContainer().setInventorySlotsVisible(true);
    } else {
      gui.getContainer().setInventorySlotsVisible(false);
    }

  }

  @Override
  public void mouseClicked(int x, int y, int par3) {

    if(!isFilterVisible()) {
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
    if(filterBounds.contains(x, y)) {
      int slot = (x - filterX) / 18;
      filter.setFluid(slot, st);
    }
    eConduit.setFilter(gui.getDir(), filter, isInput());
    EnderIO.packetPipeline.INSTANCE.sendToServer(new PacketFluidFilter(eConduit, gui.getDir(), filter, isInput()));

  }

  @Override
  public void deactivate() {
    super.deactivate();
    rsB.setToolTip((String[]) null);
    colorB.setToolTip((String[]) null);
    if(isEnder) {
      gui.getContainer().setInventorySlotsVisible(false);
      for(GuiToolTip tt : filterToolTips) {
        if(tt != null) {
          gui.removeToolTip(tt);
        }
      }
    }
  }

  @Override
  protected void renderCustomOptions(int top, float par1, int par2, int par3) {
    boolean isInput = isInput();
    if(isInput) {
      int x = gui.getGuiLeft() + gap + gui.getFontRenderer().getStringWidth(autoExtractStr) + gap + 2;
      int y = customTop;
      gui.getFontRenderer().drawString(autoExtractStr, left, top, ColorUtil.getRGB(Color.DARK_GRAY));
    }
    if(isEnder && isFilterVisible()) {

      GL11.glColor3f(1, 1, 1);
      RenderUtil.bindTexture("enderio:textures/gui/itemFilter.png");
      gui.drawTexturedModalRect(gui.getGuiLeft(), gui.getGuiTop() + 55, 0, 55, gui.getXSize(), 145);
      
      FontRenderer fr = gui.getFontRenderer();
      int sw = fr.getStringWidth(filterStr);
      int x = (gui.width / 2) - sw / 2;
      int y = top + 20;
      fr.drawString(filterStr, x, y, ColorUtil.getRGB(Color.DARK_GRAY));

      x = gui.getGuiLeft() + filterX;
      y = gui.getGuiTop() + filterY;
      GL11.glColor3f(1, 1, 1);
      RenderUtil.bindTexture("enderio:textures/gui/externalConduitConnection.png");
      gui.drawTexturedModalRect(x, y, 24, 238, 90, 18);

      FluidFilter filter = eConduit.getFilter(gui.getDir(), isInput);
      if(filter != null && !filter.isEmpty()) {
        for (int i = 0; i < filter.size(); i++) {
          Fluid f = filter.getFluidAt(i);
          if(f != null) {
            renderFluid(f, x + (i * 18), y);
          }
        }

      }

    }
  }

  private void renderFluid(Fluid f, int x, int y) {
    IIcon icon = f.getIcon();
    if(icon != null) {
      RenderUtil.bindBlockTexture();
      gui.drawTexturedModelRectFromIcon(x + 1, y + 1, icon, 16, 16);
    }

  }

  private boolean isInput() {
    return conduit.getConectionMode(gui.getDir()) == ConnectionMode.INPUT;
  }

  private boolean isFilterVisible() {
    if(!isEnder) {
      return false;
    }
    ConnectionMode mode = conduit.getConectionMode(gui.getDir());
    return mode == ConnectionMode.INPUT || mode == ConnectionMode.OUTPUT;
  }
  
  private class FilterToolTip extends GuiToolTip {

    int index;
    
    public FilterToolTip(Rectangle bounds, int index) {
      super(bounds, (String[])null);
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
      if(filter.getFluidAt(index) == null) {
        return null;
      }
      return Collections.singletonList(filter.getFluidAt(index).getLocalizedName());
    }
    
  }

}
