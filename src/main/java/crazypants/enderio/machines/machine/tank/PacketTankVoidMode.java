package crazypants.enderio.machines.machine.tank;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.util.NullHelper;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTankVoidMode extends MessageTileEntity<TileTank> implements IMessageHandler<PacketTankVoidMode, IMessage> {

  public PacketTankVoidMode() {
  }

  private @Nonnull VoidMode mode = VoidMode.NEVER;

  public PacketTankVoidMode(@Nonnull TileTank tank) {
    super(tank);
    this.mode = tank.getVoidMode();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeByte(mode.ordinal());
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    mode = NullHelper.first(VoidMode.values()[MathHelper.clamp(buf.readByte(), 0, VoidMode.values().length - 1)], mode);
  }

  @Override
  public IMessage onMessage(PacketTankVoidMode message, MessageContext ctx) {
    TileTank te = message.getTileEntity(ctx.getServerHandler().player.world);
    if (te != null) {
      te.setVoidMode(message.mode);
    }
    return null;
  }
}
