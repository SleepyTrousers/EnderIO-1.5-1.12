package crazypants.enderio.machine.alloy;

import java.awt.Rectangle;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.alloy.TileAlloySmelter.Mode;
import crazypants.enderio.network.PacketHandler;
import crazypants.gui.GuiToolTip;
import crazypants.gui.IconButton;
import crazypants.render.RenderUtil;
import crazypants.util.Lang;
import crazypants.vecmath.Vector4f;

public class GuiAlloySmelter extends GuiMachineBase {

  private TileAlloySmelter tileEntity;

  private IconButton vanillaFurnaceButton;

  protected static final int SMELT_MODE_BUTTON_ID = 76;

  public GuiAlloySmelter(InventoryPlayer par1InventoryPlayer, TileAlloySmelter furnaceInventory) {
    super(furnaceInventory, new ContainerAlloySmelter(par1InventoryPlayer, furnaceInventory));
    this.tileEntity = furnaceInventory;

    addToolTip(new GuiToolTip(new Rectangle(0, 0, 0, 0), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(Lang.localize("gui.alloy.mode.heading"));
        String txt = Lang.localize("gui.alloy.mode.all");
        if(tileEntity.getMode() == Mode.ALLOY) {
          txt = Lang.localize("gui.alloy.mode.alloy");
        } else if(tileEntity.getMode() == Mode.FURNACE) {
          txt = Lang.localize("gui.alloy.mode.furnace");
        }
        text.add(txt);
      }

      @Override
      public void onTick(int mouseX, int mouseY) {
        bounds.setBounds(xSize - 5 - BUTTON_SIZE, 60, BUTTON_SIZE, BUTTON_SIZE);
        super.onTick(mouseX, mouseY);
      }

    });
  }

  @Override
  public void initGui() {
    super.initGui();

    int x = guiLeft + xSize - 5 - BUTTON_SIZE;
    int y = guiTop + 62;

    vanillaFurnaceButton = new IconButton(getFontRenderer(), SMELT_MODE_BUTTON_ID, x, y, getIconForMode(), RenderUtil.BLOCK_TEX);
    vanillaFurnaceButton.setSize(BUTTON_SIZE, BUTTON_SIZE);

    buttonList.add(vanillaFurnaceButton);
  }

  @Override
  protected void renderSlotHighlight(int slot, Vector4f col) {
    if(tileEntity.getSlotDefinition().isOutputSlot(slot)) {
      renderSlotHighlight(col, 75, 54, 24, 24);
    } else {
      super.renderSlotHighlight(slot, col);
    }
  }

  @Override
  protected void actionPerformed(GuiButton par1GuiButton) {
    if(par1GuiButton.id == SMELT_MODE_BUTTON_ID) {
      tileEntity.setMode(tileEntity.getMode().next());
      vanillaFurnaceButton.setIcon(getIconForMode());
      PacketHandler.INSTANCE.sendToServer(new PacketClientState(tileEntity));
    } else {
      super.actionPerformed(par1GuiButton);
    }
  }

  private IIcon getIconForMode() {
    IIcon icon = EnderIO.blockAlloySmelter.vanillaSmeltingOn;
    if(tileEntity.getMode() == Mode.ALLOY) {
      icon = EnderIO.blockAlloySmelter.vanillaSmeltingOff;
    } else if(tileEntity.getMode() == Mode.FURNACE) {
      icon = EnderIO.blockAlloySmelter.vanillaSmeltingOnly;
    }
    return icon;
  }

  /**
   * Draw the background layer for the GuiContainer (everything behind the
   * items)
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/alloySmelter.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    int scaled;

    if(tileEntity.getProgress() < 1 && tileEntity.getProgress() > 0) {
      scaled = tileEntity.getProgressScaled(14) + 1;
      drawTexturedModalRect(sx + 55, sy + 49 - scaled, 176, 14 - scaled, 14, scaled);
      drawTexturedModalRect(sx + 103, sy + 49 - scaled, 176, 14 - scaled, 14, scaled);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
}
