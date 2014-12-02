package crazypants.enderio.machine.capbank.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.machine.capbank.TileCapBank;

public class PacketClientStateRequest implements IMessage, IMessageHandler<PacketClientStateRequest, PacketClientStateResponse> {

  private int x;
  private int y;
  private int z;

  public PacketClientStateRequest() {
  }

  public PacketClientStateRequest(TileCapBank capBank) {
    x = capBank.xCoord;
    y = capBank.yCoord;
    z = capBank.zCoord;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
  }

  @Override
  public PacketClientStateResponse onMessage(PacketClientStateRequest message, MessageContext ctx) {
    EntityPlayer player = ctx.getServerHandler().playerEntity;
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileCapBank) {
      return new PacketClientStateResponse(((TileCapBank) te).getNetwork());
    }
    return null;
  }
}
