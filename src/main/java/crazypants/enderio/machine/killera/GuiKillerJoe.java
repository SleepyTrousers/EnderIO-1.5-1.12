package crazypants.enderio.machine.killera;

import java.awt.Color;
import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.fluid.Fluids;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.PacketRedstoneMode;
import crazypants.enderio.machine.generator.zombie.ContainerZombieGenerator;
import crazypants.enderio.machine.generator.zombie.TileZombieGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.GuiToolTip;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

public class GuiKillerJoe extends GuiMachineBase {

  private static final int XP_ID = 3489;
  private static final int XP10_ID = 34892;

  private TileKillerJoe joe;
  
  private IconButtonEIO xpB;
  private IconButtonEIO xp10B;

  public GuiKillerJoe(InventoryPlayer inventory, TileKillerJoe tileEntity) {
    super(tileEntity, new ContainerKillerJoe(inventory, tileEntity));
    joe = tileEntity;
    
    addToolTip(new GuiToolTip(new Rectangle(18, 11, 15, 47), "") {

      @Override
      protected void updateText() {
        text.clear();
        String heading = Lang.localize("killerJoe.fuelTank");        
        text.add(heading);
        text.add(Fluids.toCapactityString(joe.fuelTank));
      }

    });
    
    xpB = new IconButtonEIO(this, XP_ID, 128, 56, IconEIO.XP);    
    xpB.setToolTip(Lang.localize("killerJoe.giveXp.tooltip"));
    
    xp10B = new IconButtonEIO(this, XP10_ID, 148, 56, IconEIO.XP_PLUS);    
    xp10B.setToolTip(Lang.localize("killerJoe.giveXp10.tooltip"));
    
  }
  
  @Override
  public void initGui() {
    super.initGui();    
    xpB.onGuiInit();
    xp10B.onGuiInit();
  }
  
  @Override
  protected void actionPerformed(GuiButton b) {
    super.actionPerformed(b);
    if(b.id == XP_ID) {
      PacketHandler.INSTANCE.sendToServer(new PacketUseXP(joe, 1));
    } else if(b.id == XP10_ID) {
      PacketHandler.INSTANCE.sendToServer(new PacketUseXP(joe, 10));
    }
  }
  
  @Override
  protected boolean showRecipeButton() {
    return false;
  }

  @Override
  protected boolean renderPowerBar() { 
    return false;
  }

  @Override
  public void renderSlotHighlights(IoMode mode) {
    super.renderSlotHighlights(mode);

    if(mode == IoMode.PULL || mode == IoMode.PUSH_PULL) {
      int x = 16;
      int y = 9;
      int w = 15 + 4;
      int h = 47 + 4;
      renderSlotHighlight(PULL_COLOR,x,y,w,h);     
    }

  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/killerJoe.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;
    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    int x = guiLeft + 18;
    int y = guiTop + 11;    
    if(joe.fuelTank.getFluidAmount() > 0) {    
      RenderUtil.renderGuiTank(joe.fuelTank.getFluid(), joe.fuelTank.getCapacity(), joe.fuelTank.getFluidAmount(), x, y, zLevel, 15, 47);           
    }
    
    String s = joe.experienceLevel + "";
    drawCenteredString(Minecraft.getMinecraft().fontRenderer, s, sx + xSize/2, sy + 50, 8453920);
    
    RenderUtil.bindTexture("enderio:textures/gui/killerJoe.png");
    GL11.glColor3f(1, 1, 1);
    int xpScaled = joe.getXpBarScaled(63);    
    drawTexturedModalRect(sx + 56, sy + 62, 192, 0, xpScaled, 3);

    
    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }


}
