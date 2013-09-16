package crazypants.enderio.machine.hypercube;

import static crazypants.enderio.machine.GuiMachineBase.BUTTON_SIZE;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.packet.Packet;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.PacketDispatcher;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.gui.IconButtonEIO;
import crazypants.enderio.gui.IconEIO;
import crazypants.enderio.gui.IconToggleButtonEIO;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.GuiMachineBase;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.gui.GuiIconRenderer;
import crazypants.gui.GuiScreenBase;
import crazypants.gui.GuiScrollableList;
import crazypants.gui.GuiToolTip;
import crazypants.gui.IconButton;
import crazypants.gui.ListSelectionListener;
import crazypants.gui.ToggleButton;
import crazypants.render.ColorUtil;
import crazypants.render.RenderUtil;
import crazypants.util.PacketUtil;

public class GuiHyperCube extends GuiScreenBase {

  protected static final int POWER_INPUT_BUTTON_ID = 1;
  protected static final int POWER_OUTPUT_BUTTON_ID = 2;
  protected static final int ADD_BUTTON_ID = 3;
  protected static final int PRIVATE_BUTTON_ID = 4;

  protected static final int SELECT_PRIVATE_BUTTON_ID = 5;
  protected static final int SELECT_PUBLIC_BUTTON_ID = 6;

  protected static final int DELETE_PRIVATE_BUTTON_ID = 7;
  protected static final int DELETE_PUBLIC_BUTTON_ID = 8;

  private static final int POWER_X = 227;
  private static final int POWER_Y = 46;
  private static final int POWER_WIDTH = 10;
  private static final int POWER_HEIGHT = 66;
  protected static final int BOTTOM_POWER_Y = POWER_Y + POWER_HEIGHT;

  private final TileHyperCube cube;

  private IconButton powerInputRedstoneButton;
  private IconButton powerOutputRedstoneButton;

  private IconButtonEIO addButton;
  private IconToggleButtonEIO privateButton;

  private GuiTextField newChannelTF;

  private GuiChannelList publicChannelList;
  private GuiChannelList privateChannelList;
  private ListSelectionListener<Channel> selectionListener;

  private IconButtonEIO selectPublicB;
  private IconButtonEIO deletePublicB;

  private IconButtonEIO selectPrivateB;
  private IconButtonEIO deletePrivateB;

  public GuiHyperCube(TileHyperCube te) {
    super(245, 145);
    this.cube = te;

    addToolTip(new GuiToolTip(new Rectangle(POWER_X, POWER_Y, POWER_WIDTH, POWER_HEIGHT), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add(BlockHyperCube.NF.format(Math.round(cube.powerHandler.getEnergyStored())) + " MJ");
      }

    });

    addToolTip(new GuiToolTip(new Rectangle(198, 12, BUTTON_SIZE, BUTTON_SIZE), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add("Input Redstone Mode");
        text.add(cube.getInputControlMode().tooltip);
      }

    });
    addToolTip(new GuiToolTip(new Rectangle(219, 12, GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE), "") {

      @Override
      protected void updateText() {
        text.clear();
        text.add("Output Redstone Mode");
        text.add(cube.getOutputControlMode().tooltip);
      }

    });

    addButton = new IconButtonEIO(this, ADD_BUTTON_ID, 137, 12, IconEIO.PLUS);
    addButton.setToolTip("Add Channel");
    addButton.enabled = false;

    privateButton = new IconToggleButtonEIO(this, PRIVATE_BUTTON_ID, 118, 12, IconEIO.PUBLIC, IconEIO.PRIVATE);
    privateButton.setSelectedToolTip("Private Channel");
    privateButton.setUnselectedToolTip("Public Channel");

    deletePublicB = new IconButtonEIO(this, DELETE_PUBLIC_BUTTON_ID, 74, 117, IconEIO.MINUS);
    deletePublicB.setToolTip("Delete Channel");
    selectPublicB = new IconButtonEIO(this, SELECT_PUBLIC_BUTTON_ID, 95, 117, IconEIO.TICK);
    selectPublicB.setToolTip("Activate Channel");

    deletePrivateB = new IconButtonEIO(this, DELETE_PRIVATE_BUTTON_ID, 183, 117, IconEIO.MINUS);
    deletePrivateB.setToolTip("Delete Channel");
    selectPrivateB = new IconButtonEIO(this, SELECT_PRIVATE_BUTTON_ID, 204, 117, IconEIO.TICK);
    selectPrivateB.setToolTip("Activate Channel");

    
    int w = 104;
    int h = 68;
    int x = 7;
    int y = 45;
    
    Channel activeChannel = cube.getChannel();
    publicChannelList = new GuiChannelList(this, w, h, x, y);
    publicChannelList.setChannels(ClientChannelRegister.instance.getPublicChannels());
    publicChannelList.setShowSelectionBox(true);
    publicChannelList.setScrollButtonIds(100, 101);
    publicChannelList.setActiveChannel(isPublic(activeChannel) ? activeChannel : null);

    x = x + 5 + w;
    privateChannelList = new GuiChannelList(this, w, h, x, y);
    privateChannelList.setChannels(ClientChannelRegister.instance.getPrivateChannels());
    privateChannelList.setShowSelectionBox(true);
    privateChannelList.setScrollButtonIds(102, 103);
    privateChannelList.setActiveChannel(isPrivate(activeChannel) ? activeChannel : null);

    selectionListener = new ListSelectionListener<Channel>() {

      @Override
      public void selectionChanged(GuiScrollableList<Channel> list, int selectedIndex) {
        Channel selected = list.getSelectedElement();
        if (selected != null) {
          GuiChannelList clear = list == publicChannelList ? privateChannelList : publicChannelList;
          clear.setSelection(-1);
        }

      }
    };

    publicChannelList.addSelectionListener(selectionListener);
    privateChannelList.addSelectionListener(selectionListener);

  }
  
  private boolean isPublic(Channel chan) {
    if(chan == null) {
      return false;
    }
    return chan.isPublic();
  }
  
  private boolean isPrivate(Channel chan) {
    if(chan == null) {
      return false;
    }
    return !chan.isPublic();
  }

  @Override
  public void initGui() {
    super.initGui();

    buttonList.clear();

    int x = guiLeft + 203;
    int y = guiTop + 12;

    powerInputRedstoneButton = new IconButton(fontRenderer, POWER_INPUT_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(cube
        .getInputControlMode()),
        RenderUtil.BLOCK_TEX);
    powerInputRedstoneButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
    buttonList.add(powerInputRedstoneButton);

    x = x + 5 + BUTTON_SIZE;
    powerOutputRedstoneButton = new IconButton(fontRenderer, POWER_OUTPUT_BUTTON_ID, x, y, AbstractMachineBlock.getRedstoneControlIcon(cube
        .getOutputControlMode()),
        RenderUtil.BLOCK_TEX);
    powerOutputRedstoneButton.setSize(GuiMachineBase.BUTTON_SIZE, GuiMachineBase.BUTTON_SIZE);
    buttonList.add(powerOutputRedstoneButton);

    y = guiTop + 12;
    x = guiLeft + 8;
    newChannelTF = new GuiTextField(fontRenderer, x, y, 103, 16);
    newChannelTF.setCanLoseFocus(false);
    newChannelTF.setMaxStringLength(32);
    newChannelTF.setFocused(true);

    privateButton.onGuiInit();
    addButton.onGuiInit();
    selectPrivateB.onGuiInit();
    selectPublicB.onGuiInit();
    deletePrivateB.onGuiInit();
    deletePublicB.onGuiInit();

    publicChannelList.onGuiInit(this);
    privateChannelList.onGuiInit(this);

  }

  @Override
  protected void actionPerformed(GuiButton par1GuiButton) {
    if (par1GuiButton.id == POWER_INPUT_BUTTON_ID) {
      int ordinal = cube.getInputControlMode().ordinal();
      ordinal++;
      if (ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      cube.setInputControlMode(RedstoneControlMode.values()[ordinal]);
      powerInputRedstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(cube.getInputControlMode()));
      Packet pkt = HyperCubePacketHandler.createRedstoneControlPacket(cube);
      PacketDispatcher.sendPacketToServer(pkt);
    } else if (par1GuiButton.id == POWER_OUTPUT_BUTTON_ID) {
      int ordinal = cube.getOutputControlMode().ordinal();
      ordinal++;
      if (ordinal >= RedstoneControlMode.values().length) {
        ordinal = 0;
      }
      cube.setOutputControlMode(RedstoneControlMode.values()[ordinal]);
      powerOutputRedstoneButton.setIcon(AbstractMachineBlock.getRedstoneControlIcon(cube.getOutputControlMode()));
      Packet pkt = HyperCubePacketHandler.createRedstoneControlPacket(cube);
      PacketDispatcher.sendPacketToServer(pkt);
    } else if (par1GuiButton.id == ADD_BUTTON_ID) {

      Channel c;
      if (privateButton.isSelected()) {
        c = new Channel(newChannelTF.getText(), Minecraft.getMinecraft().thePlayer.username);
      } else {
        c = new Channel(newChannelTF.getText(), null);
      }
      ClientChannelRegister.instance.addChannel(c);
      Packet pkt = HyperCubePacketHandler.createAddRemoveChannelPacket(c,true);
      PacketDispatcher.sendPacketToServer(pkt);

      if (privateButton.isSelected()) {
        privateChannelList.setSelection(c);
      } else {
        publicChannelList.setSelection(c);
      }

    } else if (par1GuiButton.id == SELECT_PUBLIC_BUTTON_ID || par1GuiButton.id == SELECT_PRIVATE_BUTTON_ID) {
      Channel c = (par1GuiButton.id == SELECT_PUBLIC_BUTTON_ID) ? publicChannelList.getSelectedElement() : privateChannelList.getSelectedElement();
      if (c != null) {
        setActiveChannel(c);
      }
    } else if (par1GuiButton.id == DELETE_PRIVATE_BUTTON_ID || par1GuiButton.id == DELETE_PUBLIC_BUTTON_ID) {
      Channel c = (par1GuiButton.id == DELETE_PUBLIC_BUTTON_ID) ? publicChannelList.getSelectedElement() : privateChannelList.getSelectedElement();
      if (c != null) {
        if (c.equals(cube.getChannel())) {
          setActiveChannel(null);
        }        
        ClientChannelRegister.instance.channelRemoved(c);        
        Packet pkt = HyperCubePacketHandler.createAddRemoveChannelPacket(c, false);
        PacketDispatcher.sendPacketToServer(pkt);
      }
    }

  }

  private void setActiveChannel(Channel c) {
    cube.setChannel(c);
    publicChannelList.setActiveChannel(isPublic(c) ? c : null);
    privateChannelList.setActiveChannel(isPrivate(c) ? c : null);
    Packet pkt = HyperCubePacketHandler.createChannelSelectedPacket(cube, c);
    PacketDispatcher.sendPacketToServer(pkt);
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

    boolean chanSel = publicChannelList.getSelectedElement() != null;
    selectPublicB.enabled = chanSel;
    deletePublicB.enabled = chanSel;

    chanSel = privateChannelList.getSelectedElement() != null;
    selectPrivateB.enabled = chanSel;
    deletePrivateB.enabled = chanSel;

    newChannelTF.drawTextBox();
    publicChannelList.drawScreen(mouseX, mouseY, partialTick);
    privateChannelList.drawScreen(mouseX, mouseY, partialTick);

    for (int i = 0; i < buttonList.size(); ++i) {
      GuiButton guibutton = (GuiButton) this.buttonList.get(i);
      guibutton.drawButton(this.mc, 0, 0);
    }

    int x = guiLeft + 12;
    int y = guiTop + 35;
    int rgb = ColorUtil.getRGB(Color.white);
    drawString(fontRenderer, "Public", x, y, rgb);

    x += 109;
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
