package crazypants.enderio.machines.machine.basin;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.GlStateDiagnosticsHelper;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.fluid.SmartTank;

import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.gui.GuiCapMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiBasin extends GuiCapMachineBase<TileBasin> {

  private class TooltipTank extends GuiToolTip {
    
    private final SmartTank tank;
    
    TooltipTank(Rectangle bounds, SmartTank tank) {
      super(bounds);
      this.tank = tank;
    }

    @Override
    protected void updateText() {
      text.clear();
      text.add(Lang.GUI_TANK_TANK_TANK_TANK.get());
      text.add(LangFluid.MB(tank));
    }
  }
  
  private static final @Nonnull Rectangle RECTANGLE_TANK_UP = new Rectangle(60, 10, 40, 15);
  private static final @Nonnull Rectangle RECTANGLE_TANK_DOWN = new Rectangle(60, 60, 40, 15);
  private static final @Nonnull Rectangle RECTANGLE_TANK_LEFT = new Rectangle(55, 15, 15, 40);
  private static final @Nonnull Rectangle RECTANGLE_TANK_RIGHT = new Rectangle(105, 15, 15, 40);

  protected GuiBasin(@Nonnull InventoryPlayer playerInv, @Nonnull TileBasin te) {
    super(te, new ContainerBasin(playerInv, te), "basin");
    
    addToolTip(new TooltipTank(RECTANGLE_TANK_UP, getTileEntity().tankU));
    addToolTip(new TooltipTank(RECTANGLE_TANK_DOWN, getTileEntity().tankD));
    addToolTip(new TooltipTank(RECTANGLE_TANK_LEFT, getTileEntity().tankL));
    addToolTip(new TooltipTank(RECTANGLE_TANK_RIGHT, getTileEntity().tankR));
    
    addDrawingElement(new PowerBar(te.getEnergy(), this, 12, 12, 42));
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1, 1, 1);
    bindGuiTexture();
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    
    RenderUtil.renderGuiTank(getTileEntity().tankU, guiLeft + RECTANGLE_TANK_UP.x, guiTop + RECTANGLE_TANK_UP.y, 0, RECTANGLE_TANK_UP.width, RECTANGLE_TANK_UP.height);
    RenderUtil.renderGuiTank(getTileEntity().tankD, guiLeft + RECTANGLE_TANK_DOWN.x, guiTop + RECTANGLE_TANK_DOWN.y, 0, RECTANGLE_TANK_DOWN.width, RECTANGLE_TANK_DOWN.height);
    RenderUtil.renderGuiTank(getTileEntity().tankL, guiLeft + RECTANGLE_TANK_LEFT.x, guiTop + RECTANGLE_TANK_LEFT.y, 0, RECTANGLE_TANK_LEFT.width, RECTANGLE_TANK_LEFT.height);
    RenderUtil.renderGuiTank(getTileEntity().tankR, guiLeft + RECTANGLE_TANK_RIGHT.x, guiTop + RECTANGLE_TANK_RIGHT.y, 0, RECTANGLE_TANK_RIGHT.width, RECTANGLE_TANK_RIGHT.height);
    
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
