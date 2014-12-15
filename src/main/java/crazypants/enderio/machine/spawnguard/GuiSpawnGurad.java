package crazypants.enderio.machine.spawnguard;

import java.awt.Color;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiSpawnGurad extends GuiPoweredMachineBase {

  TileSpawnGuard sg;
  ToggleButtonEIO showRangeB;
  
  private static final int RANGE_ID = 8738924;
  
  public GuiSpawnGurad(InventoryPlayer par1InventoryPlayer, TileSpawnGuard te) {
    super(te, new ContainerSpawnGuard(par1InventoryPlayer, te));
    sg = te;
    
    int x = getXSize() - 5 - BUTTON_SIZE;
    showRangeB = new ToggleButtonEIO(this, RANGE_ID, x, 44, IconEIO.ADD_BUT, IconEIO.ADD_BUT);
    showRangeB.setSize(BUTTON_SIZE, BUTTON_SIZE);
    showRangeB.setToolTip(Lang.localize("gui.spawnGurad.showRange"));
  }
  
  @Override
  public void initGui() {    
    super.initGui();
    showRangeB.onGuiInit();
    showRangeB.setSelected(sg.isShowingRange());
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/attractor.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);
    
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
    
    int range = (int) sg.getRange();
    drawCenteredString(fontRendererObj, Lang.localize("gui.spawnGurad.range") + " " + range, getGuiLeft() + sx/2 + 9, getGuiTop() + 68, ColorUtil.getRGB(Color.white));
  }

  @Override
  protected void actionPerformed(GuiButton b) {    
    super.actionPerformed(b);
    if(b.id == RANGE_ID) {
      sg.setShowRange(showRangeB.isSelected());      
    }
  }

  @Override
  protected boolean showRecipeButton() {    
    return false;
  }

}
