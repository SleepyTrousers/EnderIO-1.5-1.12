package crazypants.enderio.invpanel.network;

import crazypants.enderio.invpanel.invpanel.InventoryPanelContainer;
import crazypants.enderio.invpanel.invpanel.TileInventoryPanel;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGuiSettings implements IMessage {

  private int windowId;
  private int sortMode;
  private String filterString;
  private boolean sync;

  public PacketGuiSettings() {
    filterString = "";
  }

  public PacketGuiSettings(int windowId, int sortMode, String filterString, boolean sync) {
    this.windowId = windowId;
    this.sortMode = sortMode;
    this.filterString = filterString;
    this.sync = sync;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    windowId = buf.readInt();
    sortMode = buf.readInt();
    filterString = ByteBufUtils.readUTF8String(buf);
    sync = buf.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(windowId);
    buf.writeInt(sortMode);
    ByteBufUtils.writeUTF8String(buf, filterString);
    buf.writeBoolean(sync);
  }

  public static class Handler implements IMessageHandler<PacketGuiSettings, IMessage> {

    @Override
    public IMessage onMessage(PacketGuiSettings message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      if (player.openContainer.windowId == message.windowId && player.openContainer instanceof InventoryPanelContainer) {
        InventoryPanelContainer ipc = (InventoryPanelContainer) player.openContainer;
        TileInventoryPanel teInvPanel = ipc.getTe();
        teInvPanel.setGuiParameter(message.sortMode, message.filterString, message.sync);
      }
      return null;
    }
  }
}
