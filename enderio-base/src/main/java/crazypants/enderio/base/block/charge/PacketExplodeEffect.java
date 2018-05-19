package crazypants.enderio.base.block.charge;

import com.enderio.core.common.util.NullHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketExplodeEffect implements IMessage {

  double x;
  double y;
  double z;
  ICharge charge;

  public PacketExplodeEffect() {
  }

  public PacketExplodeEffect(Entity ent, ICharge charge) {
    x = ent.posX;
    y = ent.posY;
    z = ent.posZ;
    this.charge = charge;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeDouble(x);
    buf.writeDouble(y);
    buf.writeDouble(z);
    buf.writeInt(charge.getID());

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    x = buf.readDouble();
    y = buf.readDouble();
    z = buf.readDouble();
    int chargeID = buf.readInt();
    charge = ChargeRegister.instance.getCharge(chargeID);
  }

  public static class Handler implements IMessageHandler<PacketExplodeEffect, IMessage> {
    @Override
    public IMessage onMessage(PacketExplodeEffect message, MessageContext ctx) {
      EntityPlayer player = NullHelper.untrust(Minecraft.getMinecraft().player);
      if (message.charge != null && player != null) {
        message.charge.explodeEffect(player.world, message.x, message.y, message.z);
      }
      return null;
    }
  }

}
