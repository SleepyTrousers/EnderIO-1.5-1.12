package crazypants.enderio.machine.spawner;

import java.awt.Color;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.machine.gui.GuiPoweredMachineBase;
import crazypants.enderio.network.PacketHandler;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;

@SideOnly(Side.CLIENT)
public class GuiPoweredSpawner extends GuiPoweredMachineBase<TilePoweredSpawner> {

  private IconButtonEIO modeB;

  public GuiPoweredSpawner(InventoryPlayer par1InventoryPlayer, TilePoweredSpawner te) {
    super(te, new ContainerPoweredSpawner(par1InventoryPlayer, te));

    modeB = new IconButtonEIO(this, 8888, 115, 10, IconEIO.RIGHT_ARROW);
    modeB.setSize(10, 16);
  }

  @Override
  public void initGui() {
    super.initGui();
    modeB.onGuiInit();
  }

  @Override
  protected void actionPerformed(GuiButton par1GuiButton) {
    if(par1GuiButton == modeB) {
      getTileEntity().setSpawnMode(!getTileEntity().isSpawnMode());
      PacketHandler.INSTANCE.sendToServer(new PacketMode(getTileEntity()));
    } else {
      super.actionPerformed(par1GuiButton);
    }
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/poweredSpawner.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, xSize, ySize);

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);

    TilePoweredSpawner spawner = getTileEntity();

    int left = getGuiLeft();
    int top = getGuiTop();

    String txt = Lang.localize("gui.machine.poweredspawner.spawn");
    if(!spawner.isSpawnMode()) {
      txt = Lang.localize("gui.machine.poweredspawner.capture");
    }
    FontRenderer fr = getFontRenderer();
    int x = left + xSize / 2 - fr.getStringWidth(txt) / 2;
    int y = top + fr.FONT_HEIGHT + 6;
    fr.drawStringWithShadow(txt, x, y, ColorUtil.getRGB(Color.WHITE));

    RenderUtil.bindTexture("enderio:textures/gui/poweredSpawner.png");

    if(spawner.isSpawnMode()) {
      int yOff = 34;
      drawTexturedModalRect(left + 80, top + yOff, 207, 0, 17, 15);
      if(spawner.getProgress() < 1 && spawner.getProgress() > 0) {
        int scaled = spawner.getProgressScaled(14) + 1;
        drawTexturedModalRect(left + 81, top + yOff + 14 - scaled, 176, 14 - scaled, 14, scaled);
      }
    } else {
      drawTexturedModalRect(left + 52, top + 40, 52, 170, 72, 21);
      if(spawner.getProgress() < 1 && spawner.getProgress() > 0) {
        int scaled = spawner.getProgressScaled(24);
        drawTexturedModalRect(left + 76, top + 43, 176, 14, scaled + 1, 16);
      }
    }

  }

  @Override
  protected boolean showRecipeButton() {
    return false;
  }

}
