package crazypants.enderio.machines.machine.sagmill;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGrindingBall extends MessageTileEntity<TileSagMill> implements IMessage, IMessageHandler<PacketGrindingBall, IMessage> {

  int currGbUse;
  int maxGbUse;

  public PacketGrindingBall() {
  }

  public PacketGrindingBall(@Nonnull TileSagMill ent) {
    super(ent);
    currGbUse = ent.grindingBallDurabilityUsed;
    maxGbUse = ent.grindingBall == null ? 0 : ent.grindingBall.getDurability();
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeInt(currGbUse);
    buf.writeInt(maxGbUse);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    currGbUse = buf.readInt();
    maxGbUse = buf.readInt();
  }

  @Override
  public IMessage onMessage(PacketGrindingBall message, MessageContext ctx) {
    EntityPlayer player = EnderIO.proxy.getClientPlayer();
    TileSagMill te = message.getTileEntity(player.world);
    if (te != null) {
      te.grindingBallDurabilityUsed = message.currGbUse;
      te.grindingBallDurabilityMax = message.maxGbUse;
    }
    return null;
  }

}
