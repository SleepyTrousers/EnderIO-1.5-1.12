package crazypants.enderio.teleport.telepad.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.gui.widget.GuiToolTip;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.GuiContainerBaseEIO;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.teleport.telepad.TileDialingDevice;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDialingDevice extends GuiContainerBaseEIO {
  
  private static final int ID_TELEPORT_BUTTON = 96;
  
  GuiButton teleportButton;
  
  private TileDialingDevice te;
  
  private int powerX = 8;
  private int powerY = 9;
  private int powerScale = 120;
  
//  private int progressX = 26;
//  private int progressY = 110;
//  private int progressScale = 124;
  
  public static int SWITCH_X = 155, SWITCH_Y = 5;

  public GuiDialingDevice(InventoryPlayer playerInv, TileDialingDevice te) {
    super(new ContainerDialingDevice(playerInv, te), "telePad");
    this.te = te;
    ySize = 220;

    addToolTip(new GuiToolTip(new Rectangle(powerX, powerY, 10, powerScale), "") {
      @Override
      protected void updateText() {
        text.clear();
        updatePowerBarTooltip(text);
      }
    });
    
//    addToolTip(new GuiToolTip(new Rectangle(progressX, progressY, progressScale, 10), "") {
//      @Override
//      protected void updateText() {
//        text.clear();
//        text.add(Math.round(GuiDialingDevice.this.te.getProgress() * 100) + "%");
//      }
//    });

//    FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
//
//    int x = 48;
//    int y = 24;
//    int tfHeight = 12;
//    int tfWidth = xSize - x * 2;
    


  }

  private String getPowerOutputLabel() {
    return I18n.format("enderio.gui.max");
  }

  protected int getPowerOutputValue() {
    return te.getUsage();
  }
  
  protected void updatePowerBarTooltip(List<String> text) {
    text.add(getPowerOutputLabel() + " " + PowerDisplayUtil.formatPower(getPowerOutputValue()) + " " + PowerDisplayUtil.abrevation()
        + PowerDisplayUtil.perTickStr());
    text.add(PowerDisplayUtil.formatStoredPower(te.getEnergyStored(), te.getMaxEnergyStored()));
  }

  @Override
  public void initGui() {
    super.initGui();    

    String text = EnderIO.lang.localize("gui.telepad.teleport");
    int width = getFontRenderer().getStringWidth(text) + 10;

    int x = guiLeft + (xSize / 2) - (width / 2);
    int y = guiTop + 83;
    
    teleportButton = new GuiButton(ID_TELEPORT_BUTTON, x, y, width, 20, text);
    addButton(teleportButton);
    
    ((ContainerDialingDevice) inventorySlots).createGhostSlots(getGhostSlots());
  }

  @Override
  public void updateScreen() {
    super.updateScreen();    
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    bindGuiTexture();
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    int powerScaled = te.getPowerScaled(powerScale);
    drawTexturedModalRect(sx + powerX, sy + powerY + powerScale - powerScaled, xSize, 0, 10, powerScaled);
//    int progressScaled = Util.getProgressScaled(progressScale, te);
//    drawTexturedModalRect(sx + progressX, sy + progressY, 0, ySize, progressScaled, 10);


    super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
  }

  
  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    super.actionPerformed(button);
    
    if (button.id == ID_TELEPORT_BUTTON) {
//      te.teleportAll();
    }
  }
}
