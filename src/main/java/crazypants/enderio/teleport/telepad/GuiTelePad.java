package crazypants.enderio.teleport.telepad;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.ToggleButtonEIO;
import crazypants.enderio.teleport.ContainerTravelAccessable;
import crazypants.enderio.teleport.GuiTravelAccessable;
import crazypants.gui.GuiContainerBase;
import crazypants.render.RenderUtil;

public class GuiTelePad extends GuiContainerBase {

  private static final int ID_SWITCH_BUTTON = 99;

  private GuiTravelAccessable behind;
  private boolean showBehind;

  ToggleButtonEIO switchButton;
  ToggleButtonEIO behindSwitchButton;

  public GuiTelePad(InventoryPlayer playerInv, ITravelAccessable te, World world) {
    super(new ContainerTravelAccessable(playerInv, te, world));
    behind = new GuiTravelAccessable(playerInv, te, world);

    switchButton = new ToggleButtonEIO(this, ID_SWITCH_BUTTON, 50, 10, IconEIO.BUTTON, IconEIO.CHECKED_BUTTON);
  }

  @Override
  public void initGui() {
    super.initGui();
    switchButton.onGuiInit();

    behind.width = this.width;
    behind.height = this.height;
    behind.setGuiLeft(this.guiLeft);
    behind.setGuiTop(this.guiTop);
//    behind.setXSize(this.xSize);
//    behind.setYSize(this.ySize);
    behind.mc = this.mc;
    behind.initGui();
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
    if(showBehind) {
      behind.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
    } else {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtil.bindTexture("enderio:textures/gui/telePad.png");
      int sx = (width - xSize) / 2;
      int sy = (height - ySize) / 2;

      drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
    }
  }

  @Override
  protected void actionPerformed(GuiButton button) {
    super.actionPerformed(button);
    switch (button.id) {
    case ID_SWITCH_BUTTON:
      showBehind = !showBehind;
      break;
    }
  }
}
