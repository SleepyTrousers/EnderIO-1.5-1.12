package crazypants.enderio.invpanel.remote;

import info.loenwind.autosave.util.NBTAction;
import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.autosave.BaseHandlers;
import crazypants.enderio.invpanel.invpanel.TileInventoryPanel;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

public class PacketPrimeInventoryPanelRemote implements IMessage {

  private NBTTagCompound tag;

  public PacketPrimeInventoryPanelRemote() {
  }

  public PacketPrimeInventoryPanelRemote(@Nonnull TileInventoryPanel te) {
    tag = new NBTTagCompound();
    Writer.write(BaseHandlers.REGISTRY, tag, te);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    tag = NetworkUtil.readNBTTagCompound(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    NetworkUtil.writeNBTTagCompound(tag, buf);
  }

  public static class Handler implements IMessageHandler<PacketPrimeInventoryPanelRemote, IMessage> {
    
    @Override
    public IMessage onMessage(PacketPrimeInventoryPanelRemote message, MessageContext ctx) {
      ItemRemoteInvAccess.targetTEtime = EnderIO.proxy.getTickCount() + 10;
      TileInventoryPanel te = new TileInventoryPanel();
      Reader.read(BaseHandlers.REGISTRY, NBTAction.CLIENT, message.tag, te);
      ItemRemoteInvAccess.targetTE = te;
      System.out.println("Got Prime Packet with " + te);
      return null;
    }
  }
}
