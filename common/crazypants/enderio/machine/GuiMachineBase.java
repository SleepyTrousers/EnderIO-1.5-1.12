package crazypants.enderio.machine;

import java.awt.Rectangle;
import java.util.Iterator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.painter.PainterContainer;
import crazypants.render.GuiContainerBase;
import crazypants.render.GuiToolTip;
import crazypants.render.IconButton;
import crazypants.render.ItemButton;
import crazypants.render.RenderUtil;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.network.packet.Packet;

public abstract class GuiMachineBase extends GuiContainerBase {

  
  protected static final int POWER_Y = 13;
  protected final int POWER_X = 15;
  protected static final int POWER_WIDTH = 11;
  protected static final int POWER_HEIGHT = 53;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;
  
  protected static final int BUTTON_SIZE = 16;
  protected static final int REDSTONE_BUTTON_ID = 99;
  
  
  private AbstractMachineEntity tileEntity;

  private IconButton redstoneButton;
  
  public GuiMachineBase(AbstractMachineEntity machine, Container container) {
    super(container);
    tileEntity = machine;
    addToolTip(new GuiToolTip(new Rectangle(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT),"") {

      @Override
      protected void updateText() {        
        text.clear();
        text.add(Math.round(tileEntity.getEnergyStored()) + "/" + tileEntity.getCapacitor().getMaxEnergyStored() + " MJ");
      }
      
    });
    addToolTip(new GuiToolTip(new Rectangle(0, 0, 0, 0),"") {

      @Override
      protected void updateText() {        
        text.clear();
        switch(tileEntity.getRedstoneControlMode()) {
        case ON:
          text.add("Active when");
          text.add("recieving a");
          text.add("redstone signal.");
          break;
        case OFF:
          text.add("Active when not");
          text.add("recieving a");
          text.add("redstone signal.");
          break;
        case IGNORE:
        default:
          text.add("Allways active.");                    
        }
        
      }

      @Override
      public void onTick(int mouseX, int mouseY) {
        bounds.setBounds(xSize - 5 - BUTTON_SIZE, 5, BUTTON_SIZE, BUTTON_SIZE);
        super.onTick(mouseX, mouseY);
      }
      
    });
        
  }  
  
  @Override
  protected void actionPerformed(GuiButton par1GuiButton) {
    if(par1GuiButton.id == REDSTONE_BUTTON_ID) {
      int ordinal = tileEntity.getRedstoneControlMode().ordinal();
      ordinal++;
      if(ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      tileEntity.setRedstoneControlMode(RedstoneControlMode.values()[ordinal]);
      redstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(tileEntity.getRedstoneControlMode()));  
      Packet pkt = PacketHandler.getRedstoneControlPacket(tileEntity);
      PacketDispatcher.sendPacketToServer(pkt);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initGui() {    
    super.initGui();
    int x = guiLeft + xSize - 5 - BUTTON_SIZE;    
    int y = guiTop + 5;    
    
    redstoneButton  = new IconButton(fontRenderer, REDSTONE_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(tileEntity.getRedstoneControlMode()), RenderUtil.BLOCK_TEX);
    redstoneButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
   
    buttonList.add(redstoneButton);
  }



  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);       
    int k = (width - xSize) / 2;
    int l = (height - ySize) / 2;        
    int i1 = tileEntity.getEnergyStoredScaled(POWER_HEIGHT);
    drawTexturedModalRect(k + POWER_X, l + BOTTOM_POWER_Y - i1, 176, 31, 11, i1);
    
    for (int i = 0; i < buttonList.size(); ++i) {
        GuiButton guibutton = (GuiButton)this.buttonList.get(i);
        guibutton.drawButton(this.mc, 0, 0);
    }
    
  }  

}
