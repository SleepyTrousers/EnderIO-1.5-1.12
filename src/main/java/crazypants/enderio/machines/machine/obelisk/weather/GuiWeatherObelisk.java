package crazypants.enderio.machines.machine.obelisk.weather;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.widget.GuiToolTip;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.machine.gui.GuiInventoryMachineBase;
import crazypants.enderio.base.machine.gui.PowerBar;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.obelisk.weather.TileWeatherObelisk.WeatherTask;
import crazypants.enderio.machines.network.PacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;

public class GuiWeatherObelisk extends GuiInventoryMachineBase<TileWeatherObelisk> {

  private static final @Nonnull Rectangle RECTANGLE_TANK = new Rectangle(22, 11, 16, 63);
  private IconButton buttonStart;

  public GuiWeatherObelisk(@Nonnull InventoryPlayer inventory, @Nonnull TileWeatherObelisk tileEntity) {
    super(tileEntity, new ContainerWeatherObelisk(inventory, tileEntity), "weather_obelisk");

    addProgressTooltip(79, 29, 18, 31);

    addToolTip(new GuiToolTip(RECTANGLE_TANK, "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.GUI_WEATHER_FTANK.get());
        text.add(LangFluid.MB(getTileEntity().getInputTank()));
      }
    });

    addDrawingElement(new PowerBar<>(tileEntity, this, 8, 11, 63));
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
    ((ContainerWeatherObelisk) inventorySlots).createGhostSlots(getGhostSlotHandler().getGhostSlots());
  }

  @Override
  public void updateScreen() {
    super.updateScreen();
    if (getTileEntity().getWorld().getTotalWorldTime() % 20 == 0) {
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

    if (shouldRenderProgress() && getTileEntity().getActiveTask() != null) {
      int barHeight = getProgressScaled(ContainerWeatherObelisk.MAX_SCALE);
      Color color = getTileEntity().getActiveTask().color;
      GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
      this.drawTexturedModalRect(getGuiLeft() + 81, getGuiTop() + 58 - barHeight, getXSize(), 32 - barHeight, 12, barHeight);
    }
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }

  @Override
  protected void actionPerformed(@Nonnull GuiButton b) throws IOException {
    super.actionPerformed(b);
    if (b.id >= 0 && b.id <= 2) {
      getTileEntity().startTask();
      PacketHandler.INSTANCE.sendToServer(new PacketActivateWeather(getTileEntity(), true));
    }
  }
}
