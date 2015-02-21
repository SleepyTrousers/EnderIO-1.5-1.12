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
import crazypants.util.Lang;
import crazypants.vecmath.Vector4f;

public class GuiFarmStation extends GuiPoweredMachineBase<TileFarmStation> {

  private static final int LOCK_ID = 1234;

  public GuiFarmStation(InventoryPlayer par1InventoryPlayer, TileFarmStation machine) {
    super(machine, new FarmStationContainer(par1InventoryPlayer, machine));
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public void initGui() {
    super.initGui();
    
    int x = getGuiLeft() + 36;
    int y = getGuiTop()  + 36;
    
    buttonList.add(createLockButton(TileFarmStation.minSupSlot + 0, x, y));
    buttonList.add(createLockButton(TileFarmStation.minSupSlot + 1, x + 52, y));
    buttonList.add(createLockButton(TileFarmStation.minSupSlot + 2, x, y + 20));
    buttonList.add(createLockButton(TileFarmStation.minSupSlot + 3, x + 52, y + 20));
  }
  
  private IconButtonEIO createLockButton(int slot, int x, int y) {
    return new ToggleButtonEIO(this, LOCK_ID+slot, x, y, IconEIO.FARM_UNLOCK, IconEIO.FARM_LOCK).setSelected(getTileEntity().isSlotLocked(slot));
  }

  @Override
  protected void drawForegroundImpl(int mouseX, int mouseY) {
    super.drawForegroundImpl(mouseX, mouseY);

    if(!isConfigOverlayEnabled()) {
      for(int i=TileFarmStation.minSupSlot ; i<=TileFarmStation.maxSupSlot ; i++) {
        if(getTileEntity().isSlotLocked(i)) {
          Slot slot = inventorySlots.getSlot(i);
          GL11.glEnable(GL11.GL_BLEND);
          RenderUtil.renderQuad2D(slot.xDisplayPosition, slot.yDisplayPosition, 0, 16, 16, new Vector4f(0, 0, 0, 0.5));
        }
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
    if (b.id >= LOCK_ID+TileFarmStation.minSupSlot && b.id <= LOCK_ID+TileFarmStation.maxSupSlot) {
      getTileEntity().toggleLockedState(b.id - LOCK_ID);
    }
    super.actionPerformed(b);
  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }
  
  @Override
  protected String getPowerOutputLabel() {
    return Lang.localize("farm.gui.baseUse");
  }
}
