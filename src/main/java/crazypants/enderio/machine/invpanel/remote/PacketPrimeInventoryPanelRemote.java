package crazypants.enderio.machine.invpanel.remote;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.NetworkUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.invpanel.TileInventoryPanel;
import info.loenwind.autosave.Reader;
import info.loenwind.autosave.Writer;
import info.loenwind.autosave.annotations.Store.StoreFor;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPrimeInventoryPanelRemote implements IMessage, IMessageHandler<PacketPrimeInventoryPanelRemote, IMessage> {

  private NBTTagCompound tag;

  public PacketPrimeInventoryPanelRemote() {
  }

  public PacketPrimeInventoryPanelRemote(@Nonnull TileInventoryPanel te) {
    tag = new NBTTagCompound();
    Writer.write(tag, te);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    tag = NetworkUtil.readNBTTagCompound(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    NetworkUtil.writeNBTTagCompound(tag, buf);
  }

  @Override
  public IMessage onMessage(PacketPrimeInventoryPanelRemote message, MessageContext ctx) {
    ClientRemoteGuiManager.targetTEtime = EnderIO.proxy.getTickCount() + 10;
    TileInventoryPanel te = new TileInventoryPanel();
    Reader.read(StoreFor.CLIENT, message.tag, te);
    ClientRemoteGuiManager.targetTE = te;
    return null;
  }

}
