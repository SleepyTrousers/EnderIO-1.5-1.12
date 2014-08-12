package crazypants.enderio.machine.killera;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.generator.zombie.ContainerZombieGenerator;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.gui.GuiToolTip;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiKillerJoe extends GuiMachineBase {

  private TileKillerJoe joe;

  public GuiKillerJoe(InventoryPlayer inventory, TileKillerJoe tileEntity) {
    super(tileEntity, new ContainerKillerJoe(inventory, tileEntity));
    joe = tileEntity;
    
    addToolTip(new GuiToolTip(new Rectangle(35, 21, 15, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = Lang.localize("killerJoe.fuelTank");        
        text.add(heading);
        text.add(Fluids.toCapactityString(joe.fuelTank));
      }

    });
    
  }
  
  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected boolean renderPowerBar() { 
    return false;
  }

  @Override
  public void renderSlotHighlights(IoMode mode) {
    super.renderSlotHighlights(mode);

    if(mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      int x = 33;
      int y = 19;
      int w = 15 + 4;
      int h = 47 + 4;
      renderSlotHighlight(PULL_COLOR,x,y,w,h);     
    }

  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/killerJoe.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    int x = guiLeft + 35;
    int y = guiTop + 21;    
    if(joe.fuelTank.getFluidAmount() > 0) {    
      RenderUtil.renderGuiTank(joe.fuelTank.getFluid(), joe.fuelTank.getCapacity(), joe.fuelTank.getFluidAmount(), x, y, zLevel, 15, 47);           
    }

    RenderUtil.bindTexture("enderio:textures/gui/killerJoe.png");
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }


}
