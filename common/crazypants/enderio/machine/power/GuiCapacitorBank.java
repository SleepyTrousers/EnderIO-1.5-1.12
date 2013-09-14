package crazypants.enderio.machine.power;

import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.enderio.machine.RedstoneModePacketProcessor;
import crazypants.gui.GuiScreenBase;
import crazypants.gui.GuiToolTip;
import crazypants.gui.IconButton;
import crazypants.render.RenderUtil;

public class GuiCapacitorBank extends GuiScreenBase {

  protected static final int INPUT_BUTTON_ID = 18;
  protected static final int OUTPUT_BUTTON_ID = 37;
  
  private static final int POWER_X = 15;
  private static final int POWER_Y = 7;
  private static final int POWER_WIDTH = 10;
  private static final int POWER_HEIGHT = 75;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  private final TileCapacitorBank capBank;
  
  private IconButton inputRedstoneButton;
  private IconButton outputRedstoneButton;

  public GuiCapacitorBank(TileCapacitorBank te) {    
    super(97,88);
    this.capBank = te;

    addToolTip(new GuiToolTip(new Rectangle(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(BlockCapacitorBank.NF.format(Math.round(capBank.getEnergyStored())) + " MJ");
      }

    });

    addToolTip(new GuiToolTip(new Rectangle(0, 0, 0, 0), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add("Input Redstone Mode");
        text.add(capBank.getInputControlMode().tooltip);
      }

      @Override
      public void onTick(int mouseX, int mouseY) {
        bounds.setBounds(xSize - 5 - GuiMachineBase.BUTTON_SIZE, 5, GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
        super.onTick(mouseX, mouseY);
      }

    });

    addToolTip(new GuiToolTip(new Rectangle(0, 0, 0, 0), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add("Output Redstone Mode");
        text.add(capBank.getOutputControlMode().tooltip);
      }

      @Override
      public void onTick(int mouseX, int mouseY) {
        bounds.setBounds(xSize - 5 - GuiMachineBase.BUTTON_SIZE, 5 + GuiMachineBase.BUTTON_SIZE + 5, GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
        super.onTick(mouseX, mouseY);
      }

    });

  }

  @Override
  protected void actionPerformed(GuiButton par1GuiButton) {
    if (par1GuiButton.id == INPUT_BUTTON_ID) {
      int ordinal = capBank.getInputControlMode().ordinal();
      ordinal++;
      if (ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      capBank.setInputControlMode(RedstoneControlMode.values()[ordinal]);
      inputRedstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(capBank.getInputControlMode()));
      Packet pkt = RedstoneModePacketProcessor.getRedstoneControlPacket(capBank);
      PacketDispatcher.sendPacketToServer(pkt);
    } else if (par1GuiButton.id == OUTPUT_BUTTON_ID) {
      int ordinal = capBank.getOutputControlMode().ordinal();
      ordinal++;
      if (ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      capBank.setOutputControlMode(RedstoneControlMode.values()[ordinal]);
      outputRedstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(capBank.getOutputControlMode()));
      Packet pkt = RedstoneModePacketProcessor.getRedstoneControlPacket(capBank);
      PacketDispatcher.sendPacketToServer(pkt);
    }        
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  public void initGui() {
    super.initGui();
    int x = guiLeft + xSize - 5 - GuiMachineBase.BUTTON_SIZE;
    int y = guiTop + 5;

    inputRedstoneButton = new IconButton(fontRenderer, INPUT_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(capBank.getInputControlMode()),
        RenderUtil.BLOCK_TEX);
    inputRedstoneButton.setSize(GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
    buttonList.add(inputRedstoneButton);
    
    y = y + 5 + GuiMachineBase.BUTTON_SIZE; 
    outputRedstoneButton = new IconButton(fontRenderer, OUTPUT_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(capBank.getOutputControlMode()),
        RenderUtil.BLOCK_TEX);
    outputRedstoneButton.setSize(GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
    buttonList.add(outputRedstoneButton);
  }
  
  
  
  @Override
  protected void keyTyped(char par1, int par2) {
    super.keyTyped(par1, par2);
    if(par1 == 'e') {
      super.keyTyped(par1, 1);
    }
  }

  @Override
  protected void drawBackgroundLayer(float par1, int par2, int par3) {
    
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/capacitorBank.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);
                
    int i1 = capBank.getEnergyStoredScaled(POWER_HEIGHT);
    drawTexturedModalRect(sx + POWER_X, sy + BOTTOM_POWER_Y - i1, 97, 0, POWER_WIDTH, i1);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = (GuiButton) this.buttonList.get(i);
      guibutton.drawButton(this.mc, 0, 0);
    }
    
    int midX =  sx + xSize/2;    
    
    String str = "Max Storage " + BlockCapacitorBank.NF.format(capBank.getMaxEnergyStored()) + " MJ";
    int swid = fontRenderer.getStringWidth(str);
    int x = midX - swid/2;
    int y = guiTop - 5 - fontRenderer.FONT_HEIGHT - 5 - fontRenderer.FONT_HEIGHT;
    
    drawString(fontRenderer, str, x,y,  -1);
    
    str = "Max I/O " + BlockCapacitorBank.NF.format(capBank.getMaxIO()) + " MJ/t";
    swid = fontRenderer.getStringWidth(str);
    x = midX - swid/2;
    y += fontRenderer.FONT_HEIGHT + 5;
    
    drawString(fontRenderer, str, x,y,  -1);

  }

  @Override
  public void drawHoveringText(List par1List, int par2, int par3, FontRenderer font) {
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
    super.drawHoveringText(par1List, par2, par3, font);
    GL11.glPopAttrib();
    GL11.glPopAttrib();
  }

  public int getGuiLeft() {
    return guiLeft;
  }

  public int getGuiTop() {
    return guiTop;
  }

  public int getXSize() {
    return xSize;
  }

  public FontRenderer getFontRenderer() {
    return fontRenderer;
  }

 
}
