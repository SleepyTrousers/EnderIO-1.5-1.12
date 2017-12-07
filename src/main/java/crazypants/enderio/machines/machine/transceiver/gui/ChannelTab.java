package crazypants.enderio.machines.machine.transceiver.gui;

import java.awt.Color;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.ITabPanel;
import com.enderio.core.api.client.gui.ListSelectionListener;
import com.enderio.core.client.gui.button.IconButton;
import com.enderio.core.client.gui.button.ToggleButton;
import com.enderio.core.client.gui.widget.GuiScrollableList;
import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.UserIdent;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.base.transceiver.Channel;
import crazypants.enderio.base.transceiver.ChannelType;
import crazypants.enderio.base.transceiver.ClientChannelRegister;
import crazypants.enderio.base.transceiver.PacketAddRemoveChannel;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.transceiver.PacketSendRecieveChannel;
import crazypants.enderio.machines.machine.transceiver.TileTransceiver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class ChannelTab implements ITabPanel {

  protected static final int ADD_BUTTON_ID = 3;
  protected static final int PRIVATE_BUTTON_ID = 4;
  private static final int DELETE_CHANNEL_BUTTON_ID = 5;

  private static final int SEND_BUTTON_ID = 6;
  private static final int RECIEVE_BUTTON_ID = 7;

  final @Nonnull ChannelType type;
  final @Nonnull GuiTransceiver parent;

  final @Nonnull IconButton addButton;
  final @Nonnull ToggleButton privateButton;

  final @Nonnull GuiTextField newChannelTF;
  final @Nonnull GuiChannelList channelList;

  final @Nonnull GuiChannelList sendChannels;
  final @Nonnull GuiChannelList recieveChannels;

  final @Nonnull IconButton deleteChannelB;
  final @Nonnull IconButton sendB;
  final @Nonnull IconButton recieveB;

  final @Nonnull ListSelectionListener<Channel> selectionListener;
  final @Nonnull TileTransceiver transceiver;

  public ChannelTab(@Nonnull GuiTransceiver guiTransceiver, @Nonnull ChannelType type) {
    parent = guiTransceiver;
    this.type = type;
    transceiver = guiTransceiver.getTransciever();

    newChannelTF = new GuiTextField(76543, parent.getFontRenderer(), 7, 12, 103, 16);
    addButton = new IconButton(parent, ADD_BUTTON_ID, 137, 12, IconEIO.PLUS);
    addButton.setToolTip(Lang.GUI_TRANS_CHANNEL_ADD.get());
    addButton.enabled = false;

    privateButton = new ToggleButton(parent, PRIVATE_BUTTON_ID, 118, 12, IconEIO.LOCK_UNLOCKED, IconEIO.LOCK_LOCKED);
    privateButton.setSelectedToolTip(Lang.GUI_TRANS_CHANNEL_PRIVATE.get());
    privateButton.setUnselectedToolTip(Lang.GUI_TRANS_CHANNEL_PUBLIC.get());

    int w = 104;
    int h = 90;
    int x = 7;
    int y = 48;
    channelList = new GuiChannelList(parent, w, h, x, y);
    channelList.setChannels(ClientChannelRegister.instance.getChannelsForType(type), Predicates.<Channel> alwaysTrue());
    channelList.setShowSelectionBox(true);
    channelList.setScrollButtonIds(100, 101);

    deleteChannelB = new IconButton(parent, DELETE_CHANNEL_BUTTON_ID, x + w - 20, y + h + 4, IconEIO.MINUS);
    deleteChannelB.setToolTip(Lang.GUI_TRANS_CHANNEL_DELETE.get());

    Predicate<Channel> predicate = new Predicate<Channel>() {
      @Override
      public boolean apply(@Nullable Channel input) {
        return input != null
            && (input.isPublic() || input.getUser().equals(EnderIO.proxy.getClientPlayer().getGameProfile()) || input.getUser() == UserIdent.NOBODY);
      }

      @Override
      public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
      }

      @Override
      public int hashCode() {
        return super.hashCode();
      }
    };

    x += w + 32;
    h = 35;
    sendChannels = new GuiChannelList(parent, w, h, x, y);
    sendChannels.setChannels(transceiver.getSendChannels(type), predicate);
    sendChannels.setShowSelectionBox(true);
    sendChannels.setScrollButtonIds(200, 201);

    sendB = new IconButton(parent, SEND_BUTTON_ID, x - 24, y + h / 2 - 9, IconEIO.ARROWS);

    y += h + 20;
    recieveChannels = new GuiChannelList(parent, w, h, x, y);
    recieveChannels.setChannels(transceiver.getRecieveChannels(type), predicate);
    recieveChannels.setShowSelectionBox(true);
    recieveChannels.setScrollButtonIds(300, 301);

    recieveB = new IconButton(parent, RECIEVE_BUTTON_ID, x - 24, y + h / 2 - 9, IconEIO.ARROWS);

    selectionListener = new ListSelectionListener<Channel>() {

      @Override
      public void selectionChanged(@Nonnull GuiScrollableList<Channel> list, int selectedIndex) {
        if (selectedIndex < 0) {
          return;
        }
        if (list != channelList) {
          channelList.setSelection(-1);
        }
        if (list != sendChannels) {
          sendChannels.setSelection(-1);
        }
        if (list != recieveChannels) {
          recieveChannels.setSelection(-1);
        }
      }
    };
    channelList.addSelectionListener(selectionListener);
    sendChannels.addSelectionListener(selectionListener);
    recieveChannels.addSelectionListener(selectionListener);

  }

  @Override
  public void onGuiInit(int x, int y, int width, int height) {
    addButton.onGuiInit();
    privateButton.onGuiInit();
    deleteChannelB.onGuiInit();
    sendB.onGuiInit();
    recieveB.onGuiInit();

    y = parent.getGuiTop() + 12;
    x = parent.getGuiLeft() + 8;
    newChannelTF.xPosition = x;
    newChannelTF.yPosition = y;
    newChannelTF.setCanLoseFocus(false);
    newChannelTF.setMaxStringLength(32);
    newChannelTF.setFocused(true);

    channelList.onGuiInit(parent);
    sendChannels.onGuiInit(parent);
    recieveChannels.onGuiInit(parent);
  }

  @Override
  public void deactivate() {
    addButton.detach();
    privateButton.detach();
    deleteChannelB.detach();
    sendB.detach();
    recieveB.detach();
  }

  @Override
  public void keyTyped(char par1, int par2) {
    newChannelTF.textboxKeyTyped(par1, par2);
    addButton.enabled = newChannelTF.getText().trim().length() > 0;
  }

  @Override
  public @Nonnull IconEIO getIcon() {
    switch (type) {
    case FLUID:
      return IconEIO.WRENCH_OVERLAY_FLUID;
    case ITEM:
      return IconEIO.WRENCH_OVERLAY_ITEM;
    case POWER:
      return IconEIO.WRENCH_OVERLAY_POWER;
    default:
      return IconEIO.WRENCH_OVERLAY_POWER;
    }
  }

  @Override
  public void updateScreen() {
    newChannelTF.updateCursorCounter();
  }

  @Override
  public void render(float partialTick, int mouseX, int mouseY) {
    newChannelTF.drawTextBox();
    channelList.drawScreen(mouseX, mouseY, partialTick);
    sendChannels.drawScreen(mouseX, mouseY, partialTick);
    recieveChannels.drawScreen(mouseX, mouseY, partialTick);

    int left = parent.getGuiLeft();
    int top = parent.getGuiTop();
    int x = left + 59;
    int y = top + 36;
    parent.drawCenteredString(parent.getFontRenderer(), Lang.GUI_TRANS_AVAILABLE.get(), x, y, ColorUtil.getRGB(Color.white));

    x = left + 199;
    parent.drawCenteredString(parent.getFontRenderer(), Lang.GUI_TRANS_SEND.get(), x, y, ColorUtil.getRGB(Color.white));

    y += 56;
    parent.drawCenteredString(parent.getFontRenderer(), Lang.GUI_TRANS_RECEIVE.get(), x, y, ColorUtil.getRGB(Color.white));
  }

  @Override
  public void actionPerformed(@Nonnull GuiButton guiButton) {
    if (guiButton.id == ADD_BUTTON_ID) {
      addChannelPressed();
    } else if (guiButton.id == DELETE_CHANNEL_BUTTON_ID) {
      deleteChannelPressed();
    } else if (guiButton.id == SEND_BUTTON_ID) {
      sendTogglePressed();
    } else if (guiButton.id == RECIEVE_BUTTON_ID) {
      receiveTogglePressed();
    }
  }

  protected void receiveTogglePressed() {
    Channel c = channelList.getSelectedElement();
    if (c != null && !transceiver.getRecieveChannels(type).contains(c)) {
      transceiver.addRecieveChanel(c);
      PacketHandler.INSTANCE.sendToServer(new PacketSendRecieveChannel(transceiver, false, true, c));
    } else {
      c = recieveChannels.getSelectedElement();
      if (c != null) {
        transceiver.removeRecieveChanel(c);
        PacketHandler.INSTANCE.sendToServer(new PacketSendRecieveChannel(transceiver, false, false, c));
      }
    }
  }

  protected void sendTogglePressed() {
    Channel c = channelList.getSelectedElement();
    if (c != null && !transceiver.getSendChannels(type).contains(c)) {
      transceiver.addSendChanel(c);
      PacketHandler.INSTANCE.sendToServer(new PacketSendRecieveChannel(transceiver, true, true, c));
    } else {
      c = sendChannels.getSelectedElement();
      if (c != null) {
        transceiver.removeSendChanel(c);
        PacketHandler.INSTANCE.sendToServer(new PacketSendRecieveChannel(transceiver, true, false, c));
      }
    }
  }

  private void deleteChannelPressed() {
    Channel c = channelList.getSelectedElement();
    if (c != null) {
      ClientChannelRegister.instance.removeChannel(c);
      PacketHandler.INSTANCE.sendToServer(new PacketAddRemoveChannel(c, false));
    }
  }

  private void addChannelPressed() {
    if (newChannelTF.getText().trim().isEmpty()) {
      return;
    }
    Channel c;
    if (privateButton.isSelected()) {
      c = new Channel(newChannelTF.getText(), Minecraft.getMinecraft().player.getGameProfile(), type);
    } else {
      c = new Channel(newChannelTF.getText(), type);
    }
    ClientChannelRegister.instance.addChannel(c);
    PacketHandler.INSTANCE.sendToServer(new PacketAddRemoveChannel(c, true));
    channelList.setSelection(c);
    newChannelTF.setText("");
  }

  @Override
  public void mouseClicked(int x, int y, int par3) {
    newChannelTF.mouseClicked(x, y, par3);
  }

}
