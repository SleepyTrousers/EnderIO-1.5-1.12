package crazypants.enderio.machine.still;

import java.awt.Rectangle;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.IMachineRecipe.ResultStack;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.machine.MachineRecipeInput;
import crazypants.gui.GuiToolTip;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiVat extends GuiMachineBase {

  private static final String GUI_TEXTURE = "enderio:textures/gui/vat.png";
  
  private final TileVat vat;

  public GuiVat(InventoryPlayer inventory, TileVat te) {
    super(te, new ContainerVat(inventory, te));
    vat = te;
    
    addToolTip(new GuiToolTip(new Rectangle(30, 12, 15, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = Lang.localize("vat.inputTank");
        if(vat.inputTank.getFluid() != null) {
          heading += ": " + vat.inputTank.getFluid().getFluid().getLocalizedName();  
        } 
        text.add(heading);
        text.add(Fluids.toCapactityString(vat.inputTank));
      }

    });
    
    addToolTip(new GuiToolTip(new Rectangle(132,12,15,47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = Lang.localize("vat.outputTank");
        if(vat.outputTank.getFluid() != null) {
          heading += ": " + vat.outputTank.getFluid().getFluid().getLocalizedName();  
        } 
        text.add(heading);
        text.add(Fluids.toCapactityString(vat.outputTank));
      }

    });
    
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture(GUI_TEXTURE);
    int guiLeft = (width - xSize) / 2;
    int guiTop = (height - ySize) / 2;
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

    if(vat.getProgress() < 1 && vat.getProgress() > 0) {
      int scaled = vat.getProgressScaled(12);
      drawTexturedModalRect(guiLeft + 81, guiTop + 76 - scaled, 176, 12 - scaled, 14, scaled + 2);

      IIcon inputIcon = null;
      for (MachineRecipeInput input : vat.getCurrentTask().getInputs()) {
        if(input.fluid != null && input.fluid.getFluid() != null) {
          inputIcon = input.fluid.getFluid().getStillIcon();
          break;
        }
      }
      VatMachineRecipe rec = (VatMachineRecipe) vat.getCurrentTask().getRecipe();
      IIcon outputIcon = null;
      for (ResultStack res : rec.getCompletedResult(1.0f, vat.getCurrentTask().getInputs())) {
        if(res.fluid != null && res.fluid.getFluid() != null) {
          outputIcon = res.fluid.getFluid().getStillIcon();
        }
      }

      if(inputIcon != null && outputIcon != null) {
        renderVat(inputIcon, outputIcon, vat.getProgress());
      }

    }

    RenderUtil.bindBlockTexture();
    renderTank(vat.inputTank, 30);
    renderTank(vat.outputTank, 132);

    RenderUtil.bindTexture(GUI_TEXTURE);
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  private void renderVat(IIcon inputIcon, IIcon outputIcon, float progress) {
    RenderUtil.bindBlockTexture();

    int x = guiLeft + 76;
    int y = guiTop + 34;

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glColor4f(1, 1, 1, 0.75f * (1f - progress));
    drawTexturedModelRectFromIcon(x, y, inputIcon, 26, 28);

    GL11.glColor4f(1, 1, 1, 0.75f * progress);
    drawTexturedModelRectFromIcon(x, y, outputIcon, 26, 28);

    GL11.glDisable(GL11.GL_BLEND);

    GL11.glColor4f(1, 1, 1, 1);
    RenderUtil.bindTexture(GUI_TEXTURE);
    drawTexturedModalRect(x, y, 0, 256 - 28, 26, 28);
  }

  private void renderTank(FluidTank tank, int x) {
    FluidStack fluid = tank.getFluid();
    if(fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
      return;
    }

    IIcon icon = fluid.getFluid().getStillIcon();
    if(icon == null) {
      icon = fluid.getFluid().getIcon();
      if(icon == null) {
        return;
      }
    }

    float fullness = (float) tank.getFluidAmount() / (float) tank.getCapacity();
    int height = Math.round(47 * fullness);

    //System.out.println("GuiStill.renderTank: " + tank.getFluidAmount());

    x = guiLeft + x;
    int y = guiTop + 12;
    y = y + (47 - height);

    GL11.glColor4f(1, 1, 1, 0.75f);
    GL11.glEnable(GL11.GL_BLEND);
    drawTexturedModelRectFromIcon(x, y, icon, 15, height);
    GL11.glDisable(GL11.GL_BLEND);

  }

  @Override
  protected int getPowerX() {
    return 10;
  }

  @Override
  protected int getPowerY() {
    return 13;
  }

  @Override
  protected int getPowerHeight() {
    return 60;
  }

}
