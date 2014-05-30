package crazypants.enderio.machine.tank;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.generator.stirling.StirlingGeneratorContainer;
import crazypants.enderio.machine.generator.stirling.TileEntityStirlingGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.gui.GuiToolTip;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiTank extends GuiMachineBase {

  private TileTank entity;

  public GuiTank(InventoryPlayer par1InventoryPlayer, TileTank te) {
    super(te, new ContainerTank(par1InventoryPlayer, te));
    this.entity = te;
    
    addToolTip(new GuiToolTip(new Rectangle(80, 21, 16, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = Lang.localize("tank.tank");
        if(entity.tank.getFluid() != null) {
          heading += ": " + entity.tank.getFluid().getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(entity.tank));
      }

    });
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/tank.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    
    RenderUtil.bindBlockTexture();
    RenderUtil.renderGuiTank(entity.tank, guiLeft + 80, guiTop + 21, zLevel, 16,47);    

  }
  
  protected boolean renderPowerBar() {
    return false;
  }
}
