package crazypants.enderio.machine.invpanel;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.MessageTileEntity;
import io.netty.buffer.ByteBuf;
import java.io.UnsupportedEncodingException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;

public class PacketGuiSettings extends MessageTileEntity<TileInventoryPanel> implements IMessageHandler<PacketGuiSettings, IMessage> {

  private int sortMode;
  private String filterString;

  public PacketGuiSettings() {
    filterString = "";
  }

  public PacketGuiSettings(TileInventoryPanel tile, int sortMode, String filterString) {
    super(tile);
    this.sortMode = sortMode;
    this.filterString = filterString;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    sortMode = buf.readInt();
    int len = buf.readUnsignedShort();
    if(len > 0) {
      byte[] utf8 = new byte[len];
      buf.readBytes(utf8);
      try {
        filterString = new String(utf8, "UTF8");
      } catch (UnsupportedEncodingException ex) {
        // should not happen - if it does we can't do anything
      }
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(sortMode);
    if(filterString.isEmpty()) {
      buf.writeShort(0);
    } else {
      try {
        byte[] utf8 = filterString.getBytes("UTF8");
        buf.writeShort((short) utf8.length);
        buf.writeBytes(utf8);
      } catch (UnsupportedEncodingException ex) {
        // should not happen - if it does we can't do anything
        buf.writeShort(0);
      }
    }
  }

  @Override
  public IMessage onMessage(PacketGuiSettings message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileInventoryPanel) {
      TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
      teInvPanel.setGuiParameter(message.sortMode, message.filterString);
    }
    return null;
  }
}
