package crazypants.enderio.machines.machine.obelisk.attractor;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.ColorUtil;
import com.google.common.collect.Lists;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiAttractor extends GuiInventoryMachineBase<TileAttractor> {

  private static final int RANGE_ID = 8738924;

  private final ToggleButton showRangeB;

  public GuiAttractor(@Nonnull InventoryPlayer par1InventoryPlayer, @Nonnull TileAttractor te) {
    super(te, new ContainerAttractor(par1InventoryPlayer, te), "attractor");

    int x = getXSize() - 5 - BUTTON_SIZE;
    showRangeB = new ToggleButton(this, RANGE_ID, x, 44, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
    showRangeB.setSize(BUTTON_SIZE, BUTTON_SIZE);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public @Nonnull List<String> getToolTipText() {
        return Lists.newArrayList((showRangeB.isSelected() ? Lang.GUI_HIDE_RANGE : Lang.GUI_SHOW_RANGE).get());
      }
    });

    addDrawingElement(new PowerBar<>(te, this));
  }

  @Override
  public void initGui() {
    super.initGui();
    showRangeB.onGuiInit();
    showRangeB.setSelected(getTileEntity().isShowingRange());
    ((ContainerAttractor) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b.id == RANGE_ID) {
      getTileEntity().setShowRange(showRangeB.isSelected());
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    if (!getTileEntity().canWork()) {
      drawCenteredString(fontRenderer, Lang.GUI_OBELISK_NO_VIALS.get(), width / 2 + 9, sy + 68, ColorUtil.getRGB(Color.red));
    } else {
      int range = (int) getTileEntity().getRange();
      drawCenteredString(fontRenderer, Lang.GUI_RANGE.get(range), width / 2 + 9, sy + 68, ColorUtil.getRGB(Color.white));
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

}
