package crazypants.enderio.machine.buffer;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.enderio.core.common.network.MessageTileEntity;

public class PacketBufferIO extends MessageTileEntity<TileBuffer> implements IMessageHandler<PacketBufferIO, IMessage>, Runnable {

  public PacketBufferIO() {}
  
  private int in, out;
  private MessageContext _ctx;
  
  public PacketBufferIO(TileBuffer tile, int in, int out) {
    super(tile);
    tile.setIO(in, out);
    this.in = in;
    this.out = out;
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    this.in = buf.readInt();
    this.out = buf.readInt();
  }
  
  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(this.in);
    buf.writeInt(this.out);
  }

  @Override
  public IMessage onMessage(PacketBufferIO message, MessageContext ctx) {
    message._ctx = ctx;
    Minecraft.getMinecraft().addScheduledTask(message);
    return null;
  }

  @Override
  public void run() {
    TileBuffer buf = getTileEntity(getWorld(_ctx));
    if (buf != null) {
      buf.setIO(in, out);
    }
  }

}
