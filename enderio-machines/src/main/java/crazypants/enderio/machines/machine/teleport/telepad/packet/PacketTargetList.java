package crazypants.enderio.machines.machine.teleport.telepad.packet;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.machines.machine.teleport.telepad.TileDialingDevice;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTargetList extends MessageTileEntity<TileDialingDevice> {

  private boolean isAdd;
  private TelepadTarget target;

  public PacketTargetList() {
  }

  public PacketTargetList(@Nonnull TileDialingDevice tileDialingDevice, TelepadTarget target, boolean isAdd) {
    super(tileDialingDevice);
    this.isAdd = isAdd;
    this.target = target;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeBoolean(isAdd);
    NBTTagCompound nbt = new NBTTagCompound();
    target.writeToNBT(nbt);
    ByteBufUtils.writeTag(buf, nbt);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    isAdd = buf.readBoolean();
    NBTTagCompound nbt = ByteBufUtils.readTag(buf);
    target = nbt != null ? TelepadTarget.readFromNBT(nbt) : null;
  }

  public static class Handler implements IMessageHandler<PacketTargetList, IMessage> {

    @Override
    public IMessage onMessage(PacketTargetList message, MessageContext ctx) {
      TileDialingDevice te = message.getTileEntity(ctx.side.isClient() ? EnderIO.proxy.getClientWorld() : message.getWorld(ctx));
      if (te != null) {
        if (message.isAdd) {
          te.addTarget(message.target);
        } else {
          te.removeTarget(message.target);
        }
      }
      return null;
    }
  }

}
