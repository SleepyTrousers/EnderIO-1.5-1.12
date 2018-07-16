package crazypants.enderio.machines.machine.basin;

import java.awt.Rectangle;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.fluid.SmartTank;

import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.gui.GuiCapMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.base.machine.interfaces.IPoweredTask;
import crazypants.enderio.base.recipe.basin.BasinRecipe;
import crazypants.enderio.machines.lang.Lang;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

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
  
  private static final @Nonnull Rectangle RECT_TANK_UP = new Rectangle(65, 13, 47, 15);
  private static final @Nonnull Rectangle RECT_TRANSFER_UP = new Rectangle(88, 28, 2, 13);

  private static final @Nonnull Rectangle RECT_TANK_DOWN = new Rectangle(65, 70, 47, 15);
  private static final @Nonnull Rectangle RECT_TRANSFER_DOWN = new Rectangle(88, 57, 2, 13);

  private static final @Nonnull Rectangle RECT_TANK_LEFT = new Rectangle(36, 25, 15, 47);
  private static final @Nonnull Rectangle RECT_TRANSFER_LEFT = new Rectangle(51, 48, 30, 2);

  private static final @Nonnull Rectangle RECT_TANK_RIGHT = new Rectangle(127, 25, 15, 47);
  private static final @Nonnull Rectangle RECT_TRANSFER_RIGHT = new Rectangle(97, 48, 30, 2);

  protected GuiBasin(@Nonnull InventoryPlayer playerInv, @Nonnull TileBasin te) {
    super(te, new ContainerBasin(playerInv, te), "basin");
    
    addToolTip(new TooltipTank(RECT_TANK_UP, getTileEntity().tankU));
    addToolTip(new TooltipTank(RECT_TANK_DOWN, getTileEntity().tankD));
    addToolTip(new TooltipTank(RECT_TANK_LEFT, getTileEntity().tankL));
    addToolTip(new TooltipTank(RECT_TANK_RIGHT, getTileEntity().tankR));
    
    addProgressTooltip(52, 29, 74, 40);
    
    addDrawingElement(new PowerBar(te.getEnergy(), this, 12, 13, 59));
  }
  
  @Override
  public int getYSize() {
    return 181;
  }
  
  private void drawTank(FluidTank tank, Rectangle rect) {
    drawTank(tank.getFluid(), tank.getCapacity(), rect);
  }
  
  private void drawTank(FluidStack stack, int capacity, Rectangle rect) {
    RenderUtil.renderGuiTank(stack, capacity, stack == null ? 0 : stack.amount, guiLeft + rect.x, guiTop + rect.y, 0, rect.width, rect.height);
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GlStateManager.color(1, 1, 1);
    bindGuiTexture();
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    
    drawTank(getTileEntity().tankU, RECT_TANK_UP);
    drawTank(getTileEntity().tankD, RECT_TANK_DOWN);
    drawTank(getTileEntity().tankL, RECT_TANK_LEFT);
    drawTank(getTileEntity().tankR, RECT_TANK_RIGHT);

    if (shouldRenderProgress()) {
      if (!getTileEntity().tankU.isEmpty()) {
        FluidStack stack = getTileEntity().tankU.getFluidNN();
        drawTank(stack, stack.amount, RECT_TRANSFER_UP);
        stack = getTileEntity().tankD.getFluidNN();
        drawTank(stack, stack.amount, RECT_TRANSFER_DOWN);
      } else {
        FluidStack stack = getTileEntity().tankL.getFluidNN();
        drawTank(stack, stack.amount, RECT_TRANSFER_LEFT);
        stack = getTileEntity().tankR.getFluidNN();
        drawTank(stack, stack.amount, RECT_TRANSFER_RIGHT);
      }
    }
    
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

}
