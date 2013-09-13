package crazypants.enderio.machine.hypercube;

import static crazypants.enderio.machine.GuiMachineBase.BUTTON_SIZE;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.render.GuiIconRenderer;
import crazypants.render.GuiScreenBase;
import crazypants.render.GuiToolTip;
import crazypants.render.IconButton;
import crazypants.render.RenderUtil;
import crazypants.render.ToggleButton;

public class GuiHyperCube extends GuiScreenBase {

  protected static final int POWER_INPUT_BUTTON_ID = 18;
  protected static final int POWER_OUTPUT_BUTTON_ID = 37;
  protected static final int ADD_BUTTON_ID = 42;
  protected static final int PRIVATE_BUTTON_ID = 76;

  private static final int POWER_X = 222;
  private static final int POWER_Y = 148;
  private static final int POWER_WIDTH = 10;
  private static final int POWER_HEIGHT = 46;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  private final TileHyperCube cube;

  private IconButton powerInputRedstoneButton;
  private IconButton powerOutputRedstoneButton;
  private GuiIconRenderer powerIcon;

  private IconButton addButton;
  private ToggleButton privateButton;

  private GuiTextField newChannelTF;

  private GuiChannelList publicChannelList;
  private GuiChannelList privateChannelList;

  public GuiHyperCube(TileHyperCube te) {
    super(245, 199);
    this.cube = te;

    addToolTip(new GuiToolTip(new Rectangle(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(BlockHyperCube.NF.format(Math.round(cube.powerHandler.getEnergyStored())) + " MJ");
      }

    });

    addToolTip(new GuiToolTip(new Rectangle(188, 152, BUTTON_SIZE, BUTTON_SIZE), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add("Input Redstone Mode");
        text.add(cube.getPowerInputControlMode().tooltip);
      }

    });
    addToolTip(new GuiToolTip(new Rectangle(188, 173, GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add("Output Redstone Mode");
        text.add(cube.getPowerOutputControlMode().tooltip);
      }

    });
    
    addToolTip(new GuiToolTip(new Rectangle(121, 8, GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE), "Private Channel"));
    addToolTip(new GuiToolTip(new Rectangle(142, 8, GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE), "Add Channel"));

  }

  @Override
  public void initGui() {
    super.initGui();

    buttonList.clear();
    
    int x = guiLeft + 169;
    int y = guiTop + 161;

    powerIcon = new GuiIconRenderer(x, y + 1, ModObject.itemPowerConduit.actualId, 0);
    powerIcon.setAlpha(0.5F);
    powerIcon.setSize(14, 14);

    x = guiLeft + 188;
    y = guiTop + 152;

    powerInputRedstoneButton = new IconButton(fontRenderer, POWER_INPUT_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(cube
        .getPowerInputControlMode()),
        RenderUtil.BLOCK_TEX);
    powerInputRedstoneButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
    buttonList.add(powerInputRedstoneButton);

    y = y + 5 + BUTTON_SIZE;
    powerOutputRedstoneButton = new IconButton(fontRenderer, POWER_OUTPUT_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(cube
        .getPowerOutputControlMode()),
        RenderUtil.BLOCK_TEX);
    powerOutputRedstoneButton.setSize(GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
    buttonList.add(powerOutputRedstoneButton);

    y = guiTop + 12;
    x = guiLeft + 8;
    newChannelTF = new GuiTextField(fontRenderer, x, y, 106, 16);
    newChannelTF.setCanLoseFocus(false);
    newChannelTF.setMaxStringLength(64);
    newChannelTF.setFocused(true);

    x = x + 110;
    boolean selected = false;
    if(privateButton != null) {
      selected = privateButton.isSelected();
    }
    privateButton = new ToggleButton(fontRenderer, PRIVATE_BUTTON_ID, x, y, EnderIO.blockHyperCube.lockIcon, RenderUtil.BLOCK_TEX);
    privateButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
    privateButton.setSelected(selected);
    buttonList.add(privateButton);
    
    x = x + 5 + BUTTON_SIZE;
    addButton = new IconButton(fontRenderer, ADD_BUTTON_ID, x, y, EnderIO.blockHyperCube.addIcon, RenderUtil.BLOCK_TEX);
    addButton.enabled = false;
    addButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
    buttonList.add(addButton);

    int w = 104;
    int h = 68;
    x = guiLeft + 7;
    y = guiTop + 45;
    publicChannelList = new GuiChannelList(this, w, h, x, y);
    publicChannelList.setChannels(ClientChannelRegister.instance.getPublicChannels());
    publicChannelList.setShowSelectionBox(true);
    publicChannelList.setScrollButtonIds(87, 88);

    x = x + 15 + w;
    privateChannelList = new GuiChannelList(this, w, h, x, y);
    privateChannelList.setChannels(ClientChannelRegister.instance.getPrivateChannels());
    privateChannelList.setShowSelectionBox(true);
    privateChannelList.setScrollButtonIds(89, 90);

  }

  @Override
  protected void actionPerformed(GuiButton par1GuiButton) {
    if (par1GuiButton.id == POWER_INPUT_BUTTON_ID) {
      int ordinal = cube.getPowerInputControlMode().ordinal();
      ordinal++;
      if (ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      cube.setPowerInputControlMode(RedstoneControlMode.values()[ordinal]);
      powerInputRedstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(cube.getPowerInputControlMode()));
      Packet pkt = HyperCubePacketHandler.createRedstoneControlPacket(cube);
      PacketDispatcher.sendPacketToServer(pkt);
    } else if (par1GuiButton.id == POWER_OUTPUT_BUTTON_ID) {
      int ordinal = cube.getPowerOutputControlMode().ordinal();
      ordinal++;
      if (ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      cube.setPowerOutputControlMode(RedstoneControlMode.values()[ordinal]);
      powerOutputRedstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(cube.getPowerOutputControlMode()));
      Packet pkt = HyperCubePacketHandler.createRedstoneControlPacket(cube);
      PacketDispatcher.sendPacketToServer(pkt);
    } else if (par1GuiButton.id == ADD_BUTTON_ID) {

      Channel c = new Channel(newChannelTF.getText(), null);
      ClientChannelRegister.instance.addChannel(c);
      Packet pkt = HyperCubePacketHandler.createAddChannelPacket(c);
      PacketDispatcher.sendPacketToServer(pkt);

    } else if (par1GuiButton.id == PRIVATE_BUTTON_ID) {
      privateButton.setSelected(!privateButton.isSelected());
    }
  }

  @Override
  public boolean doesGuiPauseGame() {
    return false;
  }

  @Override
  protected void keyTyped(char par1, int par2) {
    super.keyTyped(par1, par2);
    newChannelTF.textboxKeyTyped(par1, par2);
    addButton.enabled = newChannelTF.getText().trim().length() > 0;
    super.keyTyped(par1, par2);
  }

  @Override
  protected void mouseClicked(int par1, int par2, int par3) {
    super.mouseClicked(par1, par2, par3);
    newChannelTF.mouseClicked(par1, par2, par3);
  }

  @Override
  public void updateScreen() {
    newChannelTF.updateCursorCounter();
  }

  @Override
  protected void drawBackgroundLayer(float partialTick, int mouseX, int mouseY) {

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    RenderUtil.bindTexture("enderio:textures/gui/hyperCube.png");
    int sx = (width - xSize) / 2;
    int sy = (height - ySize) / 2;

    drawTexturedModalRect(sx, sy, 0, 0, this.xSize, this.ySize);

    int i1 = cube.getEnergyStoredScaled(POWER_HEIGHT);
    drawTexturedModalRect(sx + POWER_X, sy + BOTTOM_POWER_Y - i1, 245, 0, POWER_WIDTH, i1);

    powerIcon.draw();
    newChannelTF.drawTextBox();
    publicChannelList.drawScreen(mouseX, mouseY, partialTick);
    privateChannelList.drawScreen(mouseX, mouseY, partialTick);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = (GuiButton) this.buttonList.get(i);
      guibutton.drawButton(this.mc, 0, 0);
    }

    int x = guiLeft + 12;
    int y = guiTop + 35;
    // int rgb = RenderUtil.getRGB(62,128,120);
    int rgb = RenderUtil.getRGB(Color.white);
    drawString(fontRenderer, "Public", x, y, rgb);

    x += 119;
    drawString(fontRenderer, "Private", x, y, rgb);

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
