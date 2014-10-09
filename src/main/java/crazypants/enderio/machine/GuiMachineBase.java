package crazypants.enderio.machine;

import java.awt.Rectangle;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.IoConfigRenderer.SelectedFace;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.gui.GuiContainerBase;
import crazypants.gui.GuiToolTip;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Lang;
import crazypants.vecmath.Vector4f;

public abstract class GuiMachineBase extends GuiContainerBase {

  public static final Vector4f PUSH_COLOR = new Vector4f(0.8f, 0.4f, 0.1f, 0.5f);
  public static final Vector4f PULL_COLOR = new Vector4f(0.1f, 0.4f, 0.8f, 0.5f);

  protected static final int POWER_Y = 14;
  protected final int POWER_X = 15;
  protected static final int POWER_WIDTH = 10;
  protected static final int POWER_HEIGHT = 42;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  public static final int BUTTON_SIZE = 16;
  private static final int CONFIG_ID = 8962349;

  private AbstractMachineEntity tileEntity;

  protected RedstoneModeButton redstoneButton;

  private GuiOverlayIoConfig configOverlay;

  protected IconButtonEIO configB;

  public GuiMachineBase(AbstractMachineEntity machine, Container container) {
    super(container);
    tileEntity = machine;
    if(renderPowerBar()) {
      addToolTip(new GuiToolTip(new Rectangle(getPowerX(), getPowerY(), getPowerWidth(), getPowerHeight()), "") {

        @Override
        protected void updateText() {
          text.clear();
          if(renderPowerBar()) {
            text.add(getPowerOutputText() + PowerDisplayUtil.formatPower(tileEntity.getPowerUsePerTick()) + " " + PowerDisplayUtil.abrevation()
                + PowerDisplayUtil.perTickStr());
            //text.add()
            text.add(PowerDisplayUtil.formatStoredPower(tileEntity.getEnergyStored(), tileEntity.getCapacitor().getMaxEnergyStored()));
          }
        }

      });
    }
    xSize = getXSize();
    ySize = getYSize();
    int x = getXSize() - 5 - BUTTON_SIZE;
    int y = 5;
    redstoneButton = new RedstoneModeButton(this, -1, x, y, tileEntity, new BlockCoord(tileEntity));

    y += 19;
    configB = new IconButtonEIO(this, CONFIG_ID, x, y, IconEIO.IO_CONFIG_UP);
    configB.setToolTip(Lang.localize("gui.machine.ioMode.overlay.tooltip"));

    configOverlay = new GuiOverlayIoConfig(machine) {

      @Override
      public void setVisible(boolean visible) {
        super.setVisible(visible);
        configB.setIcon(visible ? IconEIO.IO_CONFIG_DOWN : IconEIO.IO_CONFIG_UP);
      }

    };
    addOverlay(configOverlay);
  }

  protected boolean showRecipeButton() {
    return EnderIO.proxy.isNeiInstalled();
  }

  protected String getPowerOutputText() {
    return "Max: ";
  }

  public void renderSlotHighlights(IoMode mode) {
    SlotDefinition slotDef = tileEntity.getSlotDefinition();
    if(slotDef.getNumInputSlots() > 0 && (mode == IoMode.PULL || mode == IoMode.PUSH_PULL)) {
      for (int slot = slotDef.getMinInputSlot(); slot <= slotDef.getMaxInputSlot(); slot++) {
        renderSlotHighlight(slot, PULL_COLOR);
      }
    }
    if(slotDef.getNumOutputSlots() > 0 && (mode == IoMode.PUSH || mode == IoMode.PUSH_PULL)) {
      for (int slot = slotDef.getMinOutputSlot(); slot <= slotDef.getMaxOutputSlot(); slot++) {
        renderSlotHighlight(slot, PUSH_COLOR);
      }
    }
  }

  protected void renderSlotHighlight(int slot, Vector4f col) {
    Slot invSlot = (Slot) inventorySlots.inventorySlots.get(slot);
    renderSlotHighlight(col, invSlot.xDisplayPosition, invSlot.yDisplayPosition, 16, 16);
  }

  protected void renderSlotHighlight(Vector4f col, int x, int y, int width, int height) {
    GL11.glEnable(GL11.GL_BLEND);
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y, 0, width, height, col);
    GL11.glDisable(GL11.GL_BLEND);
  }

  protected int getPowerX() {
    return POWER_X;
  }

  protected int getPowerY() {
    return POWER_Y;
  }

  protected int getPowerWidth() {
    return POWER_WIDTH;
  }

  protected int getPowerHeight() {
    return POWER_HEIGHT;
  }

  @Override
  protected void actionPerformed(GuiButton b) {
    super.actionPerformed(b);
    if(b.id == CONFIG_ID) {
      boolean vis = !configOverlay.isVisible();
      configOverlay.setVisible(vis);
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    redstoneButton.onGuiInit();
    configB.onGuiInit();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    int k = (width - xSize) / 2;
    int l = (height - ySize) / 2;
    if(renderPowerBar()) {      
      int i1 = tileEntity.getEnergyStoredScaled(getPowerHeight());      
      // x, y, u, v, width, height
      drawTexturedModalRect(k + getPowerX(), l + (getPowerY() + getPowerHeight()) - i1, getPowerU(), getPowerV(), getPowerWidth(), i1);
    }

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = (GuiButton) this.buttonList.get(i);
      guibutton.drawButton(this.mc, 0, 0);
    }

    if(showRecipeButton()) {
      IconEIO.RECIPE.renderIcon(k + 155, l + 43, 16, 16, 0, true);
    }

    SelectedFace sel = configOverlay.getSelection();
    if(sel != null) {
      IoMode mode = sel.config.getIoMode(sel.face);
      if(mode != null) {
        renderSlotHighlights(mode);
      }
    }

  }

  protected int getPowerV() {
    return 31;
  }

  protected int getPowerU() {
    return 176;
  }

  protected boolean renderPowerBar() {
    return true;
  }

}
