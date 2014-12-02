package crazypants.enderio.machine.capbank.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.TileCapBank;

public class PacketNetworkIdResponse implements IMessage, IMessageHandler<PacketNetworkIdResponse, IMessage> {

  private int id;
  private int x;
  private int y;
  private int z;

  public PacketNetworkIdResponse() {
  }

  PacketNetworkIdResponse(TileCapBank capBank) {
    if(capBank != null && capBank.getNetwork() != null) {
      id = capBank.getNetwork().getId();
    } else {
      id = -1;
    }
    x = capBank.xCoord;
    y = capBank.yCoord;
    z = capBank.zCoord;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(id);
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    id = buf.readInt();
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketNetworkIdResponse message, MessageContext ctx) {
    World w = EnderIO.proxy.getClientWorld();
    if(w != null) {
      TileEntity te = w.getTileEntity(message.x, message.y, message.z);
      if(te instanceof TileCapBank) {
        ((TileCapBank) te).setNetworkId(message.id);
      }
    }
    return null;
  }

}
