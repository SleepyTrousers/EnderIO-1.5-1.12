package crazypants.enderio.machine.obelisk.weather;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;
import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.obelisk.weather.TileWeatherObelisk.WeatherTask;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;

public class GuiWeatherObelisk extends GuiPoweredMachineBase<TileWeatherObelisk> {
  
  private static final Rectangle RECTANGLE_TANK = new Rectangle(22, 11, 16, 63);
  private IconButton buttonStart;
  
  public GuiWeatherObelisk(InventoryPlayer inventory, TileWeatherObelisk tileEntity) {
    super(tileEntity, new ContainerWeatherObelisk(inventory, tileEntity), "weatherObelisk");
    
    addProgressTooltip(79, 29, 18, 31);
    
    addToolTip(new GuiToolTip(RECTANGLE_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        FluidTank tank = getTileEntity().getInputTank();
        String heading = EnderIO.lang.localize("tank.tank");
        if (tank.getFluid() != null) {
          heading += ": " + tank.getFluid().getLocalizedName();
        }
        text.add(heading);
        text.add(Fluids.toCapactityString(getTileEntity().getInputTank()));
      }
    });
  }

  @Override
  @Nullable
  public Object getIngredientUnderMouse(int mouseX, int mouseY) {
    if (RECTANGLE_TANK.contains(mouseX, mouseY)) {
      return getTileEntity().getInputTank().getFluid();
    }
    return super.getIngredientUnderMouse(mouseX, mouseY);
  }

  @Override
  public void initGui() {
    super.initGui();

    int x = (xSize / 2) - (BUTTON_SIZE / 2);
    int y = 58;

    addButton(buttonStart = new IconButton(this, 0, x, y, IconEIO.TICK));
    buttonStart.onGuiInit();
    
    refreshButtons();
    ((ContainerWeatherObelisk) inventorySlots).createGhostSlots(getGhostSlots());
  }
  
  @Override
  public void updateScreen() {
    super.updateScreen();
    if(getTileEntity().getWorld().getTotalWorldTime() % 20 == 0) {
      refreshButtons();
    }
  }
  
  private void refreshButtons() {
    FluidStack fs = getTileEntity().getInputTank().getFluid();
    if (fs == null) {
      buttonStart.enabled = false;
      return;
    }
    WeatherTask task = WeatherTask.fromFluid(fs.getFluid());
    buttonStart.enabled = getTileEntity().canStartTask(task);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    bindGuiTexture();

    this.drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize());
    
    int x = getGuiLeft() + 22;
    int y = getGuiTop() + 11;
    RenderUtil.renderGuiTank(getTileEntity().getInputTank(), x, y, 0, 16, 63);

    bindGuiTexture();
    
    drawTexturedModalRect(x, y, 186, 33, 16, 63);

    if(shouldRenderProgress() && getTileEntity().getActiveTask() != null) {
      // TODO 1.10 test
      int barHeight = getProgressScaled(ContainerWeatherObelisk.MAX_SCALE);
      Color color = getTileEntity().getActiveTask().color;
      GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
      this.drawTexturedModalRect(getGuiLeft() + 81, getGuiTop() + 58 - barHeight, getXSize(), 32 - barHeight, 12, barHeight);
    }
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected int getPowerHeight() {
    return 63;
  }

  @Override
  protected int getPowerU() {
    return super.getPowerU();
  }
  
  @Override
  protected int getPowerV() {
    return 33;
  }
  
  @Override
  protected int getPowerX() {
    return super.getPowerX() - 7;
  }
  
  @Override
  protected int getPowerY() {
    return super.getPowerY() - 3;
  }
  
  @Override
  protected void actionPerformed(GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b.id >= 0 && b.id <= 2) {
      getTileEntity().startTask();
      PacketHandler.INSTANCE.sendToServer(new PacketActivateWeather(getTileEntity()));
    }
  }
}
