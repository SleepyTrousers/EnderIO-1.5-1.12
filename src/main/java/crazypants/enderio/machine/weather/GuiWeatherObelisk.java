package crazypants.enderio.machine.weather;

import java.awt.Color;
import java.util.Locale;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.machine.weather.TileWeatherObelisk.Task;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.Lang;

public class GuiWeatherObelisk extends GuiPoweredMachineBase<TileWeatherObelisk> {

  private static final ResourceLocation texture = new ResourceLocation("enderio:textures/gui/weatherObelisk.png");
  
  public GuiWeatherObelisk(InventoryPlayer inventory, TileWeatherObelisk tileEntity) {
    super(tileEntity, new ContainerWeatherObelisk(inventory, tileEntity));
  }

  @Override
  public void initGui() {    
    super.initGui();

    int x = (xSize / 2) - (BUTTON_SIZE / 2);
    int y = 8;

    addButton(new IconButtonEIO(this, 0, x - 30, y, IconEIO.SUN), Task.CLEAR);
    addButton(new IconButtonEIO(this, 1, x, y, IconEIO.RAIN), Task.RAIN);
    addButton(new IconButtonEIO(this, 2, x + 30, y, IconEIO.THUNDER), Task.STORM);
  }

  private void addButton(IconButtonEIO button, Task task) {
    String tt1 = EnumChatFormatting.WHITE + Lang.localize("gui.weather.task." + task.name().toLowerCase(Locale.ENGLISH));
    String tt2 = EnumChatFormatting.GRAY + String.format(Lang.localize("gui.weather.requireditem"), task.requiredItem.getDisplayName());
    button.setToolTip(tt1, tt2);
    button.onGuiInit();
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    this.mc.renderEngine.bindTexture(texture);

    this.drawTexturedModalRect(getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize());
    
    if(getTileEntity().activeTask != null) {
      int barHeight = getTileEntity().getProgressScaled(31);
      Color color = getTileEntity().activeTask.color;
      GL11.glColor3f((float) color.getRed() / 255f, (float) color.getGreen() / 255f, (float) color.getBlue() / 255f);
      this.drawTexturedModalRect(getGuiLeft() + 81, getGuiTop() + 58 - barHeight, getXSize(), 32 - barHeight, 12, barHeight);
    }
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
  
  @Override
  protected int getPowerHeight() {
    return super.getPowerHeight() + 20;
  }
  
  @Override
  protected int getPowerU() {
    return super.getPowerU();
  }
  
  @Override
  protected int getPowerV() {
    return 34;
  }
  
  @Override
  protected int getPowerX() {
    return super.getPowerX();
  }
  
  @Override
  protected int getPowerY() {
    return super.getPowerY();
  }
  
  @Override
  protected void actionPerformed(GuiButton b) {
    super.actionPerformed(b);
    if (b.id >= 0 && b.id <= 2) {
      getTileEntity().startTask(b.id);
      PacketHandler.INSTANCE.sendToServer(new PacketActivateWeather(getTileEntity(), Task.values()[b.id]));
    }
  }
}
