package crazypants.enderio.base.machine.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.util.IProgressTile;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;
import com.enderio.core.common.inventory.EnderInventory.Type;
import com.enderio.core.common.inventory.EnderSlot;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.config.config.PersonalConfig;
import crazypants.enderio.base.gui.GuiContainerBaseEIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.gui.IoConfigRenderer;
import crazypants.enderio.base.gui.RedstoneModeButton;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.machine.base.te.AbstractCapabilityMachineEntity;
import crazypants.enderio.base.machine.modes.IoMode;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;

public class GuiCapMachineBase<T extends AbstractCapabilityMachineEntity> extends GuiContainerBaseEIO<T> {

  public static final @Nonnull Vector4f PUSH_COLOR = new Vector4f(0.8f, 0.4f, 0.1f, 0.5f);
  public static final @Nonnull Vector4f PULL_COLOR = new Vector4f(0.1f, 0.4f, 0.8f, 0.5f);

  public static final int BUTTON_SIZE = 16;
  private static final int CONFIG_ID = 8962349;
  private static final int RECIPE_ID = CONFIG_ID + 1;

  protected @Nonnull RedstoneModeButton<T> redstoneButton;

  private final @Nonnull GuiOverlayIoConfig<T> configOverlay;

  protected final @Nonnull GuiButtonIoConfig<T> configB;

  protected @Nonnull IconButton recipeButton;

  protected List<GuiToolTip> progressTooltips;
  protected int lastProgressTooltipValue = -1;
  private final @Nonnull ContainerEnderCap<EnderInventory, T> capContainer;

  protected GuiCapMachineBase(@Nonnull T machine, @Nonnull ContainerEnderCap<EnderInventory, T> par1Container, @Nonnull String... guiTexture) {
    super(machine, par1Container, guiTexture);
    capContainer = par1Container;

    xSize = getXSize();
    ySize = getYSize();
    int x = getButtonXPos() - 5 - BUTTON_SIZE;
    int y = 5;
    redstoneButton = new RedstoneModeButton<T>(this, -1, x, y, getOwner());

    configOverlay = new GuiOverlayIoConfig<T>(machine);
    addOverlay(configOverlay);

    y += 19;
    configB = new GuiButtonIoConfig<T>(this, CONFIG_ID, x, y, machine, configOverlay);

    y += 19;
    if (PersonalConfig.recipeButtonInMachineGuis.get()) {
      recipeButton = new IconButton(this, RECIPE_ID, x, y, IconEIO.RECIPE_BOOK);
      recipeButton.setIsVisible(false);
    } else {
      recipeButton = new IconButton(this, RECIPE_ID, x, y, IconEIO.RECIPE);
      recipeButton.setIsVisible(false);
      recipeButton.setIconMargin(1, 1);
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    redstoneButton.onGuiInit();
    configB.onGuiInit();
    recipeButton.onGuiInit();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    for (GuiButton guiButton : buttonList) {
      guiButton.drawButton(mc, 0, 0, 0);
    }

    if (showRecipeButton()) {
      recipeButton.setIsVisible(true);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    renderSlotHighlights();
  }

  public void renderSlotHighlights() {
    IoConfigRenderer.SelectedFace<T> sel = configOverlay.getSelection();
    if (sel != null) {
      IoMode mode = sel.config.getIoMode(sel.face);
      renderSlotHighlights(mode);
    }
  }

  public void renderSlotHighlights(@Nonnull IoMode mode) {
    Map<Slot, Point> slotLocations = getInventory().getSlotLocations();
    for (Slot slot : slotLocations.keySet()) {
      if (slot instanceof EnderSlot) {
        Type type = ((EnderSlot) slot).getType();
        if (mode == IoMode.PULL) {
          if (type == EnderInventory.Type.INPUT || type == EnderInventory.Type.INOUT) {
            renderSlotHighlight(slot, PULL_COLOR);
          }
        } else if (mode == IoMode.PUSH) {
          if (type == EnderInventory.Type.OUTPUT || type == EnderInventory.Type.INOUT) {
            renderSlotHighlight(slot, PUSH_COLOR);
          }
        } else if (mode == IoMode.PUSH_PULL) {
          if (type == EnderInventory.Type.INPUT) {
            renderSlotHighlight(slot, PULL_COLOR);
          } else if (type == EnderInventory.Type.OUTPUT) {
            renderSlotHighlight(slot, PUSH_COLOR);
          } else if (type == EnderInventory.Type.INOUT) {
            renderSplitHighlight(slot);
          }
        }
      }
    }
  }

  protected void renderSlotHighlight(@Nonnull Slot invSlot, @Nonnull Vector4f col) {
    renderSlotHighlight(col, invSlot.xPos, invSlot.yPos, 16, 16);
  }

  protected void renderSlotHighlight(@Nonnull Vector4f col, int x, int y, int widthIn, int heightIn) {
    GlStateManager.enableBlend();
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y, 0, widthIn, heightIn, col);
    GlStateManager.disableBlend();
  }

  protected void renderSplitHighlight(@Nonnull Slot invSlot) {
    renderSplitHighlight(invSlot.xPos, invSlot.yPos, 16, 16);
  }

  protected void renderSplitHighlight(int x, int y, int widthIn, int heightIn) {
    GlStateManager.enableBlend();
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y, 0, widthIn, heightIn / 2, PULL_COLOR);
    RenderUtil.renderQuad2D(getGuiLeft() + x, getGuiTop() + y + (heightIn / 2), 0, widthIn, heightIn / 2, PUSH_COLOR);
    GlStateManager.disableBlend();
  }

  protected boolean isConfigOverlayEnabled() {
    return configOverlay.isVisible();
  }

  protected @Nonnull T getTileEntity() {
    return getOwner();
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
    if (getTileEntity() instanceof IProgressTile) {
      float progress = ((IProgressTile) getTileEntity()).getProgress();
      if (progress >= 0 && progress < 1) {
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
    if (getTileEntity() instanceof IProgressTile) {
      return Util.getProgressScaled(scale, (IProgressTile) getTileEntity());
    }
    return 0;
  }

  protected int getButtonXPos() {
    return getXSize();
  }

  @Nonnull
  public ContainerEnderCap<EnderInventory, T> getInventory() {
    return capContainer;
  }
}
