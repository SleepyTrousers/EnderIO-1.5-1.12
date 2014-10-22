package crazypants.enderio.machine.crafter;

import java.util.List;

import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.render.RenderUtil;

public class GuiCrafter extends GuiMachineBase  {

  private TileCrafter entity;

  public GuiCrafter(InventoryPlayer par1InventoryPlayer, TileCrafter te) {
    super(te, new ContainerCrafter(par1InventoryPlayer, te));
    this.entity = te;
    xSize = getXSize();
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  public int getXSize() {
    return 219;
  }
  
  protected int getPowerU() {
    return 220;
  }

  @Override
  protected int getPowerX() {    
    return 9;
  }  
  
  protected void updatePowerBarTooltip(List<String> text) {
    text.add(PowerDisplayUtil.formatPower(Config.crafterRfPerCraft) + " " + PowerDisplayUtil.abrevation()
        + " Per Craft");
    super.updatePowerBarTooltip(text);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/crafter.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);    

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
  
}
