package crazypants.enderio.machines.machine.teleport.telepad.packet;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.config.config.TelePadConfig;
import crazypants.enderio.machines.machine.teleport.telepad.TileTelePad;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSetTarget extends MessageTileEntity<TileEntity> {

  public PacketSetTarget() {
    super();
  }

  private TelepadTarget target;

  public PacketSetTarget(@Nonnull TileTelePad te, @Nonnull TelepadTarget target) {
    super(te.getTileEntity());
    this.target = target;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    NBTTagCompound nbt = new NBTTagCompound();
    target.writeToNBT(nbt);
    ByteBufUtils.writeTag(buf, nbt);

  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    NBTTagCompound nbt = ByteBufUtils.readTag(buf);
    target = nbt != null ? TelepadTarget.readFromNBT(nbt) : null;
  }

  public static class HandlerServer implements IMessageHandler<PacketSetTarget, IMessage> {
    @Override
    public IMessage onMessage(PacketSetTarget message, MessageContext ctx) {
      TileEntity te = message.getTileEntity(message.getWorld(ctx));
      if (te instanceof TileTelePad) {
        TileTelePad tp = (TileTelePad) te;
        if (!TelePadConfig.telepadLockCoords.get()
            && (!TelePadConfig.telepadLockDimension.get() || message.target == null || message.target.getDimension() == tp.getTargetDim())) {
          tp.setTarget(message.target);
        }
        return new PacketSetTarget(tp, tp.getTarget());
      }
      return null;
    }
  }

  public static class HandlerClient implements IMessageHandler<PacketSetTarget, IMessage> {
    @Override
    public IMessage onMessage(PacketSetTarget message, MessageContext ctx) {
      TileEntity te = message.getTileEntity(message.getWorld(ctx));
      if (te instanceof TileTelePad) {
        TileTelePad tp = (TileTelePad) te;
        tp.setTarget(message.target);
      }
      return null;
    }
  }

}
