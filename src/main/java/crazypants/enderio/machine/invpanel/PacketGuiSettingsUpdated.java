package crazypants.enderio.machine.invpanel;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGuiSettingsUpdated extends MessageTileEntity<TileInventoryPanel> implements IMessageHandler<PacketGuiSettingsUpdated, IMessage> {

  private int sortMode;
  private String filterString;
  private boolean sync;

  public PacketGuiSettingsUpdated() {
  }

  public PacketGuiSettingsUpdated(TileInventoryPanel tile) {
    super(tile);
    this.sortMode = tile.getGuiSortMode();
    this.filterString = tile.getGuiFilterString();
    this.sync = tile.getGuiSync();
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    sortMode = buf.readInt();
    filterString = ByteBufUtils.readUTF8String(buf);
    sync = buf.readBoolean();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(sortMode);
    ByteBufUtils.writeUTF8String(buf, filterString);
    buf.writeBoolean(sync);
  }

  @Override
  public IMessage onMessage(PacketGuiSettingsUpdated message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.world.getTileEntity(message.getPos());
    if (te instanceof TileInventoryPanel) {
      TileInventoryPanel teInvPanel = (TileInventoryPanel) te;
      teInvPanel.setGuiParameter(message.sortMode, message.filterString, message.sync);
    }
    return null;
  }

}
