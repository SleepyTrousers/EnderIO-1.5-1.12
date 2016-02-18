package crazypants.enderio.enderface;

import crazypants.enderio.network.PacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenServerGUI implements IMessage, IMessageHandler<PacketOpenServerGUI, IMessage> {

  int x;
  int y;
  int z;
  EnumFacing side;
  Vec3 hitVec;

  public PacketOpenServerGUI() {
  }

  public PacketOpenServerGUI(int x, int y, int z, EnumFacing side, Vec3 hitVec) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.side = side;
    this.hitVec = hitVec;
  }

  @Override
  public void toBytes(ByteBuf buffer) {
    buffer.writeInt(x);
    buffer.writeInt(y);
    buffer.writeInt(z);
    buffer.writeInt(side.ordinal());
    buffer.writeDouble(hitVec.xCoord);
    buffer.writeDouble(hitVec.yCoord);
    buffer.writeDouble(hitVec.zCoord);
  }

  @Override
  public void fromBytes(ByteBuf buffer) {
    x = buffer.readInt();
    y = buffer.readInt();
    z = buffer.readInt();
    side = EnumFacing.VALUES[buffer.readInt()];
    hitVec = new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
  }

  @Override
  public IMessage onMessage(PacketOpenServerGUI message, MessageContext ctx) {
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;
    Container c = player.openContainer;
  
    PacketHandler.INSTANCE.sendTo(new PacketLockClientContainer(player.openContainer.windowId), player);
    Vec3 hitVec = message.hitVec;
    player.theItemInWorldManager.activateBlockOrUseItem(player, player.worldObj, null, new BlockPos(message.x, message.y, message.z), message.side, (float) hitVec.xCoord,
        (float) hitVec.yCoord, (float) hitVec.zCoord);
    player.theItemInWorldManager.thisPlayerMP = player;
    if (c != player.openContainer) {
      EnderIOController.INSTANCE.addContainer(player, player.openContainer);
    } else {
      PacketHandler.INSTANCE.sendTo(new PacketLockClientContainer(), player);
    }
    return null;
  }
}
