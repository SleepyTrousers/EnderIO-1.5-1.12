package crazypants.enderio.machine.alloy;

import java.awt.Rectangle;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;

import crazypants.enderio.EnderIO;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.gui.GuiToolTip;
import crazypants.gui.IconButton;
import crazypants.render.RenderUtil;

public class GuiAlloySmelter extends GuiMachineBase {

  private TileAlloySmelter tileEntity;
  
  private IconButton vanillaFurnaceButton;
  
  protected static final int SMELT_MODE_BUTTON_ID = REDSTONE_BUTTON_ID + 1;

  public GuiAlloySmelter(InventoryPlayer par1InventoryPlayer, TileAlloySmelter furnaceInventory) {
    super(furnaceInventory, new ContainerAlloySmelter(par1InventoryPlayer, furnaceInventory));
    this.tileEntity = furnaceInventory;
    
    addToolTip(new GuiToolTip(new Rectangle(0, 0, 0, 0), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add("Furnace Mode");
        text.add( tileEntity.areFurnaceRecipesEnabled() ? "All Smelting" : "Alloys Only");
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
    int y = guiTop + 60;
    
    Icon icon = tileEntity.areFurnaceRecipesEnabled() ? EnderIO.blockAlloySmelter.vanillaSmeltingOn : EnderIO.blockAlloySmelter.vanillaSmeltingOff; 
    vanillaFurnaceButton = new IconButton(fontRenderer, SMELT_MODE_BUTTON_ID, x, y, icon, RenderUtil.BLOCK_TEX);
    vanillaFurnaceButton.setSize(BUTTON_SIZE, BUTTON_SIZE);

    buttonList.add(vanillaFurnaceButton);
  }

  @Override
  protected void actionPerformed(GuiButton par1GuiButton) {    
    if (par1GuiButton.id == SMELT_MODE_BUTTON_ID) {
      tileEntity.setFurnaceRecipesEnabled(!tileEntity.areFurnaceRecipesEnabled());
      Icon icon = tileEntity.areFurnaceRecipesEnabled() ? EnderIO.blockAlloySmelter.vanillaSmeltingOn : EnderIO.blockAlloySmelter.vanillaSmeltingOff;      
      vanillaFurnaceButton.setIcon(icon);
      Packet pkt = AlloySmelterPacketProcessor.getSmeltingModePacket(tileEntity);
      PacketDispatcher.sendPacketToServer(pkt);
    } else {
      super.actionPerformed(par1GuiButton); 
    }
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

    if (tileEntity.getProgress() < 1 && tileEntity.getProgress() > 0) {
      scaled = tileEntity.getProgressScaled(12);
      drawTexturedModalRect(sx + 55, sy + 48 - scaled, 176, 12 - scaled, 14, scaled + 2);
      drawTexturedModalRect(sx + 103, sy + 48 - scaled, 176, 12 - scaled, 14, scaled + 2);
    }

    super.drawGuiContainerBackgroundLayer(par1, par2, par3);
  }
}
