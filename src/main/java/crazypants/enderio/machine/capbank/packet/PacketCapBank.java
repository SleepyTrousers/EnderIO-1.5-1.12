package crazypants.enderio.machine.capbank.packet;

import com.enderio.core.common.network.MessageTileEntity;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.TileCapBank;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public abstract class PacketCapBank<T extends PacketCapBank<?, ?>, Q extends IMessage> extends MessageTileEntity<TileCapBank> implements IMessageHandler<T, Q> {

  public PacketCapBank() {
    super();
  }

  public PacketCapBank(TileCapBank capBank) {
    super(capBank);
  }

  @Override
  public Q onMessage(T message, MessageContext ctx) {
    TileCapBank te = message.getTileEntity(message.getWorld(ctx));
    if (te == null) {
      return null;
    }
    return handleMessage(te, message, ctx);
  }

  protected abstract Q handleMessage(TileCapBank te, T message, MessageContext ctx);

  @Override
  protected World getWorld(MessageContext ctx) {
    if (ctx.side == Side.SERVER) {
      return ctx.getServerHandler().playerEntity.world;
    } else {
      return EnderIO.proxy.getClientWorld();
    }
  }

}
