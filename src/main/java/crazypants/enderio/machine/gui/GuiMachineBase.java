package crazypants.enderio.machine.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.IoConfigRenderer.SelectedFace;
import crazypants.enderio.gui.RedstoneModeButton;
import crazypants.enderio.machine.AbstractInventoryMachineEntity;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.util.TextUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public abstract class GuiMachineBase<T extends AbstractInventoryMachineEntity> extends GuiContainerBaseEIO {

  public static final Vector4f PUSH_COLOR = new Vector4f(0.8f, 0.4f, 0.1f, 0.5f);
  public static final Vector4f PULL_COLOR = new Vector4f(0.1f, 0.4f, 0.8f, 0.5f);

  public static final int BUTTON_SIZE = 16;
  private static final int CONFIG_ID = 8962349;
  private static final int RECIPE_ID = CONFIG_ID + 1;

  private final T tileEntity;

  protected RedstoneModeButton redstoneButton;

  private final GuiOverlayIoConfig configOverlay;

  protected final GuiButtonIoConfig configB;
  
  protected IconButton recipeButton;

  protected List<GuiToolTip> progressTooltips;
  protected int lastProgressTooltipValue = -1;

  protected GuiMachineBase(T machine, Container par1Container, String... guiTexture) {
    super(par1Container, guiTexture);
    tileEntity = machine;
    
    xSize = getXSize();
    ySize = getYSize();
    int x = getXSize() - 5 - BUTTON_SIZE;
    int y = 5;
    redstoneButton = new RedstoneModeButton(this, -1, x, y, tileEntity, new BlockCoord(tileEntity));

    configOverlay = new GuiOverlayIoConfig(machine);
    addOverlay(configOverlay);

    y += 19;
    configB = new GuiButtonIoConfig(this, CONFIG_ID, x, y, machine, configOverlay);

    y += 19;
    recipeButton = new IconButton(this, RECIPE_ID, x, y, IconEIO.RECIPE);
    recipeButton.visible = false;
    recipeButton.setIconMargin(1, 1);
  }

  @Override
  public void initGui() {
    super.initGui();
    redstoneButton.onGuiInit();
    configB.onGuiInit();
    recipeButton.onGuiInit();
  }

  protected boolean showRecipeButton() {
    return EnderIO.proxy.isAnEiInstalled();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);    
    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = buttonList.get(i);
      guibutton.drawButton(mc, 0, 0);
    }

    if(showRecipeButton()) {
      recipeButton.visible = true;
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    renderSlotHighlights();
  }

  public void renderSlotHighlights() {
    SelectedFace sel = configOverlay.getSelection();
    if(sel != null) {
      IoMode mode = sel.config.getIoMode(sel.face);
      renderSlotHighlights(mode);
    }
  }

  public void renderSlotHighlights(IoMode mode) {
    SlotDefinition slotDef = tileEntity.getSlotDefinition();

    for (Slot invSlot : inventorySlots.inventorySlots) {
      if (invSlot.inventory == tileEntity) {
        if ((mode == IoMode.PULL || mode == IoMode.PUSH_PULL) && slotDef.isInputSlot(invSlot.getSlotIndex())) {
          renderSlotHighlight(invSlot, PULL_COLOR);
        } else if ((mode == IoMode.PUSH || mode == IoMode.PUSH_PULL) && slotDef.isOutputSlot(invSlot.getSlotIndex())) {
          renderSlotHighlight(invSlot, PUSH_COLOR);
        }
      }
    }
  }

  protected void renderSlotHighlight(int slot, Vector4f col) {
    Slot invSlot = inventorySlots.inventorySlots.get(slot);
    renderSlotHighlight(col, invSlot.xDisplayPosition, invSlot.yDisplayPosition, 16, 16);
  }

  protected void renderSlotHighlight(Slot invSlot, Vector4f col) {
    renderSlotHighlight(col, invSlot.xDisplayPosition, invSlot.yDisplayPosition, 16, 16);
  }

  protected void renderSlotHighlight(Vector4f col, int x, int y, int width, int height) {
    GL11.glEnable(GL11.GL_BLEND);
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y, 0, width, height, col);
    GL11.glDisable(GL11.GL_BLEND);
  }
  
  protected boolean isConfigOverlayEnabled() {
    return configOverlay.isVisible();
  }
  
  protected T getTileEntity() {
    return tileEntity;
  }

  protected void addProgressTooltip(int x, int y, int w, int h) {
    if(progressTooltips == null) {
      progressTooltips = new ArrayList<GuiToolTip>();
    }

    GuiToolTip tt = new GuiToolTip(new Rectangle(x, y, w, h), (String[])null);
    progressTooltips.add(tt);
    addToolTip(tt);
  }

  protected final void updateProgressTooltips(int scaledProgress, float progress) {
    if(lastProgressTooltipValue == scaledProgress || progressTooltips == null) {
      return;
    }
    lastProgressTooltipValue = scaledProgress;

    if(scaledProgress < 0) {
      for(GuiToolTip tt : progressTooltips) {
        tt.setIsVisible(false);
      }
      return;
    }

    String msg = formatProgressTooltip(scaledProgress, progress);
    String[] tooltip = msg.split("\\|");
    for(GuiToolTip tt : progressTooltips) {
      tt.setToolTipText(tooltip);
      tt.setIsVisible(true);
    }
  }

  protected String formatProgressTooltip(int scaledProgress, float progress) {
    return TextUtil.format(EnderIO.lang.localize("gui.progress"), scaledProgress);
  }

  protected int scaleProgressForTooltip(float progress) {
    return (int) (progress * 100);
  }

  protected boolean shouldRenderProgress() {
    if(tileEntity instanceof IProgressTile) {
      float progress = ((IProgressTile) tileEntity).getProgress();
      if(progress > 0 && progress < 1) {
        updateProgressTooltips(scaleProgressForTooltip(progress), progress);
        return true;
      } else {
        updateProgressTooltips(-1, -1);
        return false;
      }
    }
    return false;
  }

  protected int getProgressScaled(int scale) {
    if(getTileEntity() instanceof IProgressTile) {
      return Util.getProgressScaled(scale, (IProgressTile) getTileEntity());
    }
    return 0;
  }
}
