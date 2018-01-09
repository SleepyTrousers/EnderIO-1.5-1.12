package crazypants.enderio.machines.machine.transceiver;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.base.filter.filters.ItemFilter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketItemFilter extends MessageTileEntity<TileTransceiver> {

  private boolean isSend;
  private ItemFilter filter;

  public PacketItemFilter() {
  }

  public PacketItemFilter(@Nonnull TileTransceiver te, boolean isSend) {
    super(te);
    this.isSend = isSend;
    if (isSend) {
      filter = te.getSendItemFilter();
    } else {
      filter = te.getReceiveItemFilter();
    }
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(isSend);
    NBTTagCompound tag = new NBTTagCompound();
    filter.writeToNBT(tag);
    NetworkUtil.writeNBTTagCompound(tag, buf);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    isSend = buf.readBoolean();
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    filter = new ItemFilter();
    filter.readFromNBT(tag);
  }

  public static class Handler implements IMessageHandler<PacketItemFilter, IMessage> {

    @Override
    public IMessage onMessage(PacketItemFilter message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      TileTransceiver tile = message.getTileEntity(player.world);
      if (tile != null && message.filter != null) {
        if (message.isSend) {
          tile.setSendItemFilter(message.filter);
        } else {
          tile.setRecieveItemFilter(message.filter);
        }
      }
      return null;
    }
  }
}
