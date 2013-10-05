package crazypants.enderio.machine;

import java.awt.Rectangle;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.gui.GuiContainerBase;
import crazypants.gui.GuiToolTip;
import crazypants.gui.IconButton;
import crazypants.render.RenderUtil;

public abstract class GuiMachineBase extends GuiContainerBase {

  protected static final int POWER_Y = 14;
  protected final int POWER_X = 15;
  protected static final int POWER_WIDTH = 10;
  protected static final int POWER_HEIGHT = 42;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  public static final int BUTTON_SIZE = 16;
  protected static final int REDSTONE_BUTTON_ID = 99;

  private AbstractMachineEntity tileEntity;

  private IconButton redstoneButton;

  public GuiMachineBase(AbstractMachineEntity machine, Container container) {
    super(container);
    tileEntity = machine;
    addToolTip(new GuiToolTip(new Rectangle(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(Math.round(tileEntity.getEnergyStored()) + "/" + tileEntity.getCapacitor().getMaxEnergyStored() + " MJ");
      }

    });
    addToolTip(new GuiToolTip(new Rectangle(0, 0, 0, 0), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add("Redstone Mode");
        text.add(tileEntity.getRedstoneControlMode().tooltip);
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
    if (par1GuiButton.id == REDSTONE_BUTTON_ID) {
      int ordinal = tileEntity.getRedstoneControlMode().ordinal();
      ordinal++;
      if (ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      tileEntity.setRedstoneControlMode(RedstoneControlMode.values()[ordinal]);
      redstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(tileEntity.getRedstoneControlMode()));
      Packet pkt = RedstoneModePacketProcessor.getRedstoneControlPacket(tileEntity);
      PacketDispatcher.sendPacketToServer(pkt);
    }
  }

  @Override
  public void initGui() {
    super.initGui();
    int x = guiLeft + xSize - 5 - BUTTON_SIZE;
    int y = guiTop + 5;

    redstoneButton = new IconButton(fontRenderer, REDSTONE_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(tileEntity.getRedstoneControlMode()),
        RenderUtil.BLOCK_TEX);
    redstoneButton.setSize(BUTTON_SIZE, BUTTON_SIZE);

    buttonList.add(redstoneButton);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    int k = (width - xSize) / 2;
    int l = (height - ySize) / 2;
    int i1 = tileEntity.getEnergyStoredScaled(POWER_HEIGHT);
    // x, y, u, v, width, height
    drawTexturedModalRect(k + POWER_X, l + BOTTOM_POWER_Y - i1, 176, 31, POWER_WIDTH, i1);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = (GuiButton) this.buttonList.get(i);
      guibutton.drawButton(this.mc, 0, 0);
    }

  }

}
