package crazypants.enderio.machine.invpanel;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.network.MessageTileEntity;

public class PacketGuiSettings extends MessageTileEntity<TileInventoryPanel> implements IMessageHandler<PacketGuiSettings, IMessage> {

  private int sortMode;
  private String filterString;
  private boolean sync;
  
  public PacketGuiSettings() {
    filterString = "";
  }

  public PacketGuiSettings(TileInventoryPanel tile, int sortMode, String filterString, boolean sync) {
    super(tile);
    this.sortMode = sortMode;
    this.filterString = filterString;
    this.sync = sync;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    sortMode = buf.readInt();
    ByteBufUtils.writeUTF8String(buf, filterString);
    sync = buf.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(sortMode);
    filterString = ByteBufUtils.readUTF8String(buf);
    buf.writeBoolean(sync);
  }

  @Override
  public IMessage onMessage(PacketGuiSettings message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileInventoryPanel) {
      TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
      teInvPanel.setGuiParameter(message.sortMode, message.filterString, message.sync);
    }
    return null;
  }
}
