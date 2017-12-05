package crazypants.enderio.machines.machine.killera;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.google.common.collect.Lists;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.gui.GuiMachineBase;
import crazypants.enderio.base.machine.modes.IoMode;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiKillerJoe extends GuiMachineBase<TileKillerJoe> {

  private static final @Nonnull Rectangle RECTANGLE_FUEL_TANK = new Rectangle(18, 11, 15, 47);
  private final @Nonnull ToggleButton showRangeB;

  public GuiKillerJoe(@Nonnull InventoryPlayer inventory, final @Nonnull TileKillerJoe tileEntity) {
    super(tileEntity, new ContainerKillerJoe(inventory, tileEntity), "killer_joe");

    addToolTip(new GuiToolTip(RECTANGLE_FUEL_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = EnderIO.lang.localize("killerJoe.fuelTank");
        text.add(heading);
        text.add(LangFluid.toCapactityString(getTileEntity().tank));
        if (tileEntity.tank.getFluidAmount() < tileEntity.getActivationAmount()) {
          text.add(EnderIO.lang.localize("gui.fluid.minReq", LangFluid.MB(tileEntity.getActivationAmount())));
        }
      }

    });

    int spacing = 5;
    int bw = 16;

    int x = 81;

    x += spacing + bw;

    x += spacing + bw;

    x = getXSize() - 5 - BUTTON_SIZE;
    showRangeB = new ToggleButton(this, -1, x, 44, IconEIO.SHOW_RANGE, IconEIO.HIDE_RANGE);
    showRangeB.setSize(BUTTON_SIZE, BUTTON_SIZE);
    addToolTip(new GuiToolTip(showRangeB.getBounds(), "null") {
      @Override
      public @Nonnull List<String> getToolTipText() {
        return Lists.newArrayList(EnderIO.lang.localize(showRangeB.isSelected() ? "gui.spawnGurad.hideRange" : "gui.spawnGurad.showRange"));
      }
    });

  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_FUEL_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().tank.getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  @Override
  public void initGui() {
    super.initGui();
    showRangeB.onGuiInit();
    showRangeB.setSelected(getTileEntity().isShowingRange());
    ((ContainerKillerJoe) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b == showRangeB) {
      getTileEntity().setShowRange(showRangeB.isSelected());
    }
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  public void renderSlotHighlights(IoMode mode) {
    super.renderSlotHighlights(mode);

    if (mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      int x = 16;
      int y = 9;
      int w = 15 + 4;
      int h = 47 + 4;
      renderSlotHighlight(PULL_COLOR, x, y, w, h);
    }

  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    int x = guiLeft + 18;
    int y = guiTop + 11;
    TileKillerJoe joe = getTileEntity();
    if (joe.tank.getFluidAmount() > 0) {
      RenderUtil.renderGuiTank(joe.tank.getFluid(), joe.tank.getCapacity(), joe.tank.getFluidAmount(), x, y, zLevel, 16, 47);
    }
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
