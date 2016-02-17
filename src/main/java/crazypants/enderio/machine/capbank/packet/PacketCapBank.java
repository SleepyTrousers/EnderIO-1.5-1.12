package crazypants.enderio.machine.capbank.packet;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.TileCapBank;
import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class PacketCapBank<T extends PacketCapBank, Q extends IMessage> implements IMessage, IMessageHandler<T, Q> {

  private int x;
  private int y;
  private int z;

  public PacketCapBank() {
  }

  public PacketCapBank(TileCapBank capBank) {
    x = capBank.getPos().getX();
    y = capBank.getPos().getY();
    z = capBank.getPos().getZ();
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

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getZ() {
    return z;
  }

  public void setZ(int z) {
    this.z = z;
  }

  @Override
  public Q onMessage(T message, MessageContext ctx) {

    TileCapBank te = getTileEntity(message, ctx);
    if(te == null) {

      return null;
    }
    return handleMessage(te, message, ctx);
  }

  protected abstract Q handleMessage(TileCapBank te, T message, MessageContext ctx);

  protected TileCapBank getTileEntity(T message, MessageContext ctx) {
    World worldObj = getWorld(ctx);
    if(worldObj == null) {
      return null;
    }
    TileEntity te = worldObj.getTileEntity(new BlockPos(message.getX(), message.getY(), message.getZ()));
    if(te == null) {
      return null;
    }
    if(te instanceof TileCapBank) {
      return (TileCapBank) te;
    }
    return null;
  }

  protected World getWorld(MessageContext ctx) {
    if(ctx.side == Side.SERVER) {
      return ctx.getServerHandler().playerEntity.worldObj;
    } else {
      return EnderIO.proxy.getClientWorld();
    }
  }

}
