package crazypants.enderio.machine.crusher;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.EnderIO;

public class PacketGrindingBall implements IMessage, IMessageHandler<PacketGrindingBall, IMessage> {

  private int x;
  private int y;
  private int z;

  int currGbUse;

  public PacketGrindingBall() {
  }

  public PacketGrindingBall(TileCrusher ent) {
    x = ent.xCoord;
    y = ent.yCoord;
    z = ent.zCoord;
    currGbUse = ent.currGbUse;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);
    buf.writeInt(currGbUse);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();
    currGbUse = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketGrindingBall message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
    if(te instanceof TileCrusher) {
      TileCrusher me = (TileCrusher) te;
      me.currGbUse = message.currGbUse;
    }
    return null;
  }

}
