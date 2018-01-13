package crazypants.enderio.base.machine.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.ContainerEnder;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.IoConfigRenderer.SelectedFace;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.machine.baselegacy.AbstractInventoryMachineEntity;
import crazypants.enderio.base.machine.baselegacy.SlotDefinition;
import crazypants.enderio.base.machine.modes.IoMode;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public abstract class GuiMachineBase<T extends AbstractInventoryMachineEntity> extends GuiContainerBaseEIO {

  public static final @Nonnull Vector4f PUSH_COLOR = new Vector4f(0.8f, 0.4f, 0.1f, 0.5f);
  public static final @Nonnull Vector4f PULL_COLOR = new Vector4f(0.1f, 0.4f, 0.8f, 0.5f);

  public static final int BUTTON_SIZE = 16;
  private static final int CONFIG_ID = 8962349;
  private static final int RECIPE_ID = CONFIG_ID + 1;

  private final @Nonnull T tileEntity;

  protected @Nonnull RedstoneModeButton<T> redstoneButton;

  private final @Nonnull GuiOverlayIoConfig<T> configOverlay;

  protected final @Nonnull GuiButtonIoConfig<T> configB;

  protected @Nonnull IconButton recipeButton;

  protected List<GuiToolTip> progressTooltips;
  protected int lastProgressTooltipValue = -1;

  protected GuiMachineBase(@Nonnull T machine, @Nonnull Container par1Container, String... guiTexture) {
    super(par1Container, guiTexture);
    tileEntity = machine;

    xSize = getXSize();
    ySize = getYSize();
    int x = getXSize() - 5 - BUTTON_SIZE;
    int y = 5;
    redstoneButton = new RedstoneModeButton<T>(this, -1, x, y, tileEntity);

    configOverlay = new GuiOverlayIoConfig<T>(machine);
    addOverlay(configOverlay);

    y += 19;
    configB = new GuiButtonIoConfig<T>(this, CONFIG_ID, x, y, machine, configOverlay);

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

    if (showRecipeButton()) {
      recipeButton.visible = true;
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    renderSlotHighlights();
  }

  public void renderSlotHighlights() {
    SelectedFace<T> sel = configOverlay.getSelection();
    if (sel != null) {
      IoMode mode = sel.config.getIoMode(sel.face);
      renderSlotHighlights(mode);
    }
  }

  public void renderSlotHighlights(@Nonnull IoMode mode) {
    SlotDefinition slotDef = tileEntity.getSlotDefinition();

    for (Slot invSlot : inventorySlots.inventorySlots) { // this is a bit hacky, we need a better way for cap-based machines
      if (invSlot.inventory == tileEntity || (inventorySlots instanceof ContainerEnder && invSlot.inventory == ((ContainerEnder<?>) inventorySlots).getInv())) {
        if ((mode == IoMode.PULL || mode == IoMode.PUSH_PULL) && slotDef.isInputSlot(invSlot.getSlotIndex())) {
          renderSlotHighlight(invSlot, PULL_COLOR);
        } else if ((mode == IoMode.PUSH || mode == IoMode.PUSH_PULL) && slotDef.isOutputSlot(invSlot.getSlotIndex())) {
          renderSlotHighlight(invSlot, PUSH_COLOR);
        }
      }
    }
  }

  protected void renderSlotHighlight(int slot, @Nonnull Vector4f col) {
    Slot invSlot = inventorySlots.inventorySlots.get(slot);
    renderSlotHighlight(col, invSlot.xPos, invSlot.yPos, 16, 16);
  }

  protected void renderSlotHighlight(@Nonnull Slot invSlot, @Nonnull Vector4f col) {
    renderSlotHighlight(col, invSlot.xPos, invSlot.yPos, 16, 16);
  }

  protected void renderSlotHighlight(@Nonnull Vector4f col, int x, int y, int widthIn, int heightIn) {
    GlStateManager.enableBlend();
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y, 0, widthIn, heightIn, col);
    GlStateManager.disableBlend();
  }

  protected boolean isConfigOverlayEnabled() {
    return configOverlay.isVisible();
  }

  protected @Nonnull T getTileEntity() {
    return tileEntity;
  }

  protected void addProgressTooltip(int x, int y, int w, int h) {
    if (progressTooltips == null) {
      progressTooltips = new ArrayList<GuiToolTip>();
    }

    GuiToolTip tt = new GuiToolTip(new Rectangle(x, y, w, h), (String[]) null);
    progressTooltips.add(tt);
    addToolTip(tt);
  }

  protected final void updateProgressTooltips(int scaledProgress, float progress) {
    if (lastProgressTooltipValue == scaledProgress || progressTooltips == null) {
      return;
    }
    lastProgressTooltipValue = scaledProgress;

    if (scaledProgress < 0) {
      for (GuiToolTip tt : progressTooltips) {
        tt.setIsVisible(false);
      }
      return;
    }

    String msg = formatProgressTooltip(scaledProgress, progress);
    String[] tooltip = msg.split("\\|");
    for (GuiToolTip tt : progressTooltips) {
      tt.setToolTipText(tooltip);
      tt.setIsVisible(true);
    }
  }

  protected String formatProgressTooltip(int scaledProgress, float progress) {
    return Lang.GUI_GENERIC_PROGRESS.get(scaledProgress);
  }

  protected int scaleProgressForTooltip(float progress) {
    return (int) (progress * 100);
  }

  protected boolean shouldRenderProgress() {
    if (tileEntity instanceof IProgressTile) {
      float progress = ((IProgressTile) tileEntity).getProgress();
      if (progress > 0 && progress < 1) {
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
    if (tileEntity instanceof IProgressTile) {
      return Util.getProgressScaled(scale, (IProgressTile) tileEntity);
    }
    return 0;
  }
}
