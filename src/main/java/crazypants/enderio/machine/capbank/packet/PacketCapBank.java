package crazypants.enderio.machine.capbank.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.TileCapBank;

public abstract class PacketCapBank<T extends PacketCapBank<?, ?>, Q extends IMessage> implements IMessage, IMessageHandler<T, Q> {

  private long pos;

  public PacketCapBank() {
  }

  public PacketCapBank(TileCapBank capBank) {
    pos = capBank.getPos().toLong();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    buf.writeLong(pos);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    pos = buf.readLong();
  }

  public BlockPos getPos() {
    return BlockPos.fromLong(pos);
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
    World world = getWorld(ctx);
    if(world == null) {
      return null;
    }
    TileEntity te = world.getTileEntity(message.getPos());
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
      return ctx.getServerHandler().playerEntity.world;
    } else {
      return EnderIO.proxy.getClientWorld();
    }
  }

}
