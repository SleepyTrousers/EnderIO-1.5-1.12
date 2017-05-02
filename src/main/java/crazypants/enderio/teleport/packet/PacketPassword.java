package crazypants.enderio.teleport.packet;

import com.enderio.core.common.TileEntityBase;
import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.api.teleport.ITravelAccessable;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPassword extends MessageTileEntity<TileEntityBase> {

  private ItemStack stack;
  private int slot;
  private boolean setLabel;

  public PacketPassword() {
  }

  private PacketPassword(TileEntityBase tile) {
    super(tile);
  }

  public static IMessage setPassword(TileEntityBase te, int slot, ItemStack stack) {
    PacketPassword msg = new PacketPassword(te);
    msg.slot = slot;
    msg.stack = stack;
    msg.setLabel = false;
    return msg;
  }

  public static PacketPassword setLabel(TileEntityBase te, ItemStack stack) {
    PacketPassword msg = new PacketPassword(te);
    msg.slot = 0;
    msg.stack = stack;
    msg.setLabel = true;
    return msg;
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    slot = buf.readShort();
    setLabel = buf.readBoolean();
    stack = ByteBufUtils.readItemStack(buf);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(slot);
    buf.writeBoolean(setLabel);
    ByteBufUtils.writeItemStack(buf, stack);
  }

  public static class Handler implements IMessageHandler<PacketPassword, IMessage> {

    @Override
    public IMessage onMessage(PacketPassword msg, MessageContext ctx) {
      TileEntityBase te = msg.getTileEntity(ctx.getServerHandler().playerEntity.world);
      if (te instanceof ITravelAccessable) {
        if (((ITravelAccessable) te).canUiBeAccessed(ctx.getServerHandler().playerEntity)) {
          if (msg.stack != null) {
            msg.stack.stackSize = 0;
          }
          if (msg.setLabel) {
            ((ITravelAccessable) te).setItemLabel(msg.stack);
          } else {
            ((ITravelAccessable) te).getPassword()[msg.slot] = msg.stack;
            ((ITravelAccessable) te).clearAuthorisedUsers();
          }
          
          BlockPos pos = msg.getPos();
          IBlockState bs = te.getWorld().getBlockState(pos);
          te.getWorld().notifyBlockUpdate(pos, bs, bs, 3);          
        }
      }
      return null;
    }
  }

}
