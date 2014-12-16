package crazypants.enderio.machine.farm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Vector4f;

public class GuiFarmStation extends GuiPoweredMachineBase {

  private TileFarmStation farm;
  
  public GuiFarmStation(InventoryPlayer par1InventoryPlayer, TileFarmStation machine) {
    super(machine, new FarmStationContainer(par1InventoryPlayer, machine));
    
    this.farm = machine;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void initGui() {
    super.initGui();
    
    int x = getGuiLeft() + 36;
    int y = getGuiTop()  + 36;
    
    buttonList.add(createButton(x, y));
    buttonList.add(createButton(x + 52, y));
    buttonList.add(createButton(x, y + 20));
    buttonList.add(createButton(x + 52, y + 20));
    id = 0;
  }
  
  private int id = 0;
  private IconButtonEIO createButton(int x, int y) {
    return new ToggleButtonEIO(this, id, x, y, IconEIO.UNLOCKED, IconEIO.LOCKED).setSelected(farm.lockedSlots.contains(id++ + farm.minSupSlot)).setIconMargin(3, 3);
  }

  @Override
  protected void drawForegroundImpl(int mouseX, int mouseY) {
    super.drawForegroundImpl(mouseX, mouseY);
    
    if(inventorySlots.inventorySlots.size() >= farm.maxSupSlot && !isConfigOverlayEnabled()) {
      for (int i : farm.lockedSlots) {
        Slot slot = inventorySlots.getSlot(i);
        GL11.glEnable(GL11.GL_BLEND);
        RenderUtil.renderQuad2D(slot.xDisplayPosition, slot.yDisplayPosition, 0, 16, 16, new Vector4f(0, 0, 0, 0.5));
      }
    }
  }
  
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/farmStation.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
    
    GL11.glEnable(GL11.GL_BLEND);    
    fr.drawString("SW", sx + 55, sy + 41, ColorUtil.getARGB(1f,1f,0.35f,1f), true);    
    fr.drawString("NW", sx + 55, sy + 59, ColorUtil.getARGB(1f,1f,0.35f,1f), true);
    fr.drawString("SE", sx + 73, sy + 41, ColorUtil.getARGB(1f,1f,0.35f,1f), true);
    fr.drawString("NE", sx + 73, sy + 59, ColorUtil.getARGB(1f,1f,0.35f,1f), true);        
    GL11.glDisable(GL11.GL_BLEND);
    
    RenderUtil.bindTexture("enderio:textures/gui/farmStation.png");
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
  
  @Override
  protected void actionPerformed(GuiButton b) {
    farm.toggleLockedState(b.id);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }
  
  protected String getPowerOutputLabel() {
    return "Base Use: ";
  }
}
