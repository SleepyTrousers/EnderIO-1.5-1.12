package crazypants.enderio.machines.machine.farm;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.api.client.gui.IGuiOverlay;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector4f;
import com.google.common.collect.Lists;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.GuiOverlayIoConfig;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

public class GuiFarmStation extends GuiInventoryMachineBase<TileFarmStation> {

  private static final int EXTRA_WITH = 8;
  private static final @Nonnull Rectangle RECTANGLE_TANK = new Rectangle(-35, 14, 16, 47);

  private static final int LOCK_ID = 1234;
  ToggleButton showRangeB;

  public GuiFarmStation(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileFarmStation machine) {
    super(machine, new FarmStationContainer(par1InventoryPlayer, machine), "farm_station");
    setYSize(ySize + 3);

    showRangeB = new ToggleButton(this, -1, 163, 43, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
    showRangeB.setSize(16, 16);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public @Nonnull List<String> getToolTipText() {
        return Lists.newArrayList((showRangeB.isSelected() ? Lang.GUI_HIDE_RANGE : Lang.GUI_SHOW_RANGE).get());
      }
    });
    addToolTip(new GuiToolTip(RECTANGLE_TANK, "") {
      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.GUI_TANK_TANK_TANK_TANK.get());
        text.add(LangFluid.MB(getTileEntity().tank));
      }
    });
    addDrawingElement(new PowerBar(machine, this, 45) {
      @Override
      protected String getPowerOutputLabel(@Nonnull String rft) {
        return Lang.GUI_FARM_BASEUSE.get(rft);
      }
    });
  }

  @Override
  public int getXSize() {
    return 176 + EXTRA_WITH;
  }

  @Override
  public void initGui() {
    super.initGui();

    int x = getGuiLeft() + 36;
    int y = getGuiTop() + 43;

    buttonList.add(createLockButton(TileFarmStation.minSupSlot + 0, x, y));
    buttonList.add(createLockButton(TileFarmStation.minSupSlot + 1, x + 52, y));
    buttonList.add(createLockButton(TileFarmStation.minSupSlot + 2, x, y + 20));
    buttonList.add(createLockButton(TileFarmStation.minSupSlot + 3, x + 52, y + 20));

    ((FarmStationContainer) inventorySlots).createGhostSlots(getGhostSlotHandler());

    showRangeB.onGuiInit();
    showRangeB.setSelected(getTileEntity().isShowingRange());

    for (IGuiOverlay overlay : overlays) {
      if (overlay instanceof GuiOverlayIoConfig) {
        overlay.getBounds().width -= EXTRA_WITH;
      }
    }
  }

  private IconButton createLockButton(int slot, int x, int y) {
    return new ToggleButton(this, LOCK_ID + slot, x, y, IconEIO.LOCK_UNLOCKED, IconEIO.LOCK_LOCKED).setSelected(getTileEntity().isSlotLocked(slot));
  }

  @Override
  protected void drawForegroundImpl(int mouseX, int mouseY) {
    super.drawForegroundImpl(mouseX, mouseY);
    if (!isConfigOverlayEnabled()) {
      for (int i = TileFarmStation.minSupSlot; i <= TileFarmStation.maxSupSlot; i++) {
        if (getTileEntity().isSlotLocked(i)) {
          Slot slot = inventorySlots.getSlot(i);
          GlStateManager.enableBlend();
          GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
          RenderUtil.renderQuad2D(slot.xPos, slot.yPos, 0, 16, 16, new Vector4f(0, 0, 0, 0.25));
          GlStateManager.disableBlend();
        }
      }
    }
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().tank.getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  @Override
  public List<Rectangle> getBlockingAreas() {
    final List<Rectangle> blockingAreas = super.getBlockingAreas();
    if (getTileEntity().hasTank()) {
      blockingAreas.add(new Rectangle(guiLeft - 42, guiTop + 3, 42, 72 - 3));
    }
    return blockingAreas;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();

    GlStateManager.enableAlpha();
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);

    if (getTileEntity().hasTank()) {
      drawTexturedModalRect(guiLeft - 42, guiTop, 214, 0, 42, 72);
      RenderUtil.renderGuiTank(getTileEntity().tank, guiLeft + RECTANGLE_TANK.x, guiTop + RECTANGLE_TANK.y, zLevel, RECTANGLE_TANK.width,
          RECTANGLE_TANK.height);
    }

    bindGuiTexture();
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;

    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    fr.drawString("SW", guiLeft + 55, guiTop + 48, ColorUtil.getARGB(1f, 1f, 0.35f, 1f), true);
    fr.drawString("NW", guiLeft + 55, guiTop + 66, ColorUtil.getARGB(1f, 1f, 0.35f, 1f), true);
    fr.drawString("SE", guiLeft + 73, guiTop + 48, ColorUtil.getARGB(1f, 1f, 0.35f, 1f), true);
    fr.drawString("NE", guiLeft + 73, guiTop + 66, ColorUtil.getARGB(1f, 1f, 0.35f, 1f), true);
    GlStateManager.disableBlend();
    GlStateManager.enableDepth();
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton b) throws IOException {
    if (b == showRangeB) {
      getTileEntity().setShowRange(showRangeB.isSelected());
      return;
    }
    if (b.id >= LOCK_ID + TileFarmStation.minSupSlot && b.id <= LOCK_ID + TileFarmStation.maxSupSlot) {
      getTileEntity().toggleLockedState(b.id - LOCK_ID);
    }
    super.actionPerformed(b);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

}
