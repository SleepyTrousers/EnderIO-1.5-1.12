package crazypants.enderio.machines.machine.generator.lava;

import java.awt.Rectangle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.gui.GuiCapMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiLavaGenerator extends GuiCapMachineBase<TileLavaGenerator> {

  private static final int POWERX = 10;
  private static final int POWERY = 14;
  private static final int POWER_HEIGHT = 42;

  private static final @Nonnull Rectangle RECTANGLE_TANK = new Rectangle(70, 21, 16, 47);
  private static final @Nonnull Rectangle RECTANGLE_HEAT = new Rectangle(90, 21, 16, 47);

  public GuiLavaGenerator(@Nonnull InventoryPlayer playerInv, @Nonnull TileLavaGenerator te) {
    super(te, new ContainerLavaGenerator<>(playerInv, te), "lava_generator");
    addDrawingElement(new PowerBar(te.getEnergy(), this, POWERX, POWERY, POWER_HEIGHT));
    addToolTip(new GuiToolTip(RECTANGLE_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.GUI_TANK_TANK_TANK_TANK.get());
        text.add(LangFluid.MB(getTileEntity().tank));
      }

    });
    addToolTip(new GuiToolTip(RECTANGLE_HEAT, "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add("TODO heat"); // TODO
        text.add(getTileEntity().heat + "°X");
        text.add((int) (getTileEntity().getHeatFactor() * 100) + "% efficiency");
        text.add(getTileEntity().getPowerGenPerTick() + " power gen");
      }

    });
  }

  @Override
  protected boolean showRecipeButton() {
    return false; // TODO JEI recipe
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
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    RenderUtil.renderGuiTank(getTileEntity().tank, guiLeft + RECTANGLE_TANK.x, guiTop + RECTANGLE_TANK.y, zLevel, RECTANGLE_TANK.width, RECTANGLE_TANK.height);

    // TODO heatbar
  }

}
